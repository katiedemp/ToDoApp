package com.example.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;


public class TaskHome extends AppCompatActivity {

    public static String KEY_ID = "id";
    public static String KEY_TASK = "task";
    public static String KEY_DATE = "date";
    public static String KEY_TIME = "time";
    public static String KEY_DESC = "description";
    Activity mActivity;
    Toolbar mMenuToolbar;
    ProgressBar mProgressBar;
    NoScrollListView mTaskListToday, mTaskListTomorrow, mTaskListUpcoming;
    NestedScrollView mScrollView;
    DBHelper mDB;
    TextView mTodayText, mTomorrowText, mUpcomingText;
    ArrayList<HashMap<String, String>> mTodayList = new ArrayList<>();
    ArrayList<HashMap<String, String>> mTomorrowList = new ArrayList<>();
    ArrayList<HashMap<String, String>> mUpcomingList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_home);

        mActivity = TaskHome.this;
        mMenuToolbar = findViewById(R.id.task_toolbar);
        setSupportActionBar(mMenuToolbar);
        mDB = new DBHelper(mActivity);
        mScrollView = findViewById(R.id.scrollView);
        mProgressBar = findViewById(R.id.progressBar);
        mTaskListToday = findViewById(R.id.taskListToday);
        mTaskListTomorrow = findViewById(R.id.taskListTomorrow);
        mTaskListUpcoming = findViewById(R.id.taskListUpcoming);
        mTodayText = findViewById(R.id.todayText);
        mTomorrowText = findViewById(R.id.tomorrowText);
        mUpcomingText = findViewById(R.id.upcomingText);
    }

    public void populateData() {
        mDB = new DBHelper(mActivity);
        mScrollView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        new LoadTask(this).execute();
    }

    public void openAddTask(View v) {
        Intent i = new Intent(this, TaskAdd.class);
        startActivity(i);
    }

    public void loadDataList(Cursor mCursor, ArrayList<HashMap<String, String>> mDataList) {
        if (mCursor != null) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                HashMap<String, String> mMapToday = new HashMap<>();
                mMapToday.put(KEY_ID, mCursor.getString(0));
                mMapToday.put(KEY_TASK, mCursor.getString(1));
                mMapToday.put(KEY_DATE, DateFunctions.Epoch2DateString(mCursor.getString(2), "dd/MM/yyyy"));
                mMapToday.put(KEY_TIME, DateFunctions.Epoch2TimeString(mCursor.getString(3), "K:mm"));
                mMapToday.put(KEY_DESC, mCursor.getString(4));
                mDataList.add(mMapToday);
                mCursor.moveToNext();
            }
        }
    }

    public void loadListView(ListView mListView, final ArrayList<HashMap<String, String>> mDataList) {
        TaskListAdapter mAdapter = new TaskListAdapter(mActivity, mDataList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(mActivity, TaskAdd.class);
            i.putExtra("isUpdate", true);
            i.putExtra("id", mDataList.get(+position).get(KEY_ID));
            startActivity(i);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            case R.id.action_reset:
                resetTasks();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void resetTasks(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (mDB.clearContact()) {
                recreate();
                Toast.makeText(getApplicationContext(), "Tasks Reset", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        builder.setNegativeButton("No", (dialog, which) -> {

            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        populateData();
    }

    @Override
    public void onDestroy() {
        mDB.close();
        super.onDestroy();
    }


    private static class LoadTask extends AsyncTask<String, Void, String> {

        private WeakReference<TaskHome> activityReference;

        // Retain a weak reference to the activity
        LoadTask(TaskHome context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            TaskHome activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            super.onPreExecute();
            activity.mTodayList.clear();
            activity.mTomorrowList.clear();
            activity.mUpcomingList.clear();
        }

        protected String doInBackground(String... args) {
            TaskHome activity = activityReference.get();
            String xml = "";

            Cursor mToday = activity.mDB.getDataToday();
            activity.loadDataList(mToday, activity.mTodayList);

            Cursor mTomorrow = activity.mDB.getDataTomorrow();
            activity.loadDataList(mTomorrow, activity.mTomorrowList);

            Cursor mUpcoming = activity.mDB.getDataUpcoming();
            activity.loadDataList(mUpcoming, activity.mUpcomingList);

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            TaskHome activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.loadListView(activity.mTaskListToday, activity.mTodayList);
            activity.loadListView(activity.mTaskListTomorrow, activity.mTomorrowList);
            activity.loadListView(activity.mTaskListUpcoming, activity.mUpcomingList);

            if (activity.mTodayList.size() > 0) {
                activity.mTodayText.setVisibility(View.VISIBLE);
            } else {
                activity.mTodayText.setVisibility(View.GONE);
            }

            if (activity.mTomorrowList.size() > 0) {
                activity.mTomorrowText.setVisibility(View.VISIBLE);
            } else {
                activity.mTomorrowText.setVisibility(View.GONE);
            }

            if (activity.mUpcomingList.size() > 0) {
                activity.mUpcomingText.setVisibility(View.VISIBLE);
            } else {
                activity.mUpcomingText.setVisibility(View.GONE);
            }

            activity.mProgressBar.setVisibility(View.GONE);
            activity.mScrollView.setVisibility(View.VISIBLE);
        }
    }

}
