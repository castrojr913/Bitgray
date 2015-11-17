package com.jacr.photoapp.utilities;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DateHelper
 * Created by Jesus Castro on 16/11/2015.
 */
public class DateHelper {

    public static String dateToString(@NonNull Date date, @NonNull String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    public static Date stringToDate(@NonNull String string, @NonNull String format) {
        Date result = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
            result = formatter.parse(string);
        } catch (ParseException e) {
            LogHelper.getInstance().exception(DateHelper.class, e, e.getMessage());
        }
        return result;
    }

}
