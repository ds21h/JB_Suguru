package jb.game.suguru;

import java.util.ArrayList;
import java.util.List;

class SuguruGame extends SuguruGameBase {
    private List<PlayField> mPlayFields;

    SuguruGame(){
        super();
        mPlayFields = new ArrayList<>();
        mPlayFields.add(xPlayField());
    }

    SuguruGame(List<Group> pGroups, List<PlayField> pFields, int pGameRows, int pGameColumns,
               int pMaxValue, int pStatus, int pDifficulty, boolean pLibSolved, int pBatchId, int pGameId, int pSelectedField, int pUsedTime){
        super();

        PlayField lPlayField;

        mPlayFields = pFields;
        if (mPlayFields.isEmpty()) {
            lPlayField = new PlayField(pGameRows * pGameColumns);
        } else {
            lPlayField = pFields.get(0);
            if (lPlayField.xFieldId() != pSelectedField){
                for (PlayField lField : mPlayFields) {
                    if (lField.xFieldId() == pSelectedField){
                        lPlayField = lField;
                        break;
                    }
                }
            }
        }
        xInitGame(pGroups, lPlayField, pGameRows, pGameColumns, pMaxValue, pStatus, pDifficulty, pLibSolved, pBatchId, pGameId, pUsedTime);
    }

    List<PlayField> xPlayFields(){
        return mPlayFields;
    }

    int xFieldCount(){
        return mPlayFields.size();
    }

    void xPlayFieldCopy(){
        int lNewId;
        PlayField lField;

        lNewId = mPlayFields.get(mPlayFields.size() - 1).xFieldId() + 1;
        lField = new PlayField(lNewId, xPlayField());
        mPlayFields.add(lField);
        xPlayField(lField);
    }

    void xSwitchPlayField(int pNewId){
        for (PlayField lField : mPlayFields){
            if (lField.xFieldId() == pNewId){
                xPlayField(lField);
                break;
            }
        }
    }

    void xDeleteCurrentPlayField(){
        if (xPlayField().xFieldId() != 0){
            if (mPlayFields.size() > 1){
                mPlayFields.remove(xPlayField());
                xPlayField(mPlayFields.get(mPlayFields.size() - 1));
            }
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

        super.xNewGame(pLibGame);
        mPlayFields.clear();
        mPlayFields.add(xPlayField());
    }

    void xStartSetUp(int pRows, int pColumns, int pMaxValue) {
        super.xStartSetUp(pRows, pColumns, pMaxValue);
        mPlayFields.clear();
        mPlayFields.add(xPlayField());
    }

    void xReset(){
        xPlayField(mPlayFields.get(0));
        super.xReset();
        mPlayFields.clear();
        mPlayFields.add(xPlayField());
    }
}
