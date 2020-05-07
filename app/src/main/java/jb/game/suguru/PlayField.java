package jb.game.suguru;

import java.util.List;

class PlayField {
    private int mFieldId;
    private int mRows;
    private int mColumns;
    private PlayCell[] mCells;
    private boolean mPencilMode;
    private int mSelection;
    private int mSelectionRow;
    private int mSelectionColumn;

    PlayField(int pRows, int pColumns) {
        int lCount;

        mFieldId = 0;
        mRows = pRows;
        mColumns = pColumns;
        mCells = new PlayCell[mRows * mColumns];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new PlayCell();
        }
        mPencilMode = false;
        mSelection = 0;
        mSelectionRow = 0;
        mSelectionColumn = 0;
    }

    boolean xPencilMode() {
        return mPencilMode;
    }

    void xPencilFlip() {
        mPencilMode = !mPencilMode;
    }

    PlayCell xSelectedCell() {
        return mCells[mSelection];
    }

    PlayCell xCell(int pRow, int pColumn) {
        return mCells[(pRow * mColumns) + pColumn];
    }

    int xSelection() {
        return mSelection;
    }

    int xSelectionRow() {
        return mSelectionRow;
    }

    void xSelectionRow(int pRow) {
        if (pRow >= 0 && pRow <= 8) {
            mSelectionRow = pRow;
            mSelection = (mSelectionRow * mColumns) + mSelectionColumn;
        }
    }

    int xSelectionColumn() {
        return mSelectionColumn;
    }

    void xSelectionColumn(int pColumn) {
        if (pColumn >= 0 && pColumn <= 8) {
            mSelectionColumn = pColumn;
            mSelection = (mSelectionRow * mColumns) + mSelectionColumn;
        }
    }

    void xResetField() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xReset();
        }
        mPencilMode = false;
    }

    void xFixField() {
        int lCount;
        Cell lCell;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            lCell = mCells[lCount];
            if (lCell.xValue() == 0) {
                lCell.xFixed(false);
            } else {
                lCell.xFixed(true);
            }
        }
    }

    void xSetCellValue(int pValue) {
        PlayCell lCell;

        lCell = mCells[mSelection];
        if (lCell.xValue() == pValue) {
            lCell.xValueReset();
        } else {
            if (lCell.xValue() == 0) {
            } else {
            }
            lCell.xValue(pValue);
        }
    }

    void xResetConflicts() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xConflict(false);
        }
    }

    void xInitPencil() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xSetPencils();
        }
    }

    void xClearPencil() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xClearPencils();
        }
    }
}
