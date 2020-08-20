package jb.game.suguru;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

class ImportLib implements Runnable {
    WeakReference<MainSuguru> mRefMain;
    private Context mContext;
    private Uri mLib;
    private Data mData;

    ImportLib(MainSuguru pMain, Context pContext, Uri pLib){
        mRefMain = new WeakReference<>(pMain);
        mContext = pContext;
        mLib = pLib;
    }

    @Override
    public void run() {
        MainSuguru lMain;
        InputStream lInStream;
        BufferedReader lBuffer = null;
        String lLine;
        LibGame lGame;

        mData = Data.getInstance(mContext);
        try{
            lInStream = mContext.getContentResolver().openInputStream(mLib);
            lBuffer = new BufferedReader(new InputStreamReader(lInStream));
            lLine = lBuffer.readLine();
            while (lLine != null){
                lGame = new LibGame(lLine);
                if (lGame.xValid()){
                    mData.xStoreLibGame(lGame);
                }
                lLine = lBuffer.readLine();
            }
        } catch (IOException pExc){
        }
        if (lBuffer != null){
            try {
                lBuffer.close();
            } catch (IOException pExc){
            }
        }

        lMain = mRefMain.get();
        if (lMain != null) {
            lMain.xFinishImport();
        }
    }
}
