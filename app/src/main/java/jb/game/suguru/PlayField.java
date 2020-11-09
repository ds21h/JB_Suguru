package jb.game.suguru;

class PlayField {
    private int mFieldId;
    private ValueCell[] mValueCells;
    private boolean mPencilMode;
    private boolean mPencilAuto;
    private int mSelection;
    private int mFilledCells;

    PlayField(int pSize) {
        int lCount;

        mFieldId = 0;
        mValueCells = new ValueCell[pSize];
        for (lCount = 0; lCount < mValueCells.length; lCount++) {
            mValueCells[lCount] = new ValueCell();
        }
        mPencilMode = false;
        mPencilAuto = true;
        mSelection = 0;
        mFilledCells = 0;
    }

    PlayField(int pFieldId, ValueCell[] pValueCells, int pSelection, boolean pPencil, boolean pPencilAuto) {
        mFieldId = pFieldId;
        mValueCells = pValueCells;
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
        mValueCells = new ValueCell[pField.mValueCells.length];
        for (lCount = 0; lCount < mValueCells.length; lCount++) {
            mValueCells[lCount] = new ValueCell(pField.mValueCells[lCount]);
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
        for (lCount = 0; lCount < mValueCells.length; lCount++){
            if (mValueCells[lCount].xValue() != 0){
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
        if (mFilledCells == mValueCells.length){
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
        ValueCell lValueCell;

        for (lCount = 0; lCount < mValueCells.length; lCount++){
            lValueCell = mValueCells[lCount];
            lValueCell.xClearPencils();
        }
    }

    ValueCell xSelectedCell() {
        return mValueCells[mSelection];
    }

    ValueCell xCell(int pCellNr) {
        return mValueCells[pCellNr];
    }

    ValueCell[] xCells(){
        return mValueCells;
    }

    void xResetField(){
        int lCount;

        mFilledCells = 0;
        for (lCount = 0; lCount < mValueCells.length; lCount++){
            if (mValueCells[lCount].xFixed()){
                mFilledCells++;
            } else {
                mValueCells[lCount].xPlayReset();
            }
        }
    }

    boolean xSetSelectedCellValue(int pValue) {
        ValueCell lValueCell;
        boolean lValueSet;

        lValueCell = mValueCells[mSelection];
        if (lValueCell.xValue() == pValue) {
            lValueCell.xValueReset();
            mFilledCells--;
            lValueSet = false;
        } else {
            if (lValueCell.xValue() == 0){
                mFilledCells++;
            }
            lValueCell.xValue(pValue);
            lValueSet = true;
        }
        return lValueSet;
    }

    void xResetConflicts() {
        int lCount;

        for (lCount = 0; lCount < mValueCells.length; lCount++) {
            mValueCells[lCount].xConflict(false);
        }
    }
 }
