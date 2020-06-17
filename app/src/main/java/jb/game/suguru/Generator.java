package jb.game.suguru;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Generator {
    private int mRows;
    private int mColumns;
    private List<Integer> mFreeCells;
    private Random mRandom;
    private PlayField mPlayField;
    private List<Group> mGroups;
    private GenerateCell[] mCells;

    Generator(int pRows, int pColumns){
        mRows = pRows;
        mColumns = pColumns;
        mRandom = new Random();
        mCells = new GenerateCell[mRows * mColumns];
    }

    PlayField xPlayField(){
        return mPlayField;
    }

    List<Group> xGroups(){
        return mGroups;
    }

    void xGenerate(){
        boolean lFound;
        do{
            sGenInit();
            sGenGroups();
            sFillGroups();
            lFound = sMakeSolution();
        } while(!lFound);
    }

    private void sGenInit(){
        int lCount;

        mPlayField = new PlayField(mRows * mColumns);
        mFreeCells = new ArrayList<>();
        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            mFreeCells.add(lCount );
        }
        mGroups = new ArrayList<>();
    }

    private void sGenGroups(){
        Group lGroup;
        int lPos;
        int lCellNr;

        while (mFreeCells.size() > 0){
            lGroup = new Group();

            lPos = mRandom.nextInt(mFreeCells.size());
            lCellNr = mFreeCells.get(lPos);
            mFreeCells.remove(lPos);
            lGroup.xAdd(lCellNr);
            do{
                if (!sGroupGrow(lGroup)){
                    break;
                }
            } while (lGroup.xSize() < 5);

            mGroups.add(lGroup);
        }
    }

    boolean sGroupGrow(Group pGroup){
        boolean lResult;
        int lRow;
        int lColumn;
        int[] lGrowCells;
        int lGrowLength;
        int lCelNr;
        int lCount;
        int lGroupCell;

        lGrowCells = new int[10];
        lGrowLength = 0;
        for (lCount = 0; lCount < pGroup.xSize(); lCount++){
            lGroupCell = pGroup.xCell(lCount);
            lRow = sRow(lGroupCell);
            lColumn = sColumn(lGroupCell);

            if (lColumn > 0) {
                if (!pGroup.xContains(lGroupCell - 1)) {
                    if (mFreeCells.contains(lGroupCell - 1)) {
                        lGrowCells[lGrowLength] = lGroupCell - 1;
                        lGrowLength++;
                    }
                }
            }
            if (lColumn < mColumns - 1){
                if (!pGroup.xContains(lGroupCell + 1)) {
                    if (mFreeCells.contains(lGroupCell + 1)) {
                        lGrowCells[lGrowLength] = lGroupCell + 1;
                        lGrowLength++;
                    }
                }
            }
            if (lRow > 0){
                if (!pGroup.xContains(lGroupCell - mColumns)) {
                    if (mFreeCells.contains(lGroupCell - mColumns)) {
                        lGrowCells[lGrowLength] = lGroupCell - mColumns;
                        lGrowLength++;
                    }
                }
            }
            if (lRow < mRows - 1){
                if (!pGroup.xContains(lGroupCell + mColumns)) {
                    if (mFreeCells.contains(lGroupCell + mColumns)) {
                        lGrowCells[lGrowLength] = lGroupCell + mColumns;
                        lGrowLength++;
                    }
                }
            }
        }
        if (lGrowLength > 0){
            lCelNr = lGrowCells[mRandom.nextInt(lGrowLength)];
            mFreeCells.remove(new Integer(lCelNr));
            pGroup.xAdd(lCelNr);
            lResult = true;
        } else {
            lResult = false;
        }
        return lResult;
    }

    private void sFillGroups(){
        int lCountGroup;
        Group lGroup;
        int lTestCell;
        Cell lPlayCell;
        int lCount;
        int lCellNr;

        for (lCountGroup = 0; lCountGroup < mGroups.size(); lCountGroup++){
            lGroup = mGroups.get(lCountGroup);
            for (lCount = 0; lCount < lGroup.xSize(); lCount++){
                lCellNr = lGroup.xCell(lCount);
                lPlayCell = mPlayField.xCell(lCellNr);
                lPlayCell.xGroup(lCountGroup);

                lPlayCell.xBndLeft(!lGroup.xContains(lCellNr - 1));
                lPlayCell.xBndRight(!lGroup.xContains(lCellNr + 1));
                lPlayCell.xBndTop(!lGroup.xContains(lCellNr - mColumns));
                lPlayCell.xBndBottom(!lGroup.xContains(lCellNr + mColumns));
            }
        }
    }

    private boolean sMakeSolution() {
        int lCount;
        Cell lPlayCell;
        Group lGroup;
        boolean lResult = true;

        for (lCount = 0; lCount < mCells.length; lCount++){
            lPlayCell = mPlayField.xCell(lCount);
            lGroup = mGroups.get(lPlayCell.xGroup());
            mCells[lCount] = new GenerateCell(lPlayCell, lGroup.xSize());
        }

        lResult = sNextValue(0);

        if (lResult){
            sMakePuzzle();
//            mPlayField.xSetCells(mCells);
        }
        return lResult;
    }

    private boolean sNextValue(int pStart) {
        boolean lResultOK;

        lResultOK = false;
        if (pStart < mCells.length) {
            while (mCells[pStart].xNextValue(mRandom)) {
                if (sCheckCell(pStart)) {
                    lResultOK = sNextValue(pStart + 1);
                    if (lResultOK) {
                        break;
                    }
                }
            }
            if (!lResultOK) {
                mCells[pStart].xFullReset();
            }
        } else {
            lResultOK = true;
        }

        return lResultOK;
    }

    private boolean sCheckCell(int pCelNr){
        boolean lResult;

        lResult = sCheckGroup(pCelNr);
        if (lResult){
            lResult = sCheckPerimeter(pCelNr);
        }
        return lResult;
    }

    private boolean sCheckGroup(int pCelNr){
        Group lGroup;
        int lCount;
        boolean lResult;
        int lCellCount;
        int lCellNr;

        lGroup = mGroups.get(mCells[pCelNr].xGroup());
        lCount = 0;
        for (lCellCount = 0; lCellCount < lGroup.xSize(); lCellCount++){
            lCellNr = lGroup.xCell(lCellCount);
            if (mCells[lCellNr].xValue() == mCells[pCelNr].xValue()){
                lCount++;
            }
        }
        if (lCount > 1){
            lResult = false;
        } else {
            lResult = true;
        }
        return lResult;
    }

    private boolean sCheckPerimeter(int pCelNr){
        int lRow;
        int lColumn;
        int lRowStart;
        int lRowEnd;
        int lColumnStart;
        int lColumnEnd;
        int lRowCount;
        int lColumnCount;
        int lCount;
        boolean lResult;

        lRow = sRow(pCelNr);
        lColumn = sColumn(pCelNr);

        if (lRow > 0){
            lRowStart = lRow - 1;
        } else {
            lRowStart = 0;
        }
        if (lRow < mRows - 1){
            lRowEnd = lRow + 1;
        } else {
            lRowEnd = mRows - 1;
        }
        if (lColumn > 0){
            lColumnStart = lColumn - 1;
        } else {
            lColumnStart = 0;
        }
        if (lColumn < mColumns - 1){
            lColumnEnd = lColumn + 1;
        } else {
            lColumnEnd = mColumns - 1;
        }
        lCount = 0;
        for (lRowCount = lRowStart; lRowCount <= lRowEnd; lRowCount++){
            for (lColumnCount = lColumnStart; lColumnCount <= lColumnEnd; lColumnCount++){
                if (mCells[(lRowCount * mColumns) + lColumnCount].xValue() == mCells[pCelNr].xValue()){
                    lCount++;
                }
            }
        }
        if (lCount > 1){
            lResult = false;
        } else {
            lResult = true;
        }
        return lResult;
    }

    private void sMakePuzzle(){
        int lCount;
        int lPos;
        int[] lKeep;
        boolean lFound;
        Group lGroup;

        lKeep = new int[5];
        lCount = 0;
        do{
            lPos = mRandom.nextInt(mRows * mColumns);
            if (!sInArray(lPos, lKeep)){
                lGroup = mGroups.get(mCells[lPos].xGroup());
                if (lGroup.xSize() > 2){
                    lKeep[lCount] = lPos;
                    lCount++;
                }
            }
        } while (lCount < 5);
        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            if (!sInArray(lCount,  lKeep)){
                mCells[lCount].xValueReset();
            }
        }
    }

    private boolean sInArray(int pValue, int[] pArray){
        boolean lFound;

        lFound = false;
        for (int lEntry:pArray){
            if (lEntry == pValue){
                lFound = true;
                break;
            }
        }
        return lFound;
    }

    private int sRow(int pPos){
        return pPos / mColumns;
    }

    private int sColumn(int pPos){
        return pPos % mColumns;
    }
}
