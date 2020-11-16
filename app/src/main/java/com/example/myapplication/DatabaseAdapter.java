package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open(){
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_FIRST_NAME,
                DatabaseHelper.COLUMN_LAST_NAME,
                DatabaseHelper.COLUMN_AGE,
                DatabaseHelper.COLUMN_PHONE_NUMBER,
                DatabaseHelper.COLUMN_USER_IMAGE,
                DatabaseHelper.COLUMN_CREATED_DATE,
                DatabaseHelper.COLUMN_UPDATED_DATE
        };

         return database.query(DatabaseHelper.TABLE, columns, null, null, null, null, DatabaseHelper.COLUMN_CREATED_DATE);
    }

    public ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String firstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_NAME));
                String age = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE_NUMBER));
                String userImage = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));
                String createdDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_DATE));
                String updatedDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_UPDATED_DATE));
                users.add(new User(id, firstName, lastName, age, phoneNumber, userImage, createdDate, updatedDate));
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        return users;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public User getUser(long id){
        User user = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            String firstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_NAME));
            String age = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE_NUMBER));
            String userImage = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_IMAGE));
            String createdDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_DATE));
            String updatedDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_UPDATED_DATE));
            user = new User(id, firstName, lastName, age, phoneNumber, userImage, createdDate, updatedDate);
        }
        cursor.close();

        return user;
    }

    public long insert(User user){

        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COLUMN_FIRST_NAME, user.getFirstName());
        cv.put(DatabaseHelper.COLUMN_LAST_NAME, user.getLastName());
        cv.put(DatabaseHelper.COLUMN_AGE, user.getAge());
        cv.put(DatabaseHelper.COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        cv.put(DatabaseHelper.COLUMN_USER_IMAGE, user.getUserImage());
        cv.put(DatabaseHelper.COLUMN_CREATED_DATE, user.getCreatedDate());
        cv.put(DatabaseHelper.COLUMN_UPDATED_DATE, user.getUpdatedDate());

        return database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long update(User user){

        String whereClause = DatabaseHelper.COLUMN_ID + "=" + String.valueOf(user.getId());
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.COLUMN_FIRST_NAME, user.getFirstName());
        cv.put(DatabaseHelper.COLUMN_LAST_NAME, user.getLastName());
        cv.put(DatabaseHelper.COLUMN_AGE, user.getAge());
        cv.put(DatabaseHelper.COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        cv.put(DatabaseHelper.COLUMN_USER_IMAGE, user.getUserImage());
        cv.put(DatabaseHelper.COLUMN_CREATED_DATE, user.getCreatedDate());
        cv.put(DatabaseHelper.COLUMN_UPDATED_DATE, user.getUpdatedDate());

        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }

    public long delete(long userId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(userId)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }
}
