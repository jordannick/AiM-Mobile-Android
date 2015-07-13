package edu.oregonstate.AiMLiteMobile.Helpers;


import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import java.util.regex.Pattern;

public class InputFilterMinMax implements InputFilter {
    private final static String TAG = "AiM_InputFilterMinMax";

    private Pattern mPattern;

    public InputFilterMinMax(int precision, int scale) {
        String pattern="^\\-?(\\d{0," + (precision-scale) + "}|\\d{0," + (precision-scale) + "}\\.\\d{0," + scale + "})$";
        this.mPattern= Pattern.compile(pattern);

    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned destination, int destinationStart, int destinationEnd) {
        if (end > start) {
            // adding: filter
            // build the resulting text
            String destinationString = destination.toString();
            String resultingTxt = destinationString.substring(0, destinationStart) + source.subSequence(start, end) + destinationString.substring(destinationEnd);
            // return null to accept the input or empty to reject it
            return resultingTxt.matches(this.mPattern.toString()) ? null : "";
        }
        // removing: always accept
        return null;
    }
   /* private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
            Log.e(TAG, nfe.toString());
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }*/
}