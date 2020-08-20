package jb.game.suguru;

import java.util.ArrayList;
import java.util.List;

class SuguruGame {
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
    private List<PlayField> mPlayFields;
    private PlayField mPlayField;
    private List<Group> mGroups;
    private int mUsedTime;
    private Group mSetupGroup;
    private int mSetupTaken;
    private boolean mLib;

    SuguruGame(){
        mGameStatus = cStatusNone;
        mSetupGroup = null;
        mRows = 6;
        mColumns = 6;
        mMaxValue = 5;
        mPlayField = new PlayField(mRows * mColumns);
        mPlayFields = new ArrayList<>();
        mPlayFields.add(mPlayField);
        mGroups = new ArrayList<>();
        mUsedTime = 0;
        mLib = false;
    }

    SuguruGame(List<Group> pGroups, List<PlayField> pFields, int pGameRows, int pGameColumns,
               int pMaxValue, int pStatus, int pDifficulty, boolean pLib, int pSelectedField, int pUsedTime){
        int lCellCount;

        mRows = pGameRows;
        mColumns = pGameColumns;
        mMaxValue = pMaxValue;
        mGameStatus = pStatus;

        mGroups = pGroups;
        mPlayFields = pFields;
        if (mPlayFields.isEmpty()) {
            mPlayField = new PlayField(mRows * mColumns);
            mPlayFields.add(mPlayField);
        } else {
            mPlayField = pFields.get(0);
            if (mPlayField.xFieldId() != pSelectedField){
                for (PlayField lField : mPlayFields) {
                    if (lField.xFieldId() == pSelectedField){
                        mPlayField = lField;
                        break;
                    }
                }
            }
        }
        sGroupBorders();
        if (mGameStatus == cStatusSetupGroups){
            mSetupGroup = new Group();
            mSetupTaken = 0;
            for (lCellCount = 0; lCellCount < mRows * mColumns; lCellCount++){
                if (mPlayField.xCell(lCellCount).xSetupSel()){
                    mSetupGroup.xAdd(lCellCount);
                }
                if (mPlayField.xCell(lCellCount).xSetupTaken()){
                    mSetupTaken++;
                }
            }
        }
        mUsedTime = pUsedTime;
        mLib = pLib;
    }

    private void sGroupBorders(){
        int lGroupCount;
        int lCellCount;
        int lCellNr;
        Group lGroup;
        Cell lCell;

        for (lGroupCount = 0; lGroupCount < mGroups.size(); lGroupCount++){
            lGroup = mGroups.get(lGroupCount);
            for (lCellCount = 0; lCellCount < lGroup.xSize(); lCellCount++){
                lCellNr = lGroup.xCell(lCellCount);
                for (PlayField lPlayField:mPlayFields) {
                    lCell = lPlayField.xCell(lCellNr);
                    lCell.xGroup(lGroupCount);
                    lCell.xBndLeft(sLeftBorder(lCellNr, lGroup));
                    lCell.xBndRight(sRightBorder(lCellNr, lGroup));
                    lCell.xBndTop(sTopBorder(lCellNr, lGroup));
                    lCell.xBndBottom(sBottomBorder(lCellNr, lGroup));
                }
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

    List<PlayField> xPlayFields(){
        return mPlayFields;
    }

    int xFieldCount(){
        return mPlayFields.size();
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
        return mLib;
    }

    void xToLib(){
        mLib = true;
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
        Cell lCell;

        mPlayField.xResetConflicts();
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
                    lCell = mPlayField.xCell(lCellNr);
                    lCell.xGroup(mGroups.size());

                    lCell.xBndLeft(!mSetupGroup.xContains(lCellNr - 1));
                    lCell.xBndRight(!mSetupGroup.xContains(lCellNr + 1));
                    lCell.xBndTop(!mSetupGroup.xContains(lCellNr - mColumns));
                    lCell.xBndBottom(!mSetupGroup.xContains(lCellNr + mColumns));
                    lCell.xSetupSel(false);
                    lCell.xSetupTaken(true);
                }
                mSetupTaken += mSetupGroup.xSize();
                mGroups.add(mSetupGroup);
                mSetupGroup = null;
                if (mSetupTaken >= mRows * mColumns){
                    mGameStatus = cStatusSetupValues;
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

    Cell xPlayCell(int pRow, int pColumn){
        return mPlayField.xCell(pRow * mColumns + pColumn);
    }

    void xSelection(int pRow, int pColumn){
        int lCellNr;

        lCellNr = pRow * mColumns + pColumn;
        if (mGameStatus == cStatusSetupGroups){
            if (!mPlayField.xCell(lCellNr).xSetupTaken()){
                mPlayField.xCell(lCellNr).xConflict(false);
                if (mSetupGroup == null){
                    mSetupGroup = new Group();
                }
                if (mSetupGroup.xContains(lCellNr)){
                    if (mSetupGroup.xDelete(lCellNr)){
                        mPlayField.xCell(lCellNr).xSetupSel(false);
                    }
                } else {
                    if (mSetupGroup.xSize() < mMaxValue){
                        if (mSetupGroup.xAdd(lCellNr)){
                            mPlayField.xCell(lCellNr).xSetupSel(true);
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

    void xPlayFieldCopy(){
        int lNewId;
        PlayField lField;

        lNewId = mPlayFields.get(mPlayFields.size() - 1).xFieldId() + 1;
        lField = new PlayField(lNewId, mPlayField);
        mPlayFields.add(lField);
        mPlayField = lField;
    }

    void xSwitchPlayField(int pNewId){
        for (PlayField lField : mPlayFields){
            if (lField.xFieldId() == pNewId){
                mPlayField = lField;
                break;
            }
        }
    }

    void xDeleteCurrentPlayField(){
        if (mPlayField.xFieldId() != 0){
            if (mPlayFields.size() > 1){
                mPlayFields.remove(mPlayField);
                mPlayField = mPlayFields.get(mPlayFields.size() - 1);
            }
        }
    }

 /*   void xGenerate(){
        Generator lGenerator;

        lGenerator = new Generator(mRows, mColumns);
        lGenerator.xGenerate();
        mPlayField = lGenerator.xPlayField();
        mGroups = lGenerator.xGroups();
    } */

    void xNewGame(LibGame pLibGame){
        int lCount;
        String lWork;
        int lValue;
        Cell lCell;
        Group lGroup;
        int lMax;
        int lSize;

        mGameStatus = cStatusNone;
        mRows = pLibGame.xRows();
        mColumns = pLibGame.xColumns();
        mGroups.clear();
        mPlayField = new PlayField(mRows * mColumns);
        mPlayFields.clear();
        mPlayFields.add(mPlayField);

        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            lCell = mPlayField.xCell(lCount);
            lWork = pLibGame.xContent().substring((lCount * 3), (lCount * 3) + 1);
            lValue = Integer.parseInt(lWork);
            if (lValue > 0){
                lCell.xValue(lValue);
                lCell.xFixed(true);
            }
            lWork = pLibGame.xContent().substring((lCount * 3) + 1, (lCount * 3) + 3);
            lValue = Integer.parseInt(lWork);
            while (lValue >= mGroups.size()){
                mGroups.add(new Group());
            }
            lGroup = mGroups.get(lValue);
            lGroup.xAdd(lCount);
            lCell.xGroup(lValue);
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
        mLib = true;
    }

    void xStartSetUp(int pRows, int pColumns, int pMaxValue) {
        mGameStatus = cStatusSetupGroups;
        mRows = pRows;
        mColumns = pColumns;
        mMaxValue = pMaxValue;
        mSetupTaken = 0;
        mGroups.clear();
        mPlayField = new PlayField(mRows * mColumns);
        mPlayFields.clear();
        mPlayFields.add(mPlayField);
    }

    boolean xFinishSetup(){
        Cell lCell;
        int lCount;
        boolean lResult;

        lResult = sCheckGame();
        if (lResult){
            for (lCount = 0; lCount < mRows * mColumns; lCount++){
                lCell = mPlayField.xCell(lCount);
                if (lCell.xValue() > 0){
                    lCell.xFixed(true);
                }
                lCell.xSetupTaken(false);
                lCell.xSetupSel(false);
            }
        }
        mLib = false;
        return lResult;
    }

    void xStartGame() {
        mGameStatus = cStatusPlay;
        xResetUsedTime();
    }

    void xReset(){
        mPlayField = mPlayFields.get(0);
        mPlayFields.clear();
        mPlayFields.add(mPlayField);
        mPlayField.xResetField();
        mGameStatus = cStatusPlay;
        mUsedTime = 0;
    }

    void xProcessDigit(int pDigit) {
        Cell lCell;
        Boolean lDeletePencils;

        if (pDigit >= 1 && pDigit <= mMaxValue) {
            lCell = mPlayField.xSelectedCell();
            if (mPlayField.xPencilMode()) {
                if (lCell.xValue() == 0){
                    lCell.xPencilFlip(pDigit);
                }
            } else {
                if (!lCell.xFixed()) {
                    lDeletePencils = mPlayField.xSelectedCell().xValue() > 0;
                    if (mPlayField.xSetSelectedCellValue(pDigit)){
                        if (sCheckGame()){
                            if (mPlayField.xPencilAuto()){
                                sAdjustGroup(mPlayField.xSelection(), pDigit);
                                sAdjustSurr(mPlayField.xSelection(), pDigit);
                            }
                            if (mPlayField.xFieldFull()){
                                mGameStatus = cStatusSolved;
                            }
                        }
                    } else {
                        sCheckGame();
                    }
                    if (lDeletePencils && mPlayField.xPencilAuto()){
                        mPlayField.xClearPencil();
                    }
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
        Cell lCell;
        Group lGroup;

        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            lCell = mPlayField.xCell(lCount);
            if (lCell.xValue() > 0){
                lCell.xClearPencils();
            } else {
                lGroup = mGroups.get(lCell.xGroup());
                lCell.xSetPencils(lGroup.xSize());
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

        lGroup = mGroups.get(mPlayField.xCell(pCellNr).xGroup());
        for (lCount = 0; lCount < lGroup.xSize(); lCount++){
            mPlayField.xCell(lGroup.xCell(lCount)).xPencil(pPencil, false);
        }
    }

    void sAdjustSurr(int pCellNr, int pPencil){
        int lStartRow;
        int lEndRow;
        int lRow;
        int lStartColumn;
        int lEndColumn;
        int lColumn;

        lRow = pCellNr / mColumns;
        lColumn = pCellNr % mColumns;
        if (lRow > 0){lStartRow = lRow - 1;} else {lStartRow = lRow;}
        if (lRow < mRows -1){lEndRow = lRow + 1;} else {lEndRow = lRow;}
        if (lColumn > 0){lStartColumn = lColumn - 1;} else {lStartColumn = lColumn;}
        if (lColumn < mColumns - 1){lEndColumn = lColumn + 1;} else {lEndColumn = lColumn;}

        for (lRow = lStartRow; lRow <= lEndRow; lRow++){
            for (lColumn = lStartColumn; lColumn <= lEndColumn; lColumn++){
                mPlayField.xCell(lRow * mColumns + lColumn).xPencil(pPencil, false);
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
        Cell lTestCell;
        Cell lCompCell;

        lResult = true;
        lTestCell = mPlayField.xCell(pCellNr);
        lGroup = mGroups.get(lTestCell.xGroup());
        if (lTestCell.xValue() > lGroup.xSize()){
            lTestCell.xConflict(true);
            lResult = false;
        } else {
            for (lGroupCount = 0; lGroupCount < lGroup.xSize(); lGroupCount++){
                lCompCellNr = lGroup.xCell(lGroupCount);
                lCompCell = mPlayField.xCell(lCompCellNr);
                if (lCompCell != lTestCell){
                    if (lTestCell.xValue() == lCompCell.xValue()){
                        lTestCell.xConflict(true);
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
        Cell lTestCell;
        Cell lCompCell;

        if (pRow > 0){lStartRow = pRow - 1;} else {lStartRow = pRow;}
        if (pRow < mRows -1){lEndRow = pRow + 1;} else {lEndRow = pRow;}
        if (pColumn > 0){lStartColumn = pColumn - 1;} else {lStartColumn = pColumn;}
        if (pColumn < mColumns - 1){lEndColumn = pColumn + 1;} else {lEndColumn = pColumn;}

        lResult = true;
        lTestCell = mPlayField.xCell(pRow * mColumns + pColumn);
        for (lRow = lStartRow; lRow <= lEndRow; lRow++){
            for (lColumn = lStartColumn; lColumn <= lEndColumn; lColumn++){
                lCompCell = mPlayField.xCell(lRow * mColumns + lColumn);
                if (lTestCell != lCompCell){
                    if (lTestCell.xValue() == lCompCell.xValue()){
                        lTestCell.xConflict(true);
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
        Cell lCell;

        lResult = new StringBuilder();
        for (lCount = 0; lCount < mRows * mColumns; lCount++){
            lCell = mPlayField.xCell(lCount);
            if (lCell.xFixed()){
                lResult.append(String.format("%01d", lCell.xValue()));
            } else {
                lResult.append("0");
            }
            lResult.append(String.format("%02d", lCell.xGroup()));
        }
        return lResult.toString();
    }
}
