package jb.game.suguru;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

class SingleDigit implements TextWatcher {
    private EditText mEdtText;
    private String mValue;

    public SingleDigit(EditText pEdtText){
        mEdtText = pEdtText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence pCharSeq, int pStart, int before, int pCount) {
        int lValue;

        if (pCount < 1){
            mValue = "?";
        } else {
            mValue = String.valueOf(pCharSeq.charAt(pStart));
            if (!mValue.equals("?")){
                try{
                    lValue = Integer.parseInt(mValue);
                } catch (NumberFormatException pExc){
                    mValue = "?";
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable pCharSeq) {
        boolean lChange;

        lChange = true;
        if (pCharSeq.length() == 1){
            if (mValue.equals(String.valueOf(pCharSeq.charAt(0)))){
                lChange = false;
            }
        }
        if (lChange){
            mEdtText.setText(mValue);
        }
    }
}
