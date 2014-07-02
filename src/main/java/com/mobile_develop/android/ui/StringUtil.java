package com.mobile_develop.android.ui;

/**
 * Created by chris on 19/05/2014.
 */
public class StringUtil {

    public static String toString(CharSequence s) {
        String result;
        if( s == null ) {
            result = null;
        } else {
            result = s.toString();
        }
        return result;
    }

}
