package com.example.aboubacrineseckprojet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityDBHelper extends SQLiteOpenHelper{



    public static class ActivityEntry implements BaseColumns {
        public static final String TABLE_NAME = "projetactivities";
        public static final String COLUMN_NAME_PROFILE_ID = "profile_id";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_ICON = "icon";
        public static final String COLUMN_NAME_DATE = "date";
    }
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "activity_db";
    public static final String CREATE_ENTRIES =
            "CREATE TABLE " + ActivityEntry.TABLE_NAME + " (" +
                    ActivityEntry._ID + " INTEGER PRIMARY KEY," +
                    ActivityEntry.COLUMN_NAME_PROFILE_ID + " INTEGER,"+
                    ActivityEntry.COLUMN_NAME_LABEL + " TEXT,"+
                    ActivityEntry.COLUMN_NAME_ICON + " INTEGER,"+
                    ActivityEntry.COLUMN_NAME_DATE + " DATE"+
                    ")";
    public static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ActivityEntry.TABLE_NAME;

    public ActivityDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
    class Activity{
        long id;
        long profileId;
        String label;
        int icon;
        Date date;
        public Activity(long id, long profileId, String label, int icon, Date date) {
            this.id = id;
            this.profileId = profileId;
            this.label = label;
            this.icon = icon;
            this.date = date;
        }
    }
    public boolean saveActivity(long profileId, String label, int icon, Date date) {
        return saveActivity(new Activity(-1,profileId,label,icon,date));
    }
    public int clearProfileActivities(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
                ActivityEntry.TABLE_NAME,
                ActivityEntry.COLUMN_NAME_PROFILE_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
    public boolean saveActivity(Activity activity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ActivityEntry.COLUMN_NAME_PROFILE_ID,activity.profileId);
        values.put(ActivityEntry.COLUMN_NAME_ICON,activity.icon);
        values.put(ActivityEntry.COLUMN_NAME_DATE,activity.date.getTime());
        values.put(ActivityEntry.COLUMN_NAME_LABEL,activity.label);
        long id = db.insert(ActivityEntry.TABLE_NAME,null,values);
        activity.id = id;
        return id > -1;
    }
    public List<Activity> getProfileActivities(long profileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                ActivityEntry._ID,
                ActivityEntry.COLUMN_NAME_DATE,
                ActivityEntry.COLUMN_NAME_ICON,
                ActivityEntry.COLUMN_NAME_LABEL,
        };
        String where = ActivityEntry.COLUMN_NAME_PROFILE_ID + " = ?";
        String[] whereValues = {String.valueOf(profileId)};
        Cursor cursor = db.query(
                ActivityEntry.TABLE_NAME,
                projection,
                where,
                whereValues,
                null,
                null,
                null
        );
        List<Activity> activityList = new ArrayList<>();
        while (cursor.moveToNext()){
            long id = cursor.getInt(cursor.getColumnIndexOrThrow(ActivityEntry._ID));
            String label = cursor.getString(cursor.getColumnIndexOrThrow(ActivityEntry.COLUMN_NAME_LABEL));
            int icon = cursor.getInt(cursor.getColumnIndexOrThrow(ActivityEntry.COLUMN_NAME_ICON));
            Date date = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(ActivityEntry.COLUMN_NAME_DATE)));
            activityList.add(new Activity(id,profileId,label,icon,date));
        }
        return activityList;
    }
}
