package jb.game.suguru;

class PlayField {
    private int mFieldId;
    private Cell[] mCells;
    private boolean mPencilMode;
    private int mSelection;
    private int mFilledCells;

    PlayField(int pSize) {
        int lCount;

        mFieldId = 0;
        mCells = new Cell[pSize];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new Cell();
        }
        mPencilMode = false;
        mSelection = 0;
        mFilledCells = 0;
    }

    PlayField(int pFieldId, Cell[] pCells, int pSelection, boolean pPencil) {
        mFieldId = pFieldId;
        mCells = pCells;
        mPencilMode = pPencil;
        mSelection = pSelection;
        sCountFilledCells();
    }

    void xSetFilledCells(){
        sCountFilledCells();
    }

    private void sCountFilledCells(){
        int lCount;

        mFilledCells = 0;
        for (lCount = 0; lCount < mCells.length; lCount++){
            if (mCells[lCount].xValue() != 0){
                mFilledCells++;
            }
        }
    }

    int xFieldId(){
        return mFieldId;
    }

    void xSelection(int pSelection){
        mSelection = pSelection;
    }

    int xSelection(){
        return mSelection;
    }

    boolean xFieldFull(){
        if (mFilledCells == mCells.length){
            return true;
        } else {
            return false;
        }
    }

    boolean xPencilMode() {
        return mPencilMode;
    }

    void xPencilFlip() {
        mPencilMode = !mPencilMode;
    }

    Cell xSelectedCell() {
        return mCells[mSelection];
    }

    Cell xCell(int pCellNr) {
        return mCells[pCellNr];
    }

    Cell[] xCells(){
        return mCells;
    }

    void xResetField(){
        int lCount;

        mFilledCells = 0;
        for (lCount = 0; lCount < mCells.length; lCount++){
            if (mCells[lCount].xFixed()){
                mFilledCells++;
            } else {
                mCells[lCount].xPlayReset();
            }
        }
    }

    boolean xSetSelectedCellValue(int pValue) {
        Cell lCell;
        boolean lValueSet;

        lCell = mCells[mSelection];
        if (lCell.xValue() == pValue) {
            lCell.xValueReset();
            mFilledCells--;
            lValueSet = false;
        } else {
            if (lCell.xValue() == 0){
                mFilledCells++;
            }
            lCell.xValue(pValue);
            lValueSet = true;
        }
        return lValueSet;
    }

    void xResetConflicts() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xConflict(false);
        }
    }
 }
