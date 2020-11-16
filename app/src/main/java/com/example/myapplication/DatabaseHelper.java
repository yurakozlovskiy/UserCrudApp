package com.example.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "userstore.db";
    private static final int DB_VERSION = 1;
    public static String TABLE = "users";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_IMAGE = "user_image";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_CREATED_DATE = "create_date";
    public static final String COLUMN_UPDATED_DATE = "updated_date";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_FIRST_NAME + " TEXT, " +
                        COLUMN_LAST_NAME + "  TEXT, " +
                        COLUMN_AGE + " TEXT, " +
                        COLUMN_PHONE_NUMBER + " TEXT, " +
                        COLUMN_USER_IMAGE + " TEXT, " +
                        COLUMN_CREATED_DATE + " TEXT, " +
                        COLUMN_UPDATED_DATE + " TEXT ); "
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(sqLiteDatabase);
    }
}
