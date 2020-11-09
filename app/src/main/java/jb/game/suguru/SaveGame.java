package jb.game.suguru;

import android.content.Context;

class SaveGame implements Runnable {
    private final Context mContext;
    private final SuguruGameBase mGame;

    SaveGame (Context pContext, SuguruGameBase pGame){
        mContext = pContext.getApplicationContext();
        mGame = new SuguruGameBase(pGame);
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
