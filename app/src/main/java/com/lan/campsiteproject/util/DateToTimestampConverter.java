package com.lan.campsiteproject.util;

import android.util.Log;
import java.sql.Timestamp;
import java.util.Date;

public class DateToTimestampConverter {
    private static final String TAG = "DateToTimestampConverter";

    public static Timestamp dateToTimestamp(Object dateObj) {
        if (dateObj == null) {
            return null;
        }

        try {
            // Nếu đã là Timestamp thì return luôn
            if (dateObj instanceof Timestamp) {
                return (Timestamp) dateObj;
            }

            // Nếu là java.util.Date
            if (dateObj instanceof Date) {
                return new Timestamp(((Date) dateObj).getTime());
            }

            // Nếu là Long (timestamp dạng milliseconds)
            if (dateObj instanceof Long) {
                return new Timestamp((Long) dateObj);
            }

            // Nếu là com.google.firebase.Timestamp
            if (dateObj.getClass().getName().equals("com.google.firebase.Timestamp")) {
                // Sử dụng reflection để lấy giá trị
                java.lang.reflect.Method toDateMethod = dateObj.getClass().getMethod("toDate");
                Date date = (Date) toDateMethod.invoke(dateObj);
                return new Timestamp(date.getTime());
            }

            Log.w(TAG, "Unknown date type: " + dateObj.getClass().getName());
            return null;

        } catch (Exception e) {
            Log.e(TAG, "Error converting date to timestamp: " + e.getMessage(), e);
            return null;
        }
    }
}