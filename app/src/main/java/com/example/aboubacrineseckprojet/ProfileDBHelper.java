package com.example.aboubacrineseckprojet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ProfileDBHelper extends SQLiteOpenHelper {
    public static class ProfileEntry implements BaseColumns{
        public static final String TABLE_NAME = "projetprofiles";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_MALE = "gender";
        public static final String COLUMN_NAME_MAJOR = "major";
    }
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "profile_db";
    public static final String CREATE_ENTRIES =
                    "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                    ProfileEntry._ID + " INTEGER PRIMARY KEY," +
                    ProfileEntry.COLUMN_NAME_EMAIL + " TEXT,"+
                    ProfileEntry.COLUMN_NAME_PASSWORD + " TEXT,"+
                    ProfileEntry.COLUMN_NAME_NAME + " TEXT,"+
                    ProfileEntry.COLUMN_NAME_IMAGE + " BLOB,"+
                    ProfileEntry.COLUMN_NAME_PHONE + " TEXT,"+
                    ProfileEntry.COLUMN_NAME_MALE + " Int,"+
                    ProfileEntry.COLUMN_NAME_MAJOR + " TEXT"+
                    ")";
    public static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME;

    public ProfileDBHelper(Context context){
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
    public boolean profileExists(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                ProfileEntry._ID
        };
        String where = ProfileEntry.COLUMN_NAME_EMAIL + " = ?";
        String[] whereValues = {email};
        Cursor cursor = db.query(
                ProfileEntry.TABLE_NAME,
                projection,
                where,
                whereValues,
                null,
                null,
                null
        );
        return !cursor.moveToNext();
    }
    public Profile getProfileFromDB(long id){
        String where = ProfileEntry._ID + " = ?";
        String[] whereValues = {String.valueOf(id)};
        return cursorToProfile(where,whereValues);
    }
    public Profile getProfileFromDB(String email,String password){
        String where = ProfileEntry.COLUMN_NAME_EMAIL + " = ? "+
                "and " + ProfileEntry.COLUMN_NAME_PASSWORD + " = ?";
        String[] whereValues = {email,password};
        return cursorToProfile(where,whereValues);
    }

    private Profile cursorToProfile(String where,String [] whereValues) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                ProfileEntry._ID,
                ProfileEntry.COLUMN_NAME_NAME,
                ProfileEntry.COLUMN_NAME_EMAIL,
                ProfileEntry.COLUMN_NAME_PASSWORD,
                ProfileEntry.COLUMN_NAME_IMAGE,
                ProfileEntry.COLUMN_NAME_PHONE,
                ProfileEntry.COLUMN_NAME_MALE,
                ProfileEntry.COLUMN_NAME_MAJOR,
        };
        Cursor cursor = db.query(
                ProfileEntry.TABLE_NAME,
                projection,
                where,
                whereValues,
                null,
                null,
                null
        );
        if (!cursor.moveToNext()){
            return null;
        }
        long id = cursor.getInt(cursor.getColumnIndexOrThrow(ProfileEntry._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_EMAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_PASSWORD));
        byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_IMAGE));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_PHONE));
        boolean male = cursor.getInt(cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_MALE)) == 1;
        String major = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.COLUMN_NAME_MAJOR));
        cursor.close();
        return new Profile(id,name,email,password,image,phone,male,major);
    }

    public boolean saveProfile(Profile profile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProfileEntry.COLUMN_NAME_NAME,profile.name);
        values.put(ProfileEntry.COLUMN_NAME_EMAIL,profile.email);
        values.put(ProfileEntry.COLUMN_NAME_PASSWORD,profile.password);
        values.put(ProfileEntry.COLUMN_NAME_IMAGE,profile.image);
        values.put(ProfileEntry.COLUMN_NAME_PHONE,profile.phone);
        values.put(ProfileEntry.COLUMN_NAME_MALE,profile.male);
        values.put(ProfileEntry.COLUMN_NAME_MAJOR,profile.major);
        long id = db.insert(ProfileEntry.TABLE_NAME,null,values);
        profile.id = id;
        return id > -1;
    }
    public boolean updateProfile(Profile profile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProfileEntry.COLUMN_NAME_NAME,profile.name);
        values.put(ProfileEntry.COLUMN_NAME_EMAIL,profile.email);
        values.put(ProfileEntry.COLUMN_NAME_PASSWORD,profile.password);
        values.put(ProfileEntry.COLUMN_NAME_IMAGE,profile.image);
        values.put(ProfileEntry.COLUMN_NAME_PHONE,profile.phone);
        values.put(ProfileEntry.COLUMN_NAME_MALE,profile.male);
        values.put(ProfileEntry.COLUMN_NAME_MAJOR,profile.major);
        long id = db.update(ProfileEntry.TABLE_NAME,
                values,
                ProfileEntry._ID +" = ?",
                new String[]{String.valueOf(profile.id)
        });
        profile.id = id;
        return id > -1;
    }
}
