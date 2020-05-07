package jb.game.suguru;

import android.app.Activity;
import android.os.Bundle;

public class MainSuguru extends Activity {
    private SuguruGame mGame;
    private SuguruView mSgrView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainsuguru_layout);

        mGame = new SuguruGame();
        mSgrView = findViewById(R.id.svMain);
        mSgrView.setGame(mGame);
    }
}
