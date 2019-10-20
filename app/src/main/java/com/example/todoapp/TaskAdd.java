package com.example.todoapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static androidx.core.os.LocaleListCompat.create;


public class TaskAdd extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    DBHelper db;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    int mStartYear = 0, mStartMonth = 0, mStartDay = 0;
    int mStartHour = 0, mStartMin = 0, mStartSec = 0;
    String mDateFinal;
    String mTimeFinal;
    String mNameFinal;
    String mDescFinal;

    Intent mIntent;
    Boolean mIsUpdate;
    String mId;

    public String todayDateString() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());
        return mDateFormat.toString();
    }

    public String todayTimeString(){
        SimpleDateFormat mTimeFormat = new SimpleDateFormat("K:mm", Locale.getDefault());
        return mTimeFormat.toString();
    }


    public void initUpdate() {
        mId = mIntent.getStringExtra("id");
        TextView toolbar_task_add_title = findViewById(R.id.toolbar_task_add_title);
        ImageView toolbar_task_delete = findViewById(R.id.toolbar_task_delete);
        EditText task_name = findViewById(R.id.task_name);
        EditText task_date = findViewById(R.id.task_date);
        EditText task_time = findViewById(R.id.task_time);
        EditText task_desc = findViewById(R.id.task_desc);
        toolbar_task_add_title.setText(getString(R.string.update));
        toolbar_task_delete.setVisibility(View.VISIBLE);
        Cursor task = db.getDataSpecific(mId);
        if (task != null) {
            task.moveToFirst();

            task_name.setText(task.getString(1));
            Calendar mCal = DateFunctions.Epoch2Calender(task.getString(2));
            mStartYear = mCal.get(Calendar.YEAR);
            mStartMonth = mCal.get(Calendar.MONTH);
            mStartDay = mCal.get(Calendar.DAY_OF_MONTH);
            task_date.setText(DateFunctions.Epoch2DateString(task.getString(2), "dd/MM/yyyy"));
            Calendar mCal1 = DateFunctions.Epoch2Calender(task.getString(3));
            mStartHour = mCal1.get(Calendar.HOUR_OF_DAY);
            mStartMin = mCal1.get(Calendar.MINUTE);
            task_time.setText(DateFunctions.Epoch2TimeString(task.getString(3), "K:mm"));
            task_desc.setText(task.getString(4));

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task);

        db = new DBHelper(getApplicationContext());
        mIntent = getIntent();
        mIsUpdate = mIntent.getBooleanExtra("isUpdate", false);

        mDateFinal = todayDateString();
        mTimeFinal = todayTimeString();
        Date mDate = new Date();
        Calendar mCal = Calendar.getInstance();
        mCal.setTime(mDate);
        mStartYear = mCal.get(Calendar.YEAR);
        mStartMonth = mCal.get(Calendar.MONTH);
        mStartDay = mCal.get(Calendar.DAY_OF_MONTH);
        mStartHour = mCal.get(Calendar.HOUR_OF_DAY);
        mStartMin = mCal.get(Calendar.MINUTE);
        //mStartSec = mCal.get(Calendar.SECOND);

        if (mIsUpdate) {
            initUpdate();
        }
    }

    public void closeAddTask(View v) {
        finish();
    }

    public void doneAddTask(View v) {
        EditText task_name = findViewById(R.id.task_name);
        EditText task_date = findViewById(R.id.task_date);
        EditText task_time = findViewById(R.id.task_time);
        EditText task_desc = findViewById(R.id.task_desc);
        mNameFinal = task_name.getText().toString();
        mDateFinal = task_date.getText().toString();
        mTimeFinal = task_time.getText().toString();
        mDescFinal = task_desc.getText().toString();


        /* Checking */
        int errorStep = 0;
        if (mNameFinal.trim().length() < 1) {
            errorStep++;
            task_name.setError("Provide a task name");
        }

        if (mDateFinal.trim().length() < 4) {
            errorStep++;
            task_date.setError("Provide a specific date");
        }

        /*if (mTimeFinal.trim().length() < 1) {
            errorStep++;
            task_time.setError("Provide a specific time");
        }*/

        if (mDescFinal.trim().length() < 1) {
            errorStep++;
            task_desc.setError("Provide a specific description");
        }


        if (errorStep == 0) {
            if (mIsUpdate) {
                db.updateContact(mId, mNameFinal, mDateFinal, mTimeFinal, mDescFinal);
                Toast.makeText(getApplicationContext(), "Task Updated.", Toast.LENGTH_SHORT).show();
            } else {
                db.insertContact(mNameFinal, mDateFinal, mTimeFinal, mDescFinal);
                Toast.makeText(getApplicationContext(), "Task Added.", Toast.LENGTH_SHORT).show();
            }

            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag("Datepickerdialog");
        TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("Timepickerdialog");

        if(tpd != null) tpd.setOnTimeSetListener(this);
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tpd = null;
        dpd = null;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mStartYear = year;
        mStartMonth = monthOfYear;
        mStartDay = dayOfMonth;
        int monthAddOne = mStartMonth + 1;
        String date = (mStartDay < 10 ? "0" + mStartDay : "" + mStartDay) + "/" +
                (monthAddOne < 10 ? "0" + monthAddOne : "" + monthAddOne) + "/" +
                mStartYear;
        EditText task_date = findViewById(R.id.task_date);
        task_date.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mStartHour = hourOfDay;
        mStartMin = minute;
        //mStartSec = second;
        String time = (mStartHour < 10 ? "0" + mStartHour : "" + mStartHour) + ":" +
                (mStartMin < 10 ? "0" + mStartMin : "" + mStartMin); 
                //+ ":" + (mStartSec < 10 ? "0" + mStartSec : "" + mStartSec);
        EditText task_time = findViewById(R.id.task_time);
        task_time.setText(time);
    }

    public void showStartDatePicker(View view) {
        dpd = DatePickerDialog.newInstance(TaskAdd.this, mStartYear, mStartMonth, mStartDay);
        dpd.setOnDateSetListener(this);
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    public void showStartTimePicker(View view) {
        tpd = TimePickerDialog.newInstance(TaskAdd.this, mStartHour, mStartMin,true);
        tpd.setOnTimeSetListener(this);
        tpd.show(getSupportFragmentManager(), "Timepickerdialog");
    }

    public void deleteTask(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (db.deleteContact(mId)) {
                finish();
                Toast.makeText(getApplicationContext(), "Task Deleted.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        builder.setNegativeButton("No", (dialog, which) -> {

            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();

    }



}
