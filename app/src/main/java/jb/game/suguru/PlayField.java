package jb.game.suguru;

class PlayField {
    private int mFieldId;
    private Cell[] mCells;
    private boolean mPencilMode;
    private boolean mPencilAuto;
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
        mPencilAuto = true;
        mSelection = 0;
        mFilledCells = 0;
    }

    PlayField(int pFieldId, Cell[] pCells, int pSelection, boolean pPencil, boolean pPencilAuto) {
        mFieldId = pFieldId;
        mCells = pCells;
        mPencilMode = pPencil;
        mPencilAuto = pPencilAuto;
        mSelection = pSelection;
        sCountFilledCells();
    }

    PlayField(PlayField pField){
       sPlayField(pField.xFieldId(), pField);
    }

    PlayField(int pFieldId, PlayField pField){
        sPlayField(pFieldId, pField);
    }

    private void sPlayField(int pFieldId, PlayField pField){
        int lCount;

        mFieldId = pFieldId;
        mCells = new Cell[pField.mCells.length];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new Cell(pField.mCells[lCount]);
        }
        mPencilMode = pField.mPencilMode;
        mPencilAuto = pField.mPencilAuto;
        mSelection = pField.mSelection;
        mFilledCells = pField.mFilledCells;
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

    boolean xPencilAuto(){
        return mPencilAuto;
    }

    void xFlipPencilAuto(){
        mPencilAuto = !mPencilAuto;
    }

    void xPencilFlip() {
        mPencilMode = !mPencilMode;
    }


    void xClearPencil(){
        int lCount;
        Cell lCell;

        for (lCount = 0; lCount < mCells.length; lCount++){
            lCell = mCells[lCount];
            lCell.xClearPencils();
        }
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
