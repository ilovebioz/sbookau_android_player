package com.sectic.sbookau.ultils;

import android.widget.EditText;

public class ValidUtils {

    public static boolean validEmail(EditText iOTxtEmail) {
        boolean valid = true;
        String sEmail = iOTxtEmail.getText().toString();
        if (sEmail.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            iOTxtEmail.setError("email không hợp lệ");
            valid = false;
        } else {
            iOTxtEmail.setError(null);
        }
        return valid;
    }

    public static boolean validLength(EditText iOTxt, int minLength, int maxLength, String iSError) {
        boolean valid = true;
        String sTmp = iOTxt.getText().toString();
        if (sTmp.trim().isEmpty() || sTmp.length() < minLength || sTmp.length() > maxLength) {
            iOTxt.setError(String.format(iSError, minLength, maxLength));
            valid = false;
        } else {
            iOTxt.setError(null);
        }
        return valid;
    }

    public static boolean validValue(EditText iOTxt, int minValue, int maxValue, String iSError) {
        boolean valid = true;
        try {
            String sTmp = iOTxt.getText().toString();
            if (sTmp.trim().isEmpty() || Integer.valueOf(sTmp) < minValue ||  Integer.valueOf(sTmp) > maxValue) {
                iOTxt.setError(String.format(iSError, minValue, maxValue));
                valid = false;
            } else {
                iOTxt.setError(null);
            }
        }catch(Exception e){
            valid = false;
        }
        return valid;
    }

    public static String validDefault(EditText iOTxt, String iSDefault) {
        String sTmp = iOTxt.getText().toString();
        sTmp = (sTmp.isEmpty()) ? iSDefault : sTmp;
        return sTmp;
    }
}

