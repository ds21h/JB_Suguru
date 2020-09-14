package jb.game.suguru;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class LibData {
    private int[] mNumberTotal;
    private int[] mNumberNew;

    LibData(Context pContext){
        Data lData;
        int lCount;
        int lResult;

        sInit();

        lData = Data.getInstance(pContext);
        for (lCount = 1; lCount <= 5; lCount++){
            lResult = lData.xCountLib(lCount, false);
            if (lResult >= 0){
                mNumberTotal[lCount - 1] = lResult;
            }
            lResult = lData.xCountLib(lCount, true);
            if (lResult >= 0){
                mNumberNew[lCount - 1] = lResult;
            }
        }
    }

    LibData(String pLibData){
        JSONObject lLibData;
        JSONArray lNumberTotal;
        JSONArray lNumberNew;
        int lCount;

        sInit();
        try {
            lLibData = new JSONObject(pLibData);
            lNumberTotal = lLibData.getJSONArray("NumberTotal");
            lNumberNew = lLibData.getJSONArray("NumberNew");
            for (lCount = 0; lCount < mNumberTotal.length; lCount++){
                mNumberTotal[lCount] = lNumberTotal.optInt(lCount);
                mNumberNew[lCount] = lNumberNew.optInt(lCount);
            }
        } catch (JSONException pExc){
        }
    }

    private void sInit(){
        mNumberTotal = new int[5];
        mNumberNew = new int[5];
    }

    String xLibData(){
        JSONObject lLibData;
        JSONArray lNumberTotal;
        JSONArray lNumberNew;
        int lCount;
        String lResult;

        lLibData = new JSONObject();
        lNumberTotal = new JSONArray();
        lNumberNew = new JSONArray();

        for (lCount = 0; lCount < mNumberTotal.length; lCount++){
            lNumberTotal.put(mNumberTotal[lCount]);
            lNumberNew.put(mNumberNew[lCount]);
        }
        try {
            lLibData.put("NumberTotal", lNumberTotal);
            lLibData.put("NumberNew", lNumberNew);
            lResult = lLibData.toString();
        } catch (JSONException pExc){
            lResult = "";
        }
        return lResult;
    }

    int xNumber(int pDifficulty, boolean pNew){
        int lResult;

        if (pNew){
            lResult = mNumberNew[pDifficulty - 1];
        } else {
            lResult = mNumberTotal[pDifficulty - 1];
        }
        return lResult;
    }

    void xSolved(int pDifficulty){
        mNumberNew[pDifficulty - 1]--;
    }

    void xAdded(int pDifficulty){
        mNumberTotal[pDifficulty - 1]++;
        mNumberNew[pDifficulty - 1]++;
    }

    void xDeleted(int pDifficulty, boolean pNew){
        mNumberTotal[pDifficulty - 1]--;
        if (pNew){
            mNumberNew[pDifficulty - 1]--;
        }
    }

}
