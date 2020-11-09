package jb.game.suguru;

class Group {
    private int[] mCellNrs;
    private int mGroupSize;
    private int mMaxSize;

    Group (){
        sInit();
    }

    Group (Group pGroup){
        int lCount;

        mCellNrs = new int[pGroup.mCellNrs.length];
        for (lCount = 0; lCount < mCellNrs.length; lCount++){
            mCellNrs[lCount] = pGroup.mCellNrs[lCount];
        }
        mGroupSize = pGroup.mGroupSize;
        mMaxSize = pGroup.mMaxSize;
    }

    Group(String pContent){
        int lStart;
        int lCellNr;

        sInit();
        lStart = 0;
        while(lStart < pContent.length()){
            try {
                lCellNr = Integer.parseInt(pContent.substring(lStart, lStart + 3));
                xAdd(lCellNr);
            } catch (NumberFormatException ignored){ }
            lStart += 3;
        }
    }

    private void sInit(){
        mMaxSize = 9;
        mCellNrs = new int[mMaxSize];
        mGroupSize = 0;
    }

    String xContent(){
        StringBuilder lBuilder;
        int lCount;
        int lCellNr;

        lBuilder = new StringBuilder();
        for (lCount = 0; lCount < mGroupSize; lCount++){
                lCellNr = mCellNrs[lCount];
                lBuilder.append(String.format("%03d", lCellNr));
        }
        return lBuilder.toString();
    }

    boolean xAdd(int pCellNr){
        int lCount;
        int lCountInsert;
        boolean lFound;
        boolean lResult;

        lFound = false;
        if (mGroupSize < mMaxSize){
            for (lCount = 0; lCount < mGroupSize; lCount++){
                if (mCellNrs[lCount] == pCellNr){
                    lFound = true;
                    break;
                }
                if (mCellNrs[lCount] > pCellNr){
                    break;
                }
            }
            if (lFound){
                lResult = false;
            } else {
                for (lCountInsert = mGroupSize; lCountInsert > lCount; lCountInsert--){
                    mCellNrs[lCountInsert] = mCellNrs[lCountInsert - 1];
                }
                mCellNrs[lCount] = pCellNr;
                mGroupSize++;
                lResult = true;
            }
        } else {
            lResult = false;
        }
        return lResult;
    }

    boolean xDelete(int pCellNr){
        int lCount;
        int lCountDelete;
        boolean lResult;

        lResult = false;
        for (lCount = 0; lCount < mGroupSize; lCount++){
            if (mCellNrs[lCount] == pCellNr){
                lResult = true;
                break;
            }
        }
        if (lResult){
            for (lCountDelete = lCount + 1; lCountDelete < mGroupSize; lCountDelete++){
                mCellNrs[lCountDelete - 1] = mCellNrs[lCountDelete];
            }
            mGroupSize--;
        }
        return lResult;
    }

    int xSize(){
        return mGroupSize;
    }

    int xCell(int pSeq){
        if ( pSeq >= 0 && pSeq < mGroupSize){
            return mCellNrs[pSeq];
        } else {
            return 0;
        }
    }

    boolean xContains(int pCellNr){
        int lCount;
        boolean lContains;

        lContains = false;
        for (lCount = 0; lCount < mGroupSize; lCount++){
            if (mCellNrs[lCount] == pCellNr){
                lContains = true;
                break;
            }
            if (mCellNrs[lCount] > pCellNr){
                break;
            }
        }
        return lContains;
    }
}
