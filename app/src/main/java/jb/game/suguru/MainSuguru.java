package jb.game.suguru;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainSuguru extends Activity {
    private final Context mContext = this;
    private SuguruGame mGame;
    private Data mData;
    private SuguruView mSgrView;
    private Bundle mGameParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainsuguru_layout);

        mGame = new SuguruGame();
        mData = Data.getInstance(mContext);
        mSgrView = findViewById(R.id.svMain);
        mSgrView.setGame(mGame);
    }

    @Override
    protected void onResume() {
        int lRows;
        int lColumns;
        int lMaxValue;

        super.onResume();

        mGame = mData.xCurrentGame();
        mSgrView.setGame(mGame);
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

        if (pRequest == 1) {
            if (pResult == RESULT_OK) {
                lBundle = pInt.getExtras();
                if (lBundle != null) {
                    mGameParams = lBundle;
                }
            }
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
        mGame.xFinishSetup();
        mSgrView.invalidate();
    }

    public void hReset(MenuItem pItem) {
        mGame.xReset();
        mSgrView.invalidate();
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
