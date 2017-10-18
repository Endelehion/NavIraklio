package com.example.varda.naviraklio.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class InputValidation {
    private Context context;

    /**
     * constructor
     *
     * @param context
     */
    public InputValidation(Context context) {
        this.context = context;
    }

    /**
     * method to check InputEditText filled .
     *
     * @param textInputEditText
     * @param textInputLayout
     * @param message
     * @return
     */
    public boolean isInputEditTextFilled(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * method to check AutoCompleteTextView filled .
     *
     * @param textInputEditText
     * @param textInputLayout
     * @param message
     * @return
     */

    public boolean isAutoCompleteTextViewFilled(AutoCompleteTextView textInputEditText, TextInputLayout textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean isCreditCardValid(TextInputEditText creditField, TextInputLayout textInputLayout) {
        String creditString = creditField.getText().toString().trim();
        boolean creditValid = creditString.length() == 16 && isNumeric(creditString);
        if (!creditValid) {
            textInputLayout.setError("Invalid Credit Card Number");
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return creditValid;
    }


    public static boolean isNumeric(String str) {
        try {
            long d = Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean isTelNumberValid(TextInputEditText telNumberField, TextInputLayout textInputLayout) {
        String telString = telNumberField.getText().toString().trim();
        boolean telValid = telNumberField.length() <= 15 && telNumberField.length() >= 10 && isNumeric(telString);
        if (!telValid) {
            textInputLayout.setError("Invalid Phone Number");
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return telValid;
    }

    /**
     * method to check InputEditText has valid username .
     *
     * @param textInputEditText
     * @param textInputLayout
     * @param message
     * @return
     */
    /*public boolean isInputEditTextUsername(AutoCompleteTextView textInputEditText, TextInputLayout textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }*/
    public boolean isInputEditTextValidUsername(AutoCompleteTextView textInputEditText, TextInputLayout textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty() || textInputEditText.length() < 5) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * method to check InputEditText has valid password .
     *
     * @param textInputEditText
     * @param textInputLayout
     * @param message
     * @return
     */
    public boolean isInputEditTextValidPassword(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty() || textInputEditText.length() < 5) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextMatches(TextInputEditText textInputEditText1, TextInputEditText textInputEditText2, TextInputLayout textInputLayout, String message) {
        String value1 = textInputEditText1.getText().toString().trim();
        String value2 = textInputEditText2.getText().toString().trim();
        if (!value1.contentEquals(value2)) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText2);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * method to Hide keyboard
     *
     * @param view
     */
    private void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
