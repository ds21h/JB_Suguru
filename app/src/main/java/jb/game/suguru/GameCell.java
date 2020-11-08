package jb.game.suguru;

class GameCell {
    private int mGroup;
    private boolean mBndLeft;
    private boolean mBndRight;
    private boolean mBndTop;
    private boolean mBndBottom;
    private boolean mSetupSel;
    private boolean mSetupTaken;

   GameCell(){
       mGroup = -1;
       mBndLeft = false;
       mBndRight = false;
       mBndTop = false;
       mBndBottom = false;
       mSetupSel = false;
       mSetupTaken = false;
   }

   GameCell(GameCell pGrp){
       mGroup = pGrp.mGroup;
       mBndLeft = pGrp.mBndLeft;
       mBndRight = pGrp.mBndRight;
       mBndTop = pGrp.mBndTop;
       mBndBottom = pGrp.mBndBottom;
       mSetupSel = pGrp.mSetupSel;
       mSetupTaken = pGrp.mSetupTaken;
   }

    int xGroup(){
        return mGroup;
    }

    void xGroup(int pGroup){
        mGroup = pGroup;
    }

    boolean xBndLeft(){
        return mBndLeft;
    }

    void xBndLeft(boolean pBnd){
        mBndLeft = pBnd;
    }

    boolean xBndRight(){
        return mBndRight;
    }

    void xBndRight(boolean pBnd){
        mBndRight = pBnd;
    }

    boolean xBndTop(){
        return mBndTop;
    }

    void xBndTop(boolean pBnd){
        mBndTop = pBnd;
    }

    boolean xBndBottom(){
        return mBndBottom;
    }

    void xBndBottom(boolean pBnd){
        mBndBottom = pBnd;
    }

    boolean xSetupSel(){
        return mSetupSel;
    }

    void xSetupSel(boolean pSetupSel){
        mSetupSel = pSetupSel;
    }

    boolean xSetupTaken(){
        return mSetupTaken;
    }

    void xSetupTaken(boolean pSetupTaken){
        mSetupTaken = pSetupTaken;
    }
}
