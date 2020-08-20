package jb.game.suguru;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.Instant;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class MainSuguru extends Activity {
    private final Context mContext = this;
    private SuguruGame mGame;
    private Data mData;
    private SuguruView mSgrView;
    private Bundle mGameParams = null;
    private Bundle mStoreParams = null;
    private Uri mLoadUri = null;
    private long mStartTime;
    private volatile boolean mLoadActive;

    Handler mRefreshHandler = new Handler();
    Runnable mRefreshRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            StringBuilder lBuilder;
            int lCount;
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
                    setTitle(getString(R.string.app_name) + String.format(" %02d:%02d", lMinute, lSecond));
                    mRefreshHandler.postDelayed(this, 100);
                    break;
                }
                case SuguruGame.cStatusSolved: {
                    lElapsed = mGame.xUsedTime();
                    lMinute = lElapsed / 60;
                    lSecond = lElapsed % 60;
                    setTitle(getString(R.string.app_name) + String.format(" %02d:%02d", lMinute, lSecond));
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

        mGame = new SuguruGame();
        mData = Data.getInstance(mContext);
        mSgrView = findViewById(R.id.svMain);
        mSgrView.setGame(mGame);
        mSgrView.setIntSuguruView(new SuguruView.intSuguruView() {
            @Override
            public void onSolved() {
                sSaveUsedTime();
                Toast.makeText(mContext, R.string.msg_solved, Toast.LENGTH_SHORT).show();
            }
        });
        if (savedInstanceState == null) {
            mLoadActive = false;
        } else {
            mLoadActive = savedInstanceState.getBoolean("LoadActive");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("LoadActive", mLoadActive);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        int lRows;
        int lColumns;
        int lMaxValue;
        int lDifficulty;

        super.onResume();

        mGame = mData.xCurrentGame();
        mSgrView.setGame(mGame);
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRefreshHandler.removeCallbacks(mRefreshRunnable);
        if (mGame.xGameStatus() == SuguruGame.cStatusPlay) {
            sSaveUsedTime();
        }
        mData.xSaveGame(mGame);
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

        List<PlayField> lFields;

        MenuItem lMnuNew;
        MenuItem lMnuSetup;
        MenuItem lMnuSetupStart;
        MenuItem lMnuSetupFinish;
        MenuItem lMnuStore;
        MenuItem lMnuPencil;
        MenuItem lMnuPencilAuto;
        MenuItem lMnuPlayField;
        MenuItem lMnuLibrary;

        lMnuNew = pMenu.findItem(R.id.mnuNew);
        lMnuSetup = pMenu.findItem(R.id.mnuSetup);
        lMnuSetupStart = pMenu.findItem(R.id.mnuSetupStart);
        lMnuSetupFinish = pMenu.findItem(R.id.mnuSetupFinish);
        lMnuStore = pMenu.findItem(R.id.mnuStore);
        lMnuPencil = pMenu.findItem(R.id.mnuPencil);
        lMnuPencilAuto = pMenu.findItem(R.id.mnuPencilAuto);
        lMnuPlayField = pMenu.findItem(R.id.mnuFields);
        lMnuLibrary = pMenu.findItem(R.id.mnuLib);

        lMnuNew.setEnabled(true);
        lMnuSetup.setEnabled(true);
        lMnuSetupStart.setEnabled(true);
        lMnuLibrary.setEnabled(!mLoadActive);
        switch (mGame.xGameStatus()) {
            case SuguruGame.cStatusSetupGroups: {
                lMnuSetupFinish.setEnabled(false);
                lMnuStore.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuPlayField.setEnabled(false);
                break;
            }
            case SuguruGame.cStatusSetupValues: {
                lMnuSetupFinish.setEnabled(true);
                lMnuStore.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuPlayField.setEnabled(false);
                break;
            }
            case SuguruGame.cStatusPlay: {
                lMnuSetupFinish.setEnabled(false);
                if (mGame.xLib()) {
                    lMnuStore.setEnabled(false);
                } else {
                    lMnuStore.setEnabled(true);
                }
                lMnuPencil.setEnabled(true);
                lMnuPencilAuto.setChecked(mGame.xPencilAuto());
                lMnuPlayField.setEnabled(true);
                break;
            }
            case SuguruGame.cStatusSolved: {
                lMnuSetupFinish.setEnabled(false);
                if (mGame.xLib()) {
                    lMnuStore.setEnabled(false);
                } else {
                    lMnuStore.setEnabled(true);
                }
                lMnuPencil.setEnabled(false);
                lMnuPlayField.setEnabled(false);
                break;
            }
            default: {
                lMnuSetupFinish.setEnabled(false);
                lMnuStore.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuPlayField.setEnabled(false);
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
                case 1:
                    mGameParams = pInt.getExtras();
                    break;
                case 2:
                    mStoreParams = pInt.getExtras();
                    break;
                case 3:
                    lUri = pInt.getData();
                    lImportRunnable = new ImportLib(this, this.getApplicationContext(), lUri);
                    lAsync = new Thread(lImportRunnable);
                    lAsync.start();
                    break;
            }
        }
    }

    void xFinishImport(){
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

    public void hNew(MenuItem pItem) {
        int lItem;
        int lDifficulty;
        LibGame lLibGame;

        lItem = pItem.getItemId();
        switch (lItem) {
            case R.id.mnuNewVE:
                lDifficulty = 1;
                break;
            case R.id.mnuNewE:
                lDifficulty = 2;
                break;
            case R.id.mnuNewM:
                lDifficulty = 3;
                break;
            case R.id.mnuNewH:
                lDifficulty = 4;
                break;
            case R.id.mnuNewVH:
                lDifficulty = 5;
                break;
            default:
                lDifficulty = 0;
                break;
        }
        lLibGame = mData.xRandomLibGame(lDifficulty);
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
        startActivityForResult(lInt, 1);
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
        startActivityForResult(lInt, 2);
    }

    private void sStore(int pLevel) {
        mGame.xToLib();
        mData.xLibGame(mGame, pLevel);
        Toast.makeText(mContext, R.string.msg_game_stored, Toast.LENGTH_SHORT).show();
    }

    public void hAutoPencil(MenuItem pItem) {
        mGame.xFlipPencilAuto();
//        mSgrView.invalidate();
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
        mData.xSavePlayField(mGame.xPlayField());
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
        mData.xSavePlayField(mGame.xPlayField());
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
        lIntent.setType("application/sgl");

        startActivityForResult(lIntent, 3);
    }
}
