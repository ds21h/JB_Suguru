package jb.game.suguru;

class Cell {
    private int mMaxValue;
    private int mGroup;
    private boolean mBndLeft;
    private boolean mBndRight;
    private boolean mBndTop;
    private boolean mBndBottom;
    private int mValue;
    private boolean mFixed;
    private boolean mConflict;
    private boolean mSetupSel;
    private boolean mSetupTaken;
    private boolean[] mPencil;

    Cell(){
        mMaxValue = 10;
        sFullReset();
    }

    Cell(int pValue, boolean pFixed, boolean pConflict, boolean pSetupSel, boolean pSetupTaken, String pPencil){
        int lCount;
        char lPos;

        mMaxValue = 10;
        mGroup = -1;
        mBndLeft = false;
        mBndRight = false;
        mBndTop = false;
        mBndBottom = false;
        mValue = pValue;
        mFixed = pFixed;
        mConflict = pConflict;
        mSetupSel = pSetupSel;
        mSetupTaken = pSetupTaken;
        mPencil = new boolean[mMaxValue];
        for (lCount = 0; lCount < pPencil.length(); lCount++) {
            lPos = pPencil.charAt(lCount);
            if (lPos >= '1' && lPos <= '9') {
                mPencil[(int) (lPos - '0')] = true;
            }
        }
    }

    void xFullReset(){
        sFullReset();
    }

    void xPlayReset(){
        sPlayReset();
    }

    private void sFullReset(){
        int lCount;

        mGroup = -1;
        mBndLeft = false;
        mBndRight = false;
        mBndTop = false;
        mBndBottom = false;
        mFixed = false;
        mSetupSel = false;
        mSetupTaken = false;
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

    Cell(Cell pCell){
        sSetCell(pCell );
    }

    void xSetCell(Cell pCell){
        sSetCell(pCell );
    }

    void sSetCell(Cell pCell){
        mGroup = pCell.mGroup;
        mBndLeft = pCell.mBndLeft;
        mBndRight = pCell.mBndRight;
        mBndTop = pCell.mBndTop;
        mBndBottom = pCell.mBndBottom;
        mValue = pCell.mValue;
        mFixed = pCell.mFixed;
        mSetupSel = pCell.mSetupSel;
        mSetupTaken = pCell.mSetupTaken;
    }

    int xGroup(){
        return mGroup;
    }

    void xGroup(int pGroup){
        mGroup = pGroup;
    }

    boolean xBndLeft(){
        return mBndLeft;
    }

    void xBndLeft(boolean pBnd){
        mBndLeft = pBnd;
    }

    boolean xBndRight(){
        return mBndRight;
    }

    void xBndRight(boolean pBnd){
        mBndRight = pBnd;
    }

    boolean xBndTop(){
        return mBndTop;
    }

    void xBndTop(boolean pBnd){
        mBndTop = pBnd;
    }

    boolean xBndBottom(){
        return mBndBottom;
    }

    void xBndBottom(boolean pBnd){
        mBndBottom = pBnd;
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

    boolean xSetupSel(){
        return mSetupSel;
    }

    void xSetupSel(boolean pSetupSel){
        mSetupSel = pSetupSel;
    }

    boolean xSetupTaken(){
        return mSetupTaken;
    }

    void xSetupTaken(boolean pSetupTaken){
        mSetupTaken = pSetupTaken;
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
