package jb.game.suguru;

class Cell {
    private int mValue;
    private boolean mFixed;

    Cell(){
        mValue = 0;
        mFixed = false;
    }

    Cell(int pValue, boolean pFixed){
        mValue = pValue;
        mFixed = pFixed;
    }

    Cell(Cell pCell){
        mValue = pCell.mValue;
        mFixed = pCell.mFixed;
    }

    void xInitCell(Cell pCell){
        mValue = pCell.mValue;
        mFixed = pCell.mFixed;
    }

    void xReset() {
        mValue = 0;
        mFixed = false;
    }

    int xValue() {
        return mValue;
    }

    void xValue(int pValue) {
        if (pValue >= 1 && pValue <= 5) {
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
}
