package jb.game.suguru;

import java.util.Random;

class GenerateCell extends Cell {
    private boolean[] mFree;
    private int mFreeValues;
    private int mMax;

    GenerateCell(Cell pCell, int pMax){
        super(pCell);
        mMax = pMax;
        mFree = new boolean[mMax];
        sResetFree();
    }

    private void sResetFree(){
        int lCount;

        for (lCount = 0; lCount < mMax; lCount++){
            mFree[lCount] = true;
        }
        mFreeValues = mMax;
    }

    boolean xNextValue(Random pRandom){
        int lPos;
        boolean lResult;
        int lCount;

        if (mFreeValues > 0){
            lPos = pRandom.nextInt(mFreeValues);
            for (lCount = 0; lCount < mMax; lCount++){
                if (mFree[lCount]){
                    if (lPos == 0){
                        xValue(lCount + 1);
                        mFree[lCount] = false;
                        mFreeValues--;
                        break;
                    }
                    lPos--;
                }
            }
            lResult = true;
        } else {
            xFullReset();
            lResult = false;
        }
        return lResult;
    }

    void xFullReset(){
        xValueReset();
        sResetFree();
    }
}
