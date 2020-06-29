package jb.game.suguru;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;
import org.threeten.bp.Instant;

public class MainSuguru extends Activity {
    private final Context mContext = this;
    private SuguruGame mGame;
    private Data mData;
    private SuguruView mSgrView;
    private Bundle mGameParams = null;
    private long mStartTime;

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
    }

    @Override
    protected void onResume() {
        int lRows;
        int lColumns;
        int lMaxValue;

        super.onResume();

        mGame = mData.xCurrentGame();
        mSgrView.setGame(mGame);
        if (mGame.xGameStatus() == SuguruGame.cStatusPlay) {
            sSetStartTime();
            mStartTime -= mGame.xUsedTime();
            mGame.xResetUsedTime();
        }
        mRefreshHandler.postDelayed(mRefreshRunnable, 10);
        if (mGameParams != null){
            lRows = mGameParams.getInt(SelectGameParams.cRows);
            lColumns = mGameParams.getInt(SelectGameParams.cColumns);
            lMaxValue = mGameParams.getInt(SelectGameParams.cMaxValue);
            mGameParams = null;
            sSetupStart(lRows, lColumns, lMaxValue);
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
    protected void onActivityResult(int pRequest, int pResult, Intent pInt) {
        Bundle lBundle;
        int lDifficulty;

        if (pRequest == 1) {
            if (pResult == RESULT_OK) {
                lBundle = pInt.getExtras();
                if (lBundle != null) {
                    mGameParams = lBundle;
                }
            }
        } else {
            if (pRequest == 2){
                if (pResult == RESULT_OK) {
                    lBundle = pInt.getExtras();
                    if (lBundle != null) {
                        lDifficulty = lBundle.getInt(SelectDifficulty.cLevel);
                        sStore(lDifficulty);
                    }
                }
            }
        }
    }

    private void sSetStartTime() {
        Instant lInstant;

        lInstant = Instant.now();
        mStartTime = lInstant.getEpochSecond();
    }

    private void sSaveUsedTime(){
        Instant lInstant;

        lInstant = Instant.now();
        mGame.xAddUsedTime((int)(lInstant.getEpochSecond() - mStartTime));
    }

    public void hNew(MenuItem pItem) {
        int lItem;
        int lDifficulty;
        LibGame lLibGame;

        lItem = pItem.getItemId();
        switch (lItem){
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
        if (lLibGame != null){
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

    private void sSetupStart(int pRows, int pColumns, int pMaxValue){
        mData.xDeleteSave();
        mGame.xStartSetUp(pRows, pColumns, pMaxValue);
        mSgrView.invalidate();
    }

    public void hSetupFinish(MenuItem pItem) {
        if (mGame.xFinishSetup()){
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
        mData.xLibGame(mGame,  pLevel);
        Toast.makeText(mContext, R.string.msg_game_stored, Toast.LENGTH_SHORT).show();
    }

    public void hFillPencil(MenuItem pItem){
        mGame.xFillPencil();
        mSgrView.invalidate();
    }

    public void hClearPencil(MenuItem pItem){
        mGame.xClearPencil();
        mSgrView.invalidate();
    }
}
