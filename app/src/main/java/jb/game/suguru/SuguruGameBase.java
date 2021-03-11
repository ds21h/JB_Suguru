 package jb.game.suguru;

import java.util.ArrayList;
import java.util.List;

class SuguruGameBase {
    private int mGameStatus;
    static final int cStatusNone = 0;
    static final int cStatusSetupGroups = 1;
    static final int cStatusSetupValues = 2;
    static final int cStatusGenerate = 3;
    static final int cStatusPlay = 4;
    static final int cStatusSolved = 5;

    private int mRows;
    private int mColumns;
    private int mMaxValue;
    private PlayField mPlayField;
    private List<Group> mGroups;
    private GameCell[] mGameCells;
    private int mUsedTime;
    private Group mSetupGroup;
    private int mSetupTaken;
    private int mBatchId;
    private int mGameId;
    private int mDifficulty;
    private boolean mLibSolved;

    SuguruGameBase(){
        mGameStatus = cStatusNone;
        mSetupGroup = null;
        mRows = 6;
        mColumns = 6;
        mMaxValue = 5;
        mPlayField = new PlayField(mRows * mColumns);
        mGroups = new ArrayList<>();
        sInitGameCells();
        mUsedTime = 0;
        mBatchId = -1;
        mGameId = -1;
        mDifficulty = 0;
        mLibSolved = false;
    }

    SuguruGameBase(SuguruGameBase pGame){
        Group lGroup;
        int lCount;

        mGameStatus = pGame.mGameStatus;
        mRows = pGame.mRows;
        mColumns = pGame.mColumns;
        mMaxValue = pGame.mMaxValue;
        mPlayField = new PlayField(pGame.mPlayField);
        mGroups = new ArrayList<>();
        for (lCount = 0; lCount < pGame.mGroups.size(); lCount++){
            lGroup = new Group(pGame.mGroups.get(lCount));
            mGroups.add(lGroup);
        }
        mGameCells = new GameCell[pGame.mGameCells.length];
        for (lCount = 0; lCount < mGameCells.length; lCount++){
            mGameCells[lCount] = new GameCell(pGame.mGameCells[lCount]);
        }
        mUsedTime = pGame.mUsedTime;
        mSetupGroup = pGame.mSetupGroup;
        mSetupTaken = pGame.mSetupTaken;
        mBatchId = pGame.mBatchId;
        mGameId = pGame.mGameId;
        mDifficulty = pGame.mDifficulty;
        mLibSolved = pGame.mLibSolved;
    }

    void xInitGame(List<Group> pGroups, PlayField pField, int pGameRows, int pGameColumns,
                   int pMaxValue, int pStatus, int pDifficulty, boolean pLibSolved, int pBatchId, int pGameId, int pUsedTime){
        int lCellCount;
        int lCount;
        Group lGroup;

        mRows = pGameRows;
        mColumns = pGameColumns;
        mMaxValue = pMaxValue;
        mGameStatus = pStatus;

        sInitGameCells();
        mGroups = pGroups;
        if (mGameStatus == cStatusSetupGroups){
            mSetupGroup = mGroups.get(mGroups.size() - 1);
            mSetupTaken = 0;
            for (lCount = 0; lCount < mGroups.size() - 1; lCount++){
                lGroup = mGroups.get(lCount);
                for (lCellCount = 0; lCellCount < lGroup.xSize(); lCellCount++){
                    mGameCells[lGroup.xCell(lCellCount)].xSetupTaken(true);
                    mSetupTaken++;
                }
            }
            for (lCellCount = 0; lCellCount < mSetupGroup.xSize(); lCellCount++){
                mGameCells[mSetupGroup.xCell(lCellCount)].xSetupSel(true);
            }
        } else {
            mSetupGroup = null;
        }
        mPlayField = pField;
        sGroupBorders();
        mUsedTime = pUsedTime;
        mBatchId = pBatchId;
        mGameId = pGameId;
        mDifficulty = pDifficulty;
        mLibSolved = pLibSolved;
    }

    private void sInitGameCells(){
        int lCount;

        mGameCells = new GameCell[mRows * mColumns];
        for (lCount = 0; lCount < mGameCells.length; lCount++){
            mGameCells[lCount] = new GameCell();
        }
    }

    private void sGroupBorders(){
        int lGroupCount;
        int lCellCount;
        int lCellNr;
        int lActiveGroups;
        Group lGroup;
        GameCell lGameCell;

        if (mGameStatus == cStatusSetupGroups){
            lActiveGroups = mGroups.size() - 1;
        } else {
            lActiveGroups = mGroups.size();
        }
        for (lGroupCount = 0; lGroupCount < lActiveGroups; lGroupCount++){
            lGroup = mGroups.get(lGroupCount);
            for (lCellCount = 0; lCellCount < lGroup.xSize(); lCellCount++){
                lCellNr = lGroup.xCell(lCellCount);
                lGameCell = mGameCells[lCellNr];
                lGameCell.xGroup(lGroupCount);
                lGameCell.xBndLeft(sLeftBorder(lCellNr, lGroup));
                lGameCell.xBndRight(sRightBorder(lCellNr, lGroup));
                lGameCell.xBndTop(sTopBorder(lCellNr, lGroup));
                lGameCell.xBndBottom(sBottomBorder(lCellNr, lGroup));
            }
        }
    }

    int xRows(){
        return mRows;
    }

    int xColumns(){
        return mColumns;
    }

    int xMaxValue(){
        return mMaxValue;
    }

    int xGameStatus(){
        return mGameStatus;
    }

    int xUsedTime() {
        return mUsedTime;
    }

    void xAddUsedTime(int pCorr) {
        mUsedTime += pCorr;
    }

    void xResetUsedTime() {
        mUsedTime = 0;
    }

    List<Group> xGroups(){
        return mGroups;
    }

    PlayField xPlayField(){
        return mPlayField;
    }

    void xPlayField(PlayField pField){
        mPlayField = pField;
    }

    boolean xPencilMode(){
        return mPlayField.xPencilMode();
    }

    boolean xPencilAuto(){
        return mPlayField.xPencilAuto();
    }

    void xFlipPencilAuto(){
        mPlayField.xFlipPencilAuto();
    }

    void xPencilFlip(){
        mPlayField.xPencilFlip();
    }

    boolean xLib(){
        return mBatchId >= 0;
    }

    int xBatchId(){
        return mBatchId;
    }

    int xGameId(){
        return mGameId;
    }

    int xDifficulty(){
        return mDifficulty;
    }

    void xChangeDifficulty(int pDifficulty){
        mDifficulty = pDifficulty;
        mLibSolved = false;
    }

    boolean xLibSolved(){
        return mLibSolved;
    }

    boolean xSetLibSolved(){
        boolean lResult;

        lResult = mLibSolved;
        mLibSolved = true;
        return lResult;
    }

    void xToLib(int pGameId, int pDifficulty){
        if (pGameId >= 0){
            mBatchId = 0;
            mGameId = pGameId;
            mDifficulty = pDifficulty;
        }
    }

    boolean xShowGroupButton(){
        if (mGameStatus == cStatusSetupGroups && mSetupGroup != null && mSetupGroup.xSize() > 0){
            return true;
        } else {
            return false;
        }
    }

    void xStoreGroup(){
        int lCount;
        int lCellNr;
        boolean lGroupOK;
        GameCell lGameCell;

        if (mSetupGroup != null){
            lGroupOK = true;
            if (mSetupGroup.xSize() > 1){
                for (lCount = 0; lCount < mSetupGroup.xSize(); lCount++){
                    lCellNr = mSetupGroup.xCell(lCount);
                    if (sLeftBorder(lCellNr, mSetupGroup)
                            && sRightBorder(lCellNr, mSetupGroup)
                            && sTopBorder(lCellNr, mSetupGroup)
                            && sBottomBorder(lCellNr, mSetupGroup)){
                        lGroupOK = false;
                        break;
                    }
                }
            }
            if (lGroupOK){
                for (lCount = 0; lCount < mSetupGroup.xSize(); lCount++){
                    lCellNr = mSetupGroup.xCell(lCount);
                    lGameCell = mGameCells[lCellNr];
                    lGameCell.xGroup(mGroups.size() - 1);

                    lGameCell.xBndLeft(!mSetupGroup.xContains(lCellNr - 1));
                    lGameCell.xBndRight(!mSetupGroup.xContains(lCellNr + 1));
                    lGameCell.xBndTop(!mSetupGroup.xContains(lCellNr - mColumns));
                    lGameCell.xBndBottom(!mSetupGroup.xContains(lCellNr + mColumns));
                    lGameCell.xSetupSel(false);
                    lGameCell.xSetupTaken(true);
                }
                mSetupTaken += mSetupGroup.xSize();
                if (mSetupTaken >= mRows * mColumns){
                    mSetupGroup = null;
                    mGameStatus = cStatusSetupValues;
                } else {
                    mSetupGroup = new Group();
                    mGroups.add(mSetupGroup);
                }
            }
        }
    }

    private boolean sLeftBorder(int pCellNr, Group pGroup){
        int lColumn;
        boolean lResult;

        lColumn = pCellNr % mColumns;
        lResult = true;
        if (lColumn > 0){
            if (pGroup.xContains(pCellNr - 1)){
                lResult  = false;
            }
        }
        return lResult;
    }

    private boolean sRightBorder(int pCellNr, Group pGroup){
        int lColumn;
        boolean lResult;

        lColumn = pCellNr % mColumns;
        lResult = true;
        if (lColumn < mColumns - 1){
            if (pGroup.xContains(pCellNr + 1)){
                lResult  = false;
            }
        }
        return lResult;
    }

    private boolean sTopBorder(int pCellNr, Group pGroup){
        int lRow;
        boolean lResult;

        lRow = pCellNr / mColumns;
        lResult = true;
        if (lRow > 0){
            if (pGroup.xContains(pCellNr - mColumns)){
                lResult  = false;
            }
        }
        return lResult;
    }

    private boolean sBottomBorder(int pCellNr, Group pGroup){
        int lRow;
        boolean lResult;

        lRow = pCellNr / mColumns;
        lResult = true;
        if (lRow < mRows - 1){
            if (pGroup.xContains(pCellNr + mColumns)){
                lResult  = false;
            }
        }
        return lResult;
    }

    ValueCell xPlayCell(int pRow, int pColumn){
        return mPlayField.xCell(pRow * mColumns + pColumn);
    }

    GameCell xGameCell(int pRow, int pColumn){
        return mGameCells[pRow * mColumns + pColumn];
    }

    void xSelection(int pRow, int pColumn){
        int lCellNr;

        lCellNr = pRow * mColumns + pColumn;
        if (mGameStatus == cStatusSetupGroups){
            if (!mGameCells[lCellNr].xSetupTaken()){
                if (mSetupGroup.xContains(lCellNr)){
                    if (mSetupGroup.xDelete(lCellNr)){
                        mGameCells[lCellNr].xSetupSel(false);
                    }
                } else {
                    if (mSetupGroup.xSize() < mMaxValue){
                        if (mSetupGroup.xAdd(lCellNr)){
                            mGameCells[lCellNr].xSetupSel(true);
                        }
                    }
                }
            }
        } else {
            mPlayField.xSelection(lCellNr);
        }
    }

    boolean xIsSelection(int pRow, int pColumn){
        if (mPlayField.xSelection() == (pRow * mColumns) + pColumn){
            return true;
        } else {
            return false;
        }
    }

    void xNewGame(LibGame pLibGame){
        int lCount;
        String lWork;
        int lValue;
        ValueCell lValueCell;
        GameCell lGameCell;
        Group lGroup;
        int lMax;
        int lSize;

        mGameStatus = cStatusNone;
        mRows = pLibGame.xRows();
        mColumns = pLibGame.xColumns();
        mGroups.clear();
        mPlayField = new PlayField(mRows * mColumns);
        sInitGameCells();

        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            lValueCell = mPlayField.xCell(lCount);
            lGameCell = mGameCells[lCount];
            lWork = pLibGame.xContent().substring((lCount * 3), (lCount * 3) + 1);
            lValue = Integer.parseInt(lWork);
            if (lValue > 0){
                lValueCell.xValue(lValue);
                lValueCell.xFixed(true);
            }
            lWork = pLibGame.xContent().substring((lCount * 3) + 1, (lCount * 3) + 3);
            lValue = Integer.parseInt(lWork);
            while (lValue >= mGroups.size()){
                mGroups.add(new Group());
            }
            lGroup = mGroups.get(lValue);
            lGroup.xAdd(lCount);
            lGameCell.xGroup(lValue);
        }
        mPlayField.xSetFilledCells();
        lMax = 0;
        for (lCount = 0; lCount < mGroups.size(); lCount++) {
            lSize = mGroups.get(lCount).xSize();
            if (lSize > lMax){
                lMax = lSize;
            }
        }
        mMaxValue = lMax;
        sGroupBorders();
        mBatchId = pLibGame.xBatchId();
        mGameId = pLibGame.xGameId();
        mDifficulty = pLibGame.xDifficulty();
        mLibSolved = pLibGame.xSolved();
    }

    void xStartSetUp(int pRows, int pColumns, int pMaxValue) {
        mGameStatus = cStatusSetupGroups;
        mRows = pRows;
        mColumns = pColumns;
        mMaxValue = pMaxValue;
        mSetupTaken = 0;
        mGroups.clear();
        mSetupGroup = new Group();
        mGroups.add(mSetupGroup);
        mPlayField = new PlayField(mRows * mColumns);
        sInitGameCells();
        mBatchId = -1;
        mGameId = -1;
        mDifficulty = 0;
        mLibSolved = false;
    }

    boolean xFinishSetup(){
        ValueCell lValueCell;
        int lCount;
        boolean lResult;

        lResult = sCheckGame();
        if (lResult){
            for (lCount = 0; lCount < mRows * mColumns; lCount++){
                lValueCell = mPlayField.xCell(lCount);
                if (lValueCell.xValue() > 0){
                    lValueCell.xFixed(true);
                }
                mGameCells[lCount].xSetupTaken(false);
                mGameCells[lCount].xSetupSel(false);
            }
        }
        return lResult;
    }

    void xStartGame() {
        mGameStatus = cStatusPlay;
        xResetUsedTime();
    }

    void xReset(){
        mPlayField.xResetField();
        mGameStatus = cStatusPlay;
        mUsedTime = 0;
    }

    void xUndo(){
        mPlayField.xActionUndo();
    }

    boolean xUndoAvail(){
        boolean lResult;

        if (mGameStatus == cStatusPlay){
            lResult = mPlayField.xActionPresent();
        } else {
            lResult = false;
        }
        return lResult;
    }

    void xProcessDigit(int pDigit) {
        ValueCell lValueCell;
        boolean lCellFilled;

        if (pDigit >= 1 && pDigit <= mMaxValue) {
            lValueCell = mPlayField.xSelectedCell();
            if (mPlayField.xPencilMode()) {
                if (lValueCell.xValue() == 0){
                    mPlayField.xActionBegin();
                    mPlayField.xActionSaveCell();
                    lValueCell.xPencilFlip(pDigit);
                    mPlayField.xActionEnd();
                }
            } else {
                if (!lValueCell.xFixed()) {
                    lCellFilled = mPlayField.xSelectedCell().xValue() > 0;
                    mPlayField.xActionBegin();
                    mPlayField.xActionSaveCell();
                    if (mPlayField.xSetSelectedCellValue(pDigit)){
                        if (sCheckGame()){
                            if (mPlayField.xFieldFull()){
                                mGameStatus = cStatusSolved;
                            } else {
                                if (mPlayField.xPencilAuto()){
                                    sAdjustGroup(mPlayField.xSelection(), pDigit);
                                    sAdjustSurr(mPlayField.xSelection(), pDigit);
                                }
                            }
                        }
                    } else {
                        sCheckGame();
                    }
                    if (lCellFilled && mPlayField.xPencilAuto()){
                        mPlayField.xClearPencil(true);
                    }
                    mPlayField.xActionEnd();
                }
            }
        }
    }

    void xFillPencil(){
        sInitPencils();
        sAdjustPencils();
    }

    void sInitPencils(){
        int lCount;
        ValueCell lValueCell;
        GameCell lGameCell;
        Group lGroup;

        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            lValueCell = mPlayField.xCell(lCount);
            lGameCell = mGameCells[lCount];
            if (lValueCell.xValue() > 0){
                lValueCell.xClearPencils();
            } else {
                lGroup = mGroups.get(lGameCell.xGroup());
                lValueCell.xSetPencils(lGroup.xSize());
            }
        }
    }

    void sAdjustPencils(){
        int lCount;
        int lValue;

        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            lValue = mPlayField.xCell(lCount).xValue();
            if (lValue > 0){
                sAdjustGroup(lCount, lValue);
                sAdjustSurr(lCount, lValue);
            }
        }
    }

    void sAdjustGroup(int pCellNr, int pPencil){
        Group lGroup;
        int lCount;
        int lCellNr;

        lGroup = mGroups.get(mGameCells[pCellNr].xGroup());
        for (lCount = 0; lCount < lGroup.xSize(); lCount++){
            lCellNr = lGroup.xCell(lCount);
            mPlayField.xActionSaveCell(lCellNr);
            mPlayField.xCell(lCellNr).xPencil(pPencil, false);
        }
    }

    void sAdjustSurr(int pCellNr, int pPencil){
        int lStartRow;
        int lEndRow;
        int lRow;
        int lStartColumn;
        int lEndColumn;
        int lColumn;
        int lCellNr;

        lRow = pCellNr / mColumns;
        lColumn = pCellNr % mColumns;
        if (lRow > 0){lStartRow = lRow - 1;} else {lStartRow = lRow;}
        if (lRow < mRows -1){lEndRow = lRow + 1;} else {lEndRow = lRow;}
        if (lColumn > 0){lStartColumn = lColumn - 1;} else {lStartColumn = lColumn;}
        if (lColumn < mColumns - 1){lEndColumn = lColumn + 1;} else {lEndColumn = lColumn;}

        for (lRow = lStartRow; lRow <= lEndRow; lRow++){
            for (lColumn = lStartColumn; lColumn <= lEndColumn; lColumn++){
                lCellNr = lRow * mColumns + lColumn;
                mPlayField.xActionSaveCell(lCellNr);
                mPlayField.xCell(lCellNr).xPencil(pPencil, false);
            }
        }
    }

    void xClearPencil(){
        mPlayField.xClearPencil();
    }

    private boolean sCheckGame(){
        boolean lResult;
        int lRow;
        int lColumn;
        int lCellNr;

        lResult = true;
        mPlayField.xResetConflicts();
        lCellNr = 0;
        for (lRow = 0; lRow < mRows; lRow++){
            for (lColumn = 0; lColumn < mColumns; lColumn++){
                if (mPlayField.xCell(lCellNr).xValue() > 0){
                    if (!sTestGroup(lCellNr)){
                        lResult = false;
                    }
                    if (!sTestSurr(lRow, lColumn)){
                        lResult = false;
                    }
                }
                lCellNr++;
            }
        }
        return lResult;
    }

    private boolean sTestGroup(int pCellNr){
        boolean lResult;
        Group lGroup;
        int lGroupCount;
        int lCompCellNr;
        ValueCell lTestValueCell;
        ValueCell lCompValueCell;

        lResult = true;
        lTestValueCell = mPlayField.xCell(pCellNr);
        lGroup = mGroups.get(mGameCells[pCellNr].xGroup());
        if (lTestValueCell.xValue() > lGroup.xSize()){
            lTestValueCell.xConflict(true);
            lResult = false;
        } else {
            for (lGroupCount = 0; lGroupCount < lGroup.xSize(); lGroupCount++){
                lCompCellNr = lGroup.xCell(lGroupCount);
                lCompValueCell = mPlayField.xCell(lCompCellNr);
                if (lCompValueCell != lTestValueCell){
                    if (lTestValueCell.xValue() == lCompValueCell.xValue()){
                        lTestValueCell.xConflict(true);
                        lResult = false;
                    }
                }
            }
        }
        return lResult;
    }

    private boolean sTestSurr(int pRow, int pColumn){
        boolean lResult;
        int lStartRow;
        int lEndRow;
        int lRow;
        int lStartColumn;
        int lEndColumn;
        int lColumn;
        ValueCell lTestValueCell;
        ValueCell lCompValueCell;

        if (pRow > 0){lStartRow = pRow - 1;} else {lStartRow = pRow;}
        if (pRow < mRows -1){lEndRow = pRow + 1;} else {lEndRow = pRow;}
        if (pColumn > 0){lStartColumn = pColumn - 1;} else {lStartColumn = pColumn;}
        if (pColumn < mColumns - 1){lEndColumn = pColumn + 1;} else {lEndColumn = pColumn;}

        lResult = true;
        lTestValueCell = mPlayField.xCell(pRow * mColumns + pColumn);
        for (lRow = lStartRow; lRow <= lEndRow; lRow++){
            for (lColumn = lStartColumn; lColumn <= lEndColumn; lColumn++){
                lCompValueCell = mPlayField.xCell(lRow * mColumns + lColumn);
                if (lTestValueCell != lCompValueCell){
                    if (lTestValueCell.xValue() == lCompValueCell.xValue()){
                        lTestValueCell.xConflict(true);
                        lResult = false;
                    }
                }
            }
        }
        return lResult;
    }

    String xGameBasic(){
        StringBuilder lResult;
        int lCount;
        ValueCell lValueCell;

        lResult = new StringBuilder();
        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            lValueCell = mPlayField.xCell(lCount);
            if (lValueCell.xFixed()){
                lResult.append(String.format("%01d", lValueCell.xValue()));
            } else {
                lResult.append("0");
            }
            lResult.append(String.format("%02d", mGameCells[lCount].xGroup()));
        }
        return lResult.toString();
    }
}
