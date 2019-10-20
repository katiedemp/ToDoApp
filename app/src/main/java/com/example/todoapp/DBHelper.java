package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DBHelper.db";
    private static final String CONTACTS_TABLE_NAME = "todolist";
    private static final String KEY_ROW_ID = "id";

    DBHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "CREATE TABLE " + CONTACTS_TABLE_NAME +
                            "(id INTEGER PRIMARY KEY, task TEXT, dateStr INTEGER, timeStr INTEGER, description TEXT)"
            );
        } catch (Exception e) {
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    private Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + CONTACTS_TABLE_NAME + " order by id desc", null);

    }

    Cursor getDataSpecific(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + CONTACTS_TABLE_NAME + " WHERE id = '" + id + "' order by id desc", null);

    }

    Cursor getDataToday() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + CONTACTS_TABLE_NAME +
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) = date('now', 'localtime') order by id desc", null);

    }


    Cursor getDataTomorrow() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + CONTACTS_TABLE_NAME +
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) = date('now', '+1 day', 'localtime')  order by id desc", null);

    }


    Cursor getDataUpcoming() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + CONTACTS_TABLE_NAME +
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) > date('now', '+1 day', 'localtime') order by id desc", null);

    }

    private long getDate(String day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());
        Date mDate = new Date();
        try {
            mDate = dateFormat.parse(day);
        } catch (ParseException ignored) {

        }
        return mDate.getTime();
    }

    private long getTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "K:mm", Locale.getDefault());
        Date mTime = new Date();
        try {
            mTime = dateFormat.parse(time);
        } catch (ParseException ignored) {

        }
        return mTime.getTime();
    }


    boolean insertContact(String task, String dateStr, String timeStr, String description) {
        Date date;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put("task", task);
        mContentValues.put("dateStr", getDate(dateStr));
        mContentValues.put("timeStr", getTime(timeStr));
        mContentValues.put("description", description);
        db.insert(CONTACTS_TABLE_NAME, null, mContentValues);
        return true;
    }

    boolean updateContact(String id, String task, String dateStr, String timeStr, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues mContentValues = new ContentValues();

        mContentValues.put("task", task);
        mContentValues.put("dateStr", getDate(dateStr));
        mContentValues.put("timeStr", getTime(timeStr));
        mContentValues.put("description", description);

        db.update(CONTACTS_TABLE_NAME, mContentValues, "id = ? ", new String[]{id});
        return true;
    }

    boolean deleteContact(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME, KEY_ROW_ID + "= ?", new String[]{id}) > 0;
    }

    public boolean clearContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
        return true;
    }
}
