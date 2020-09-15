package jb.game.suguru;

import android.content.Context;

class SaveGame implements Runnable {
    private Context mContext;
    private SuguruGame mGame;

    SaveGame (Context pContext, SuguruGame pGame){
        mContext = pContext.getApplicationContext();
        mGame = new SuguruGame(pGame);
    }

    @Override
    public void run() {
       Data lData;

       lData = Data.getInstance(mContext);
       synchronized(mContext){
           lData.xSaveGame(mGame);
       }
    }
}
