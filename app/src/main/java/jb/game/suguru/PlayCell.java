package jb.game.suguru;

class PlayCell extends Cell {
    private boolean mConflict;
    private boolean[] mPencil = new boolean[5];

    PlayCell() {
        super();
        sReset();
    }

    PlayCell(int pValue, boolean pFixed, boolean pConflict, String pPencil) {
        super(pValue, pFixed);

        String lPos;
        int lCount;

        sReset();
        mConflict = pConflict;
        for (lCount = 0; lCount < pPencil.length(); lCount++) {
            lPos = pPencil.substring(lCount, lCount + 1);
            if (lPos.charAt(0) >= '1' && lPos.charAt(0) <= '5') {
                mPencil[Integer.valueOf(lPos) - 1] = true;
            }
        }
    }

    PlayCell(PlayCell pCell){
        super(pCell);
        mConflict = pCell.mConflict;
        mPencil = pCell.mPencil.clone();
    }

    @Override
    void xInitCell(Cell pCell) {
        super.xInitCell(pCell);
        sReset();
    }

    void xInitPlayCell(PlayCell pCell){
        int lCount;

        xInitCell(pCell);
        mConflict = pCell.mConflict;
        for (lCount = 0; lCount < mPencil.length; lCount++){
            mPencil[lCount] = pCell.mPencil[lCount];
        }
    }

    @Override
    void xReset() {
        super.xReset();
        sReset();
    }

    private void sReset() {
        int lCount;

        mConflict = false;
        for (lCount = 0; lCount < mPencil.length; lCount++) {
            mPencil[lCount] = false;
        }
    }

    boolean xConflict() {
        return mConflict;
    }

    void xConflict(boolean pConflict) {
        mConflict = pConflict;
    }

    boolean xPencil(int pValue) {
        if (pValue >= 1 && pValue <= 5) {
            return mPencil[pValue - 1];
        } else {
            return false;
        }
    }

    String xPencils() {
        StringBuilder lBuilder;
        int lCount;

        lBuilder = new StringBuilder();
        for (lCount = 0; lCount < mPencil.length; lCount++) {
            if (mPencil[lCount]) {
                lBuilder.append(lCount + 1);
            }
        }
        return lBuilder.toString();
    }

    void xPencilFlip(int pValue) {
        if (pValue >= 1 && pValue <= 5) {
            mPencil[pValue - 1] = !mPencil[pValue - 1];
        }
    }

    void xPencilReset(int pPencil){
        if (pPencil >= 1 && pPencil <= 5) {
            mPencil[pPencil - 1] = false;
        }
    }

   void xSetPencils() {
        sSetPencils(true);
    }

    void xClearPencils() {
        sSetPencils(false);
    }

    private void sSetPencils(boolean pPencil) {
        int lCount;

        for (lCount = 0; lCount < mPencil.length; lCount++) {
            mPencil[lCount] = pPencil;
        }
    }
}
