package com.example.varda.naviraklio.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.varda.naviraklio.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UserManager.db";

    // User table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_CURRENT_USER = "current_user";


    // User Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_FIRST_NAME = "user_first_name";
    private static final String COLUMN_USER_LAST_NAME = "user_last_name";
    private static final String COLUMN_USER_USERNAME = "user_username";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_USER_ADDRESS = "user_address";
    private static final String COLUMN_USER_TEL = "user_tel";
    private static final String COLUMN_USER_CREDIT_CARD = "user_credit_card";

    // create table sql query for user
    private String CREATE_USER_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
             + COLUMN_USER_FIRST_NAME + " TEXT,"
            + COLUMN_USER_LAST_NAME + " TEXT," + COLUMN_USER_USERNAME + " TEXT PRIMARY KEY," + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_ADDRESS
            + " TEXT," + COLUMN_USER_TEL + " TEXT," + COLUMN_USER_CREDIT_CARD + " TEXT " + ")";

    // drop table sql query for user
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // create table sql query for user
    private String CREATE_CURRENT_USER_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_CURRENT_USER + "("
            + COLUMN_USER_FIRST_NAME + " TEXT,"
            + COLUMN_USER_LAST_NAME + " TEXT," + COLUMN_USER_USERNAME + " TEXT PRIMARY KEY," + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_ADDRESS
            + " TEXT," + COLUMN_USER_TEL + " TEXT," + COLUMN_USER_CREDIT_CARD + " TEXT " + ")";


    // drop table sql query for user
    private String DROP_CURRENT_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_CURRENT_USER;

    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE_IF_NOT_EXISTS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);

        // Create tables again
        onCreate(db);

    }

    /**
     * This method is to create user record
     *
     * @param user
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_USER_LAST_NAME, user.getLastName());
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_USER_TEL, user.getTel());
        values.put(COLUMN_USER_CREDIT_CARD, user.getCreditCard());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void addCurrentUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_USER_LAST_NAME, user.getLastName());
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_USER_TEL, user.getTel());
        values.put(COLUMN_USER_CREDIT_CARD, user.getCreditCard());

        // Inserting Row
        db.insert(TABLE_CURRENT_USER, null, values);
        db.close();
    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    public List<User> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_FIRST_NAME,
                COLUMN_USER_LAST_NAME,
                COLUMN_USER_USERNAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_ADDRESS,
                COLUMN_USER_TEL,
                COLUMN_USER_CREDIT_CARD
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_LAST_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_username,user_password,user_address,user_tel,user_credit_card FROM user ORDER BY user_name;
         */
        db.execSQL(CREATE_USER_TABLE_IF_NOT_EXISTS);
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                user.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ADDRESS)));
                user.setTel(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TEL)));
                user.setCreditCard(cursor.getString(cursor.getColumnIndex(COLUMN_USER_CREDIT_CARD)));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }

    public User getCurrentUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_FIRST_NAME,
                COLUMN_USER_LAST_NAME,
                COLUMN_USER_USERNAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_ADDRESS,
                COLUMN_USER_TEL,
                COLUMN_USER_CREDIT_CARD
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_LAST_NAME + " ASC";


        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_username,user_password,user_address,user_tel,user_credit_card FROM user ORDER BY user_name;
         */
        db.execSQL(CREATE_USER_TABLE_IF_NOT_EXISTS);
        Cursor cursor = db.query(TABLE_CURRENT_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        User user = new User();
        if (cursor.moveToFirst()) {
            do {
                user.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                user.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ADDRESS)));
                user.setTel(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TEL)));
                user.setCreditCard(cursor.getString(cursor.getColumnIndex(COLUMN_USER_CREDIT_CARD)));
                // Adding user record to list
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return user;
    }

    /**
     * This method to update user record
     *
     * @param user
     */
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_USER_LAST_NAME, user.getLastName());
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_USER_TEL, user.getTel());
        values.put(COLUMN_USER_CREDIT_CARD, user.getCreditCard());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_USERNAME + " = ?",
                new String[]{String.valueOf(user.getUsername())});
        db.close();
    }


    /**
     * This method is to delete user record
     *
     * @param user
     */
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_USERNAME + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * This method to check user exist or not
     *
     * @param username
     * @return true/false
     */
    public boolean checkUser(String username) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_USERNAME
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_USERNAME + " = ?";

        // selection argument
        String[] selectionArgs = {username};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_username = 'username-example';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;

    }

    /**
     * This method to check user exist or not
     *
     * @param username
     * @param password
     * @return true/false
     */
    public boolean checkUser(String username, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_FIRST_NAME,
                COLUMN_USER_LAST_NAME,
                COLUMN_USER_USERNAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_ADDRESS,
                COLUMN_USER_TEL,
                COLUMN_USER_CREDIT_CARD
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_USERNAME + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {username, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_username = 'username-example' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();
        if (cursorCount > 0) {
            if (cursor.moveToFirst()) {
                do {
                    this.clearCurrentUserTable(this.getWritableDatabase());
                    String firstNameResult =  cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME));
                    String lastNameResult = cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME));

                    String usernameResult = cursor.getString(cursor.getColumnIndex(COLUMN_USER_USERNAME));

                    String passwordResult = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));

                    String addressResult = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ADDRESS));

                    String telResult = cursor.getString(cursor.getColumnIndex(COLUMN_USER_TEL));

                    String creditCardResult = cursor.getString(cursor.getColumnIndex(COLUMN_USER_CREDIT_CARD));

                    this.addCurrentUser(new User(firstNameResult, lastNameResult, usernameResult, passwordResult, addressResult, telResult, creditCardResult));

                } while (cursor.moveToNext());
            }

        }


        cursor.close();
        db.close();
        return cursorCount > 0;

    }

    public void clearUserTable(SQLiteDatabase db) {
        //Drop Appointment Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(CREATE_USER_TABLE_IF_NOT_EXISTS);
        db.close();
    }

    public void clearCurrentUserTable(SQLiteDatabase db) {
        //Drop Appointment Table if exist
        db.execSQL(DROP_CURRENT_USER_TABLE);
        db.execSQL(CREATE_CURRENT_USER_TABLE_IF_NOT_EXISTS);
        db.close();
    }

}
