package jb.game.suguru;

import java.util.Arrays;

class ValueCell {
    private final int mMaxValue;
    private int mValue;
    private boolean mFixed;
    private boolean mConflict;
    private boolean[] mPencil;

    ValueCell(){
        mMaxValue = 10;
        sFullReset();
    }

    ValueCell(int pValue, boolean pFixed, boolean pConflict, String pPencil){
        int lCount;
        char lPos;

        mMaxValue = 10;
        mValue = pValue;
        mFixed = pFixed;
        mConflict = pConflict;
        mPencil = new boolean[mMaxValue];
        for (lCount = 0; lCount < pPencil.length(); lCount++) {
            lPos = pPencil.charAt(lCount);
            if (lPos >= '1' && lPos <= '9') {
                mPencil[lPos - '0'] = true;
            }
        }
    }

    ValueCell(ValueCell pValueCell){
        mMaxValue = pValueCell.mMaxValue;
        mValue = pValueCell.mValue;
        mFixed = pValueCell.mFixed;
        mConflict = pValueCell.mConflict;
        mPencil = Arrays.copyOf(pValueCell.mPencil, pValueCell.mPencil.length);
    }

    void xPlayReset(){
        sPlayReset();
    }

    private void sFullReset(){
        mFixed = false;
        sPlayReset();
    }

    private void sPlayReset(){
        int lCount;

        mValue = 0;
        mConflict = false;
        mPencil = new boolean[mMaxValue];
        for (lCount = 0; lCount < mPencil.length; lCount++) {
            mPencil[lCount] = false;
        }
    }

    int xValue() {
        return mValue;
    }

    void xValue(int pValue) {
        if (pValue >= 1 && pValue <= mMaxValue) {
            mValue = pValue;
        }
    }

    void xValueReset() {
        mValue = 0;
    }

    boolean xFixed() {
        return mFixed;
    }

    void xFixed(boolean pFixed) {
        mFixed = pFixed;
    }

    boolean xConflict() {
        return mConflict;
    }

    void xConflict(boolean pConflict) {
        mConflict = pConflict;
    }

    boolean xPencil(int pPencil) {
        if (pPencil >= 1 && pPencil <= mMaxValue) {
            return mPencil[pPencil];
        } else {
            return false;
        }
    }

    void xPencil(int pPencil, boolean pValue){
        if (mValue == 0){
            if (pPencil >= 1 && pPencil <= mMaxValue) {
                mPencil[pPencil] = pValue;
            }
        }
    }

    void xPencilFlip(int pValue) {
        if (mValue == 0){
            if (pValue >= 1 && pValue <= mMaxValue) {
                mPencil[pValue] = !mPencil[pValue];
            }
        }
    }

    void xClearPencils(){
        int lCount;

        for (lCount = 0; lCount < mPencil.length; lCount++) {
            mPencil[lCount] = false;
        }
    }

    void xSetPencils(int pMax){
        int lCount;

        for (lCount = 1; lCount < mPencil.length; lCount++) {
            if (lCount > pMax){
                mPencil[lCount] = false;
            } else {
                mPencil[lCount] = true;
            }
        }
    }

    String xPencils(){
        StringBuilder lBuilder;
        int lCount;

        lBuilder = new StringBuilder();
        for (lCount = 1; lCount < mPencil.length; lCount++) {
            if (mPencil[lCount]) {
                lBuilder.append(lCount);
            }
        }
        return lBuilder.toString();
    }
 }
