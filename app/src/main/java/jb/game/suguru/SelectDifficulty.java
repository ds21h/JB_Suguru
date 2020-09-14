package jb.game.suguru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class SelectDifficulty extends Activity {
    static final String cLevel = "Level";
    private int mDifficulty;
    RadioButton[] mButtons = new RadioButton[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_difficulty_layout);

        Intent lIntent;
        Bundle lBundle;

        if (savedInstanceState == null){
            lIntent = getIntent();
            lBundle = lIntent.getExtras();
            if (lBundle == null){
                mDifficulty = 0;
            } else {
                mDifficulty = lBundle.getInt(cLevel, 0);
            }
        } else {
            mDifficulty = savedInstanceState.getInt(cLevel, 0);
        }
        mButtons[0] = findViewById(R.id.rdoVeryEasy);
        mButtons[1] = findViewById(R.id.rdoEasy);
        mButtons[2] = findViewById(R.id.rdoMedium);
        mButtons[3] = findViewById(R.id.rdoHard);
        mButtons[4] = findViewById(R.id.rdoVeryHard);
        if (mDifficulty > 0){
            mButtons[mDifficulty - 1].setChecked(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        sGetSelectedDifficulty();
        savedInstanceState.putInt(cLevel, mDifficulty);
    }

    private void sGetSelectedDifficulty(){
        int lCount;
        for (lCount = 0; lCount < mButtons.length; lCount++){
            if (mButtons[lCount].isChecked()){
                break;
            }
        }
        if (lCount < mButtons.length){
            mDifficulty = lCount + 1;
        } else {
            mDifficulty = 0;
        }
    }

    public void hOk_Click(View pView){
        Intent lInt;
        Bundle lBundle;

        sGetSelectedDifficulty();
        if (mDifficulty > 0){
            lInt = new Intent();
            lBundle = new Bundle();
            lBundle.putInt(cLevel, mDifficulty);
            lInt.putExtras(lBundle);
            setResult(RESULT_OK, lInt);
            finish();
        } else {
            Toast.makeText(this, R.string.msg_no_selection, Toast.LENGTH_SHORT).show();
        }
    }
}
