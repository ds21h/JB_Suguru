package jb.game.suguru;

import android.content.Context;

class SavePlayfield implements Runnable {
    private final Context mContext;
    private final PlayField mField;

    SavePlayfield (Context pContext, PlayField pField){
        mContext = pContext.getApplicationContext();
        mField = new PlayField(pField);
    }

    @Override
    public void run() {
        Data lData;

        lData = Data.getInstance(mContext);
        synchronized(mContext){
            lData.xSavePlayField(mField);
        }
    }
}
