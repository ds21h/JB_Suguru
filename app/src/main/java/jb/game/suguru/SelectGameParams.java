package jb.game.suguru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SelectGameParams extends Activity {
    static final String cRows = "Rows";
    static final String cColumns = "Columns";
    static final String cMaxValue = "MaxValue";

    EditText mEdtRows;
    EditText mEdtColumns;
    EditText mEdtMaxValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_params_layout);

        Intent lInt;
        Bundle lBundle;

        mEdtRows = findViewById(R.id.txtRows);
        mEdtRows.addTextChangedListener(new SingleDigit(mEdtRows));
        mEdtColumns = findViewById(R.id.txtColumns);
        mEdtColumns.addTextChangedListener(new SingleDigit(mEdtColumns));
        mEdtMaxValue = findViewById(R.id.txtMaxValue);
        mEdtMaxValue.addTextChangedListener(new SingleDigit(mEdtMaxValue));
        if (savedInstanceState == null) {
            lInt = getIntent();
            lBundle = lInt.getExtras();
            if (lBundle == null) {
                finish();
            } else {
                mEdtRows.setText(String.valueOf(lBundle.getInt(cRows)));
                mEdtColumns.setText(String.valueOf(lBundle.getInt(cColumns)));
                mEdtMaxValue.setText(String.valueOf(lBundle.getInt(cMaxValue)));
            }
        }
    }

    public void hParamSet(View pVw){
        Intent lInt;
        Bundle lBundle;

        lInt = new Intent();
        lBundle = new Bundle();
        lBundle.putInt(cRows, sReturnValue(mEdtRows));
        lBundle.putInt(cColumns, sReturnValue(mEdtColumns));
        lBundle.putInt(cMaxValue, sReturnValue(mEdtMaxValue));
        lInt.putExtras(lBundle);
        setResult(RESULT_OK, lInt);
        finish();
    }

    private int sReturnValue(EditText pText){
        String lValue;
        int lIntValue;

        lValue = pText.getText().toString();
        if (lValue.equals("?")){
            lIntValue = -1;
        } else {
            try {
                lIntValue = Integer.parseInt(lValue);
            } catch (NumberFormatException pExc){
                lIntValue = -1;
            }
        }
        return lIntValue;
    }
}