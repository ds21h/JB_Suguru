package jb.game.suguru;

import java.util.ArrayList;
import java.util.List;

class Action {
    private int mSelection;
    private int mFilledCells;
    private boolean mPencilMode;
    private boolean mPencilAuto;
    private List<ActionCell> mCells;

    Action (int pSelection, int pFilledCells, boolean pPencilMode, boolean pPencilAuto){
        mSelection = pSelection;
        mFilledCells = pFilledCells;
        mPencilMode = pPencilMode;
        mPencilAuto = pPencilAuto;
        mCells = new ArrayList<>();
    }

    void xSaveCell (int pCellNr, ValueCell pCell){
        ActionCell lCell;

        lCell = new ActionCell(pCellNr, pCell);
        mCells.add(lCell);
    }

    int xNumCells(){
        return mCells.size();
    }

    ActionCell xGetCell(int pCellNr){
        if (pCellNr >= 0 && pCellNr < mCells.size()){
            return mCells.get(pCellNr);
        } else {
            return null;
        }
    }

    int xSelection(){
        return mSelection;
    }

    int xFilledCells(){
        return  mFilledCells;
    }

    boolean xPencilMode(){
        return mPencilMode;
    }

    boolean xPencilAuto(){
        return mPencilAuto;
    }
}
