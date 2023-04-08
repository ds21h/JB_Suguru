package jb.game.suguru;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.threeten.bp.Instant;

import java.util.List;

public class MainSuguru extends Activity {
    private final int cReqSetup = 1;
    private final int cReqStore = 2;
    private final int cReqImport = 3;
    private final int cReqChangeDiff = 4;

    private final Context mContext = this;
    private SuguruGame mGame;
    private Data mData;
    private SuguruView mSgrView;
    private Bundle mGameParams = null;
    private Bundle mStoreParams = null;
    private Bundle mSelectDiffParams = null;
    private long mStartTime;
    private volatile boolean mLoadActive;
    private boolean mSelectNew;
    private LibData mLibData;
    private String mHeader;

    Handler mRefreshHandler = new Handler(Looper.getMainLooper());
    Runnable mRefreshRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            Instant lInstant;
            long lNowTime;
            int lElapsed;
            int lMinute;
            int lSecond;

            switch (mGame.xGameStatus()) {
                case SuguruGame.cStatusPlay: {
                    lInstant = Instant.now();
                    lNowTime = lInstant.getEpochSecond();
                    lElapsed = (int) (lNowTime - mStartTime);
                    lMinute = lElapsed / 60;
                    lSecond = lElapsed % 60;
                    setTitle(mHeader + String.format(" %02d:%02d", lMinute, lSecond));
                    mRefreshHandler.postDelayed(this, 100);
                    break;
                }
                case SuguruGame.cStatusSolved: {
                    lElapsed = mGame.xUsedTime();
                    lMinute = lElapsed / 60;
                    lSecond = lElapsed % 60;
                    setTitle(mHeader + String.format(" %02d:%02d", lMinute, lSecond));
                    mRefreshHandler.postDelayed(this, 500);
                    break;
                }
                case SuguruGame.cStatusSetupGroups: {
                    setTitle(getString(R.string.app_name) + " - " + getString(R.string.hd_setup_groups));
                    mRefreshHandler.postDelayed(this, 500);
                    break;
                }
                case SuguruGame.cStatusSetupValues: {
                    setTitle(getString(R.string.app_name) + " - " + getString(R.string.hd_setup_values));
                    mRefreshHandler.postDelayed(this, 500);
                    break;
                }
                default: {
                    setTitle(getString(R.string.app_name));
                    mRefreshHandler.postDelayed(this, 500);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainsuguru_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String lLibData;

        mGame = new SuguruGame();
        mData = Data.getInstance(mContext);
        mHeader = "";
        mSgrView = findViewById(R.id.svMain);
        mSgrView.setGame(mGame);
        mSgrView.setIntSuguruView(new SuguruView.intSuguruView() {
            @Override
            public void onSolved() {
                if (mGame.xBatchId() >= 0){
                    if (!mGame.xSetLibSolved()){
                        mLibData.xSolved(mGame.xDifficulty());
                    }
                    mData.xLibGamePlayed(mGame.xBatchId(), mGame.xGameId());
                }
                sSaveUsedTime();
                Toast.makeText(mContext, R.string.msg_solved, Toast.LENGTH_SHORT).show();
            }
        });
        if (savedInstanceState == null) {
            mLoadActive = false;
            mSelectNew = true;
            mLibData = new LibData(mContext);
        } else {
            mLoadActive = savedInstanceState.getBoolean("LoadActive");
            mSelectNew = savedInstanceState.getBoolean("SelectNew");
            lLibData = savedInstanceState.getString("LibData");
            mLibData = new LibData(lLibData);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("LoadActive", mLoadActive);
        outState.putBoolean("SelectNew", mSelectNew);
        outState.putString("LibData", mLibData.xLibData());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart(){
        super.onStart();

        mGame = mData.xCurrentGame();
    }

    @Override
    protected void onResume() {
        int lRows;
        int lColumns;
        int lMaxValue;
        int lDifficulty;

        super.onResume();

        mSgrView.setGame(mGame);
        sSetHeader();
        if (mGame.xGameStatus() == SuguruGame.cStatusPlay) {
            sSetStartTime();
            mStartTime -= mGame.xUsedTime();
            mGame.xResetUsedTime();
        }
        mRefreshHandler.postDelayed(mRefreshRunnable, 10);
        if (mGameParams != null) {
            lRows = mGameParams.getInt(SelectGameParams.cRows);
            lColumns = mGameParams.getInt(SelectGameParams.cColumns);
            lMaxValue = mGameParams.getInt(SelectGameParams.cMaxValue);
            mGameParams = null;
            sSetupStart(lRows, lColumns, lMaxValue);
        }
        if (mStoreParams != null) {
            lDifficulty = mStoreParams.getInt(SelectDifficulty.cLevel);
            mStoreParams = null;
            sStore(lDifficulty);
        }
        if (mSelectDiffParams != null) {
            lDifficulty = mSelectDiffParams.getInt(SelectDifficulty.cLevel);
            mSelectDiffParams = null;
            sChangeDifficulty(lDifficulty);
        }
    }

    @Override
    protected void onPause() {
        mRefreshHandler.removeCallbacks(mRefreshRunnable);
        if (mGame.xGameStatus() == SuguruGame.cStatusPlay) {
            sSaveUsedTime();
        }

        super.onPause();
    }

    @Override
    protected void onStop(){
        SaveGame lSaveGame;
        Thread lThread;

        lSaveGame = new SaveGame(mContext, mGame);
        lThread = new Thread(lSaveGame);
        lThread.start();

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        super.onCreateOptionsMenu(pMenu);
        getMenuInflater().inflate(R.menu.suguru_menu, pMenu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu pMenu) {
        super.onPrepareOptionsMenu(pMenu);

        MenuItem lMnuUndo;
        MenuItem lMnuNew;
        MenuItem lMenuVE;
        MenuItem lMenuE;
        MenuItem lMenuM;
        MenuItem lMenuH;
        MenuItem lMenuVH;
        MenuItem lMnuSelectNew;
        MenuItem lMnuSetup;
        MenuItem lMnuSetupStart;
        MenuItem lMnuSetupFinish;
        MenuItem lMnuStore;
        MenuItem lMnuPencil;
        MenuItem lMnuPencilAuto;
        MenuItem lMnuPlayField;
        MenuItem lMnuLibrary;
        MenuItem lMnuChangeDiff;

        lMnuUndo = pMenu.findItem(R.id.mnuUndo);
        lMnuNew = pMenu.findItem(R.id.mnuNew);
        lMenuVE = pMenu.findItem(R.id.mnuNewVE);
        lMenuE = pMenu.findItem(R.id.mnuNewE);
        lMenuM = pMenu.findItem(R.id.mnuNewM);
        lMenuH = pMenu.findItem(R.id.mnuNewH);
        lMenuVH = pMenu.findItem(R.id.mnuNewVH);
        lMnuSelectNew = pMenu.findItem(R.id.mnuSelectNew);
        lMnuSetup = pMenu.findItem(R.id.mnuSetup);
        lMnuSetupStart = pMenu.findItem(R.id.mnuSetupStart);
        lMnuSetupFinish = pMenu.findItem(R.id.mnuSetupFinish);
        lMnuStore = pMenu.findItem(R.id.mnuStore);
        lMnuPencil = pMenu.findItem(R.id.mnuPencil);
        lMnuPencilAuto = pMenu.findItem(R.id.mnuPencilAuto);
        lMnuPlayField = pMenu.findItem(R.id.mnuFields);
        lMnuLibrary = pMenu.findItem(R.id.mnuLib);
        lMnuChangeDiff = pMenu.findItem(R.id.mnuChangeDiff);

        lMnuNew.setEnabled(true);
        lMenuVE.setTitle(getString(R.string.mnu_level_very_easy) + " (" + mLibData.xNumber(1, mSelectNew) + ")");
        lMenuE.setTitle(getString(R.string.mnu_level_easy) + " (" + mLibData.xNumber(2, mSelectNew) + ")");
        lMenuM.setTitle(getString(R.string.mnu_level_medium) + " (" + mLibData.xNumber(3, mSelectNew) + ")");
        lMenuH.setTitle(getString(R.string.mnu_level_hard) + " (" + mLibData.xNumber(4, mSelectNew) + ")");
        lMenuVH.setTitle(getString(R.string.mnu_level_very_hard) + " (" + mLibData.xNumber(5, mSelectNew) + ")");
        lMnuSelectNew.setChecked(mSelectNew);
        lMnuSetup.setEnabled(true);
        lMnuSetupStart.setEnabled(true);
        lMnuLibrary.setEnabled(!mLoadActive);

        switch (mGame.xGameStatus()) {
            case SuguruGame.cStatusSetupGroups: {
                lMnuUndo.setEnabled(false);
                lMnuSetupFinish.setEnabled(false);
                lMnuStore.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuPlayField.setEnabled(false);
                lMnuChangeDiff.setEnabled(false);
                break;
            }
            case SuguruGame.cStatusSetupValues: {
                lMnuUndo.setEnabled(false);
                lMnuSetupFinish.setEnabled(true);
                lMnuStore.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuPlayField.setEnabled(false);
                lMnuChangeDiff.setEnabled(false);
                break;
            }
            case SuguruGame.cStatusPlay: {
                lMnuUndo.setEnabled(mGame.xUndoAvail());
                lMnuSetupFinish.setEnabled(false);
                if (mGame.xLib()) {
                    lMnuStore.setEnabled(false);
                } else {
                    lMnuStore.setEnabled(true);
                }
                lMnuPencil.setEnabled(true);
                lMnuPencilAuto.setChecked(mGame.xPencilAuto());
                lMnuPlayField.setEnabled(true);
                lMnuChangeDiff.setEnabled(mGame.xBatchId() >= 0);
                break;
            }
            case SuguruGame.cStatusSolved: {
                lMnuUndo.setEnabled(false);
                lMnuSetupFinish.setEnabled(false);
                if (mGame.xLib()) {
                    lMnuStore.setEnabled(false);
                } else {
                    lMnuStore.setEnabled(true);
                }
                lMnuPencil.setEnabled(false);
                lMnuPlayField.setEnabled(false);
                lMnuChangeDiff.setEnabled(mGame.xBatchId() >= 0);
                break;
            }
            default: {
                lMnuUndo.setEnabled(false);
                lMnuSetupFinish.setEnabled(false);
                lMnuStore.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuPlayField.setEnabled(false);
                lMnuChangeDiff.setEnabled(false);
                break;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int pRequest, int pResult, Intent pInt) {
        Uri lUri;
        ImportLib lImportRunnable;
        Thread lAsync;

        if (pResult == RESULT_OK) {
            switch (pRequest) {
                case cReqSetup:
                    mGameParams = pInt.getExtras();
                    break;
                case cReqStore:
                    mStoreParams = pInt.getExtras();
                    break;
                case cReqImport:
                    lUri = pInt.getData();
                    lImportRunnable = new ImportLib(this, this.getApplicationContext(), lUri);
                    lAsync = new Thread(lImportRunnable);
                    lAsync.start();
                    break;
                case cReqChangeDiff:
                    mSelectDiffParams = pInt.getExtras();
                    break;
            }
        }
    }

    private void sSetHeader() {
        if (mGame.xGameStatus() == SuguruGame.cStatusPlay || mGame.xGameStatus() == SuguruGame.cStatusSolved) {
            switch (mGame.xDifficulty()) {
                case 1: {
                    mHeader = getString(R.string.mnu_level_very_easy);
                    break;
                }
                case 2: {
                    mHeader = getString(R.string.mnu_level_easy);
                    break;
                }
                case 3: {
                    mHeader = getString(R.string.mnu_level_medium);
                    break;
                }
                case 4: {
                    mHeader = getString(R.string.mnu_level_hard);
                    break;
                }
                case 5: {
                    mHeader = getString(R.string.mnu_level_very_hard);
                    break;
                }
                default: {
                    mHeader = getString(R.string.app_name);
                    break;
                }
            }
        } else {
            mHeader = getString(R.string.app_name);
        }
    }

    void xFinishImport(){
        mLibData = new LibData(mContext);
        mLoadActive = false;
    }

    private void sSetStartTime() {
        Instant lInstant;

        lInstant = Instant.now();
        mStartTime = lInstant.getEpochSecond();
    }

    private void sSaveUsedTime() {
        Instant lInstant;

        lInstant = Instant.now();
        mGame.xAddUsedTime((int) (lInstant.getEpochSecond() - mStartTime));
    }

    public void hUndo(MenuItem pItem) {
        mGame.xUndo();
        mSgrView.invalidate();
    }

    public void hSelectNew(MenuItem pItem) {
        mSelectNew = !mSelectNew;
    }

    public void hNew(MenuItem pItem) {
        int lItem;
        int lDifficulty;
        LibGame lLibGame;

        lItem = pItem.getItemId();
        if (lItem == R.id.mnuNewVE) {
            lDifficulty = 1;
        } else if (lItem == R.id.mnuNewE) {
            lDifficulty = 2;
        } else if (lItem == R.id.mnuNewM) {
            lDifficulty = 3;
        } else if (lItem == R.id.mnuNewH) {
            lDifficulty = 4;
        } else if (lItem == R.id.mnuNewVH) {
            lDifficulty = 5;
        } else {
            lDifficulty = 0;
        }
        lLibGame = mData.xRandomLibGame(mSelectNew, lDifficulty);
        if (lLibGame == null) {
            Toast.makeText(mContext, R.string.msg_not_found, Toast.LENGTH_SHORT).show();
        } else {
            mData.xDeleteSave();
            mGame.xNewGame(lLibGame);
            sStartGame();
        }
    }

    public void hSetupStart(MenuItem pItem) {
        Intent lInt;
        Bundle lBundle;

        lBundle = new Bundle();
        lBundle.putInt(SelectGameParams.cRows, mGame.xRows());
        lBundle.putInt(SelectGameParams.cColumns, mGame.xColumns());
        lBundle.putInt(SelectGameParams.cMaxValue, mGame.xMaxValue());
        lInt = new Intent();
        lInt.setClass(this, SelectGameParams.class);
        lInt.putExtras(lBundle);
        startActivityForResult(lInt, cReqSetup);
    }

    private void sSetupStart(int pRows, int pColumns, int pMaxValue) {
        mData.xDeleteSave();
        mGame.xStartSetUp(pRows, pColumns, pMaxValue);
        mSgrView.invalidate();
    }

    public void hSetupFinish(MenuItem pItem) {
        if (mGame.xFinishSetup()) {
            sStartGame();
        }
        mSgrView.invalidate();
    }

    private void sStartGame() {
        sSetStartTime();
        mGame.xStartGame();
        sSetHeader();
        mSgrView.setEnabled(true);
        mSgrView.invalidate();
    }

    public void hReset(MenuItem pItem) {
        mData.xDeleteSave();
        sSetStartTime();
        mGame.xReset();
        mSgrView.invalidate();
    }

    public void hStore(MenuItem pItem) {
        Intent lInt;

        lInt = new Intent();
        lInt.setClass(this, SelectDifficulty.class);
        startActivityForResult(lInt, cReqStore);
    }

    private void sStore(int pLevel) {
        int lGameId;

        lGameId = mData.xStoreLibGame(mGame, pLevel);
        mGame.xToLib(lGameId, pLevel);
        mLibData.xAdded(pLevel);
        Toast.makeText(mContext, R.string.msg_game_stored, Toast.LENGTH_SHORT).show();
    }

    public void hAutoPencil(MenuItem pItem) {
        mGame.xFlipPencilAuto();
    }

    public void hFillPencil(MenuItem pItem) {
        mGame.xFillPencil();
        mSgrView.invalidate();
    }

    public void hClearPencil(MenuItem pItem) {
        mGame.xClearPencil();
        mSgrView.invalidate();
    }

    public void hFieldCopy(MenuItem pItem) {
        SavePlayfield lSavePlayfield;
        Thread lThread;

        lSavePlayfield = new SavePlayfield(mContext, mGame.xPlayField());
        lThread = new Thread(lSavePlayfield);
        lThread.start();
        mGame.xPlayFieldCopy();
        mSgrView.invalidate();
    }

    public void hFieldSwitch(MenuItem pItem) {
        AlertDialog lDialog;
        AlertDialog.Builder lBuilder;
        final String[] lItems;
        List<PlayField> lFields;
        int lCountIn;
        int lCountOut;
        int lId;

        lFields = mGame.xPlayFields();
        lItems = new String[lFields.size() - 1];

        lCountOut = 0;
        for (lCountIn = 0; lCountIn < lFields.size(); lCountIn++){
            lId = lFields.get(lCountIn).xFieldId();
            if (lId != mGame.xPlayField().xFieldId()){
                lItems[lCountOut] = String.valueOf(lId);
                lCountOut++;
            }
        }
        if (lItems.length > 1){
            lBuilder = new AlertDialog.Builder(this);
            lBuilder.setItems(lItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pChoice) {
                    sSwitchPlayField(Integer.parseInt(lItems[pChoice]));
                }
            });
            lDialog = lBuilder.create();
            lDialog.show();
        } else {
            sSwitchPlayField(Integer.parseInt(lItems[0]));
        }
    }

    private void sSwitchPlayField(int pNewId){
        SavePlayfield lSavePlayfield;
        Thread lThread;

        lSavePlayfield = new SavePlayfield(mContext, mGame.xPlayField());
        lThread = new Thread(lSavePlayfield);
        lThread.start();
        mGame.xSwitchPlayField(pNewId);
        mSgrView.invalidate();
    }

    public void hFieldDelete(MenuItem pItem) {
        mData.xDeletePlayField(mGame.xPlayField().xFieldId());
        mGame.xDeleteCurrentPlayField();
        mSgrView.invalidate();
    }

    public void hImport(MenuItem pItem) {
        Intent lIntent;

        lIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        lIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        lIntent.setType("application/sgl");
        lIntent.setType("*/*");

        startActivityForResult(lIntent, cReqImport);
    }

    public void hChangeDifficulty(MenuItem pItem) {
        Intent lInt;
        Bundle lBundle;

        lInt = new Intent();
        lInt.setClass(this, SelectDifficulty.class);
        lBundle = new Bundle();
        lBundle.putInt(SelectDifficulty.cLevel, mGame.xDifficulty());
        lInt.putExtras(lBundle);
        startActivityForResult(lInt, cReqChangeDiff);
    }

    private void sChangeDifficulty(int pDifficulty){
        mLibData.xDeleted(mGame.xDifficulty(), !mGame.xLibSolved());
        mGame.xChangeDifficulty(pDifficulty);
        mData.xLibGameSetDiff(mGame.xBatchId(), mGame.xGameId(), pDifficulty);
        mLibData.xAdded(pDifficulty);
        Toast.makeText(mContext, R.string.msg_difficulty_changed, Toast.LENGTH_SHORT).show();
    }
}
