package com.github.ttdyce.nhviewer.model.room;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class DateConverter {
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @TypeConverter
    public static Date fromDate(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToString(Date value) {
        if (value != null) {
            return df.format(value);
        } else {
            return null;
        }
    }


}
