package jb.game.suguru;

class LibGame {
    private int mBatchId;
    private int mGameId;
    private int mDifficulty;
    private boolean mSolved;
    private int mRows;
    private int mColumns;
    private String mContent;
    private boolean mValid;

    LibGame(int pBatchId, int pGameId, int pDifficulty, boolean pSolved, int pRows, int pColumns, String pContent){
        mBatchId = pBatchId;
        mGameId = pGameId;
        mDifficulty = pDifficulty;
        mSolved = pSolved;
        mRows = pRows;
        mColumns = pColumns;
        mContent = pContent;
        mValid = true;
    }

    LibGame(String pGame){
        String lWork;

        if (pGame.length() > 40){
            try {
                lWork = pGame.substring(0, 4);
                mBatchId = Integer.parseInt(lWork);
                lWork = pGame.substring(4, 8);
                mGameId = Integer.parseInt(lWork);
                lWork = pGame.substring(8, 9);
                mDifficulty = Integer.parseInt(lWork);
                lWork = pGame.substring(9, 10);
                mRows = Integer.parseInt(lWork);
                lWork = pGame.substring(10,11);
                mColumns = Integer.parseInt(lWork);
                if (pGame.length() == mRows * mColumns * 3 + 11){
                    mContent = pGame.substring(11);
                    mValid = true;
                } else {
                    mContent = "";
                    mValid = false;
                }
            } catch (NumberFormatException pExc){
                sInitInvalid();
            }
        } else {
            sInitInvalid();
        }
    }

    private void sInitInvalid(){
        mBatchId = 0;
        mGameId = 0;
        mDifficulty = 0;
        mRows = 0;
        mColumns = 0;
        mContent = "";
        mValid = false;
    }

    boolean xValid(){
        return mValid;
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

    boolean xSolved(){
        return mSolved;
    }

    int xRows(){
        return mRows;
    }

    int xColumns(){
        return mColumns;
    }

    String xContent(){
        return mContent;
    }
}
