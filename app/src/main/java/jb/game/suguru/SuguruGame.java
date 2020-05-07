package jb.game.suguru;

class SuguruGame {
    private int mRows;
    private int mColumns;
    private PlayField mPlayField;

    SuguruGame(){
        mRows = 10;
        mColumns = 8;
        mPlayField = new PlayField(mRows, mColumns);
    }

    int xRows(){
        return mRows;
    }

    int xColumns(){
        return mColumns;
    }

    PlayField xPlayField(){
        return mPlayField;
    }
}
