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
    private final Context mContext;
    private final Uri mLib;

    ImportLib(MainSuguru pMain, Context pContext, Uri pLib){
        mRefMain = new WeakReference<>(pMain);
        mContext = pContext;
        mLib = pLib;
    }

    @Override
    public void run() {
        Data lData;
        MainSuguru lMain;
        InputStream lInStream;
        BufferedReader lBuffer = null;
        String lLine;
        LibGame lGame;

        lData = Data.getInstance(mContext);
        try{
            lData.xStartImportGame();
            lInStream = mContext.getContentResolver().openInputStream(mLib);
            lBuffer = new BufferedReader(new InputStreamReader(lInStream));
            lLine = lBuffer.readLine();
            while (lLine != null){
                lGame = new LibGame(lLine);
                if (lGame.xValid()){
                    lData.xStoreImportGame(lGame);
                }
                lLine = lBuffer.readLine();
            }
        } catch (IOException ignored){
        } finally {
            lData.xEndImportGame();
        }
        if (lBuffer != null){
            try {
                lBuffer.close();
            } catch (IOException ignored){
            }
        }

        lMain = mRefMain.get();
        if (lMain != null) {
            lMain.xFinishImport();
        }
    }
}
