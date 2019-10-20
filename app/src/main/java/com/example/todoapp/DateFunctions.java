package com.example.todoapp;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFunctions {
    static String Epoch2DateString(String mEpochSeconds, String mFormat) {
        Date mUpdateDate = new Date(Long.parseLong(mEpochSeconds));
        SimpleDateFormat mSDF = new SimpleDateFormat(mFormat, Locale.getDefault());
        mSDF.setTimeZone(TimeZone.getDefault());
        return mSDF.format(mUpdateDate);
    }

    static String Epoch2TimeString(String mEpochSeconds, String mFormat) {
        Time mUpdateTime = new Time(Long.parseLong(mEpochSeconds));
        SimpleDateFormat mSDF = new SimpleDateFormat(mFormat, Locale.getDefault());
        mSDF.setTimeZone(TimeZone.getDefault());
        return mSDF.format(mUpdateTime);
    }

    static Calendar Epoch2Calender(String mEpochSeconds) {
        Date mUpdateDate = new Date(Long.parseLong(mEpochSeconds));
        Calendar mCal = Calendar.getInstance();
        mCal.setTime(mUpdateDate);

        return mCal;
    }

}
