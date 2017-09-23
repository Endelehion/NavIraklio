package com.example.varda.naviraklio.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.varda.naviraklio.Appointment;
import com.example.varda.naviraklio.model.AppointmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johnis on 24/9/2017.
 */
public class DatabaseAppointmentHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "AppointmentModelManager.db";

    // Appointment table name
    private static final String TABLE_APPOINTMENT = "appointment";

    // Appointment Table Columns names
    private static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    private static final String COLUMN_APPOINTMENT_DATE = "appointment_date";
    private static final String COLUMN_APPOINTMENT_DURATION = "appointment_duration";
    private static final String COLUMN_APPOINTMENT_LATITUDE = "appointment_latitude";
    private static final String COLUMN_APPOINTMENT_LONGITUDE = "appointment_longitude";
    private static final String COLUMN_APPOINTMENT_ADDRESS = "appointment_address";
    private static final String COLUMN_APPOINTMENT_TYPE = "appointment_type";
    private static final String COLUMN_APPOINTMENT_OPEN_HOUR = "appointment_openHour";
    private static final String COLUMN_APPOINTMENT_CLOSE_HOUR = "appointment_closeHour";

    // create table sql query for appointment
    private String CREATE_APPOINTMENT_TABLE = "CREATE TABLE " + TABLE_APPOINTMENT + "("
            + COLUMN_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_APPOINTMENT_DATE + " TEXT,"
            + COLUMN_APPOINTMENT_DURATION + " TEXT," + COLUMN_APPOINTMENT_LATITUDE + " TEXT," + COLUMN_APPOINTMENT_LONGITUDE
            + " TEXT," + COLUMN_APPOINTMENT_ADDRESS + " TEXT," + COLUMN_APPOINTMENT_TYPE + " TEXT," + COLUMN_APPOINTMENT_OPEN_HOUR + " TEXT," + COLUMN_APPOINTMENT_CLOSE_HOUR + " TEXT " + ")";

    // drop table sql query for appointment
    private String DROP_APPOINTMENT_TABLE = "DROP TABLE IF EXISTS " + TABLE_APPOINTMENT;

    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseAppointmentHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APPOINTMENT_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop Appointment Table if exist
        db.execSQL(DROP_APPOINTMENT_TABLE);

        // Create tables again
        onCreate(db);

    }

    /**
     * This method is to create appointment record
     *
     * @param appointment
     */
    public void addAppointmentModel(AppointmentModel appointment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_APPOINTMENT_DATE, appointment.getDate());
        values.put(COLUMN_APPOINTMENT_DURATION, appointment.getDuration());
        values.put(COLUMN_APPOINTMENT_LATITUDE, appointment.getLatitude());
        values.put(COLUMN_APPOINTMENT_LONGITUDE, appointment.getLongitude());
        values.put(COLUMN_APPOINTMENT_ADDRESS, appointment.getAddress());
        values.put(COLUMN_APPOINTMENT_TYPE, appointment.getType());
        values.put(COLUMN_APPOINTMENT_OPEN_HOUR, appointment.getOpenHour());
        values.put(COLUMN_APPOINTMENT_CLOSE_HOUR, appointment.getCloseHour());

        // Inserting Row
        db.insert(TABLE_APPOINTMENT, null, values);
        db.close();
    }

    /**
     * This method is to fetch all appointments and return the list of appointment records
     *
     * @return list
     */
    public List<AppointmentModel> getAllAppointments() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_APPOINTMENT_ID,
                COLUMN_APPOINTMENT_DATE,
                COLUMN_APPOINTMENT_DURATION,
                COLUMN_APPOINTMENT_LATITUDE,
                COLUMN_APPOINTMENT_LONGITUDE,
                COLUMN_APPOINTMENT_ADDRESS,
                COLUMN_APPOINTMENT_TYPE,
                COLUMN_APPOINTMENT_OPEN_HOUR,
                COLUMN_APPOINTMENT_CLOSE_HOUR
        };
        // sorting orders
        String sortOrder =
                COLUMN_APPOINTMENT_ID + " ASC";
        List<AppointmentModel> appointmentModelList = new ArrayList<AppointmentModel>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the appointment table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT appointment_id,appointment_date,appointment_duration,appointment_latitude,appointment_longitude,appointment_address,appointment_type,appointment_openHour,appointment_closeHour FROM appointment ORDER BY appointment_id;
         */
        Cursor cursor = db.query(TABLE_APPOINTMENT, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AppointmentModel appointmentModel = new AppointmentModel();
                appointmentModel.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_ID))));
                appointmentModel.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_DATE)));
                appointmentModel.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_DURATION)));
                appointmentModel.setLatitude(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_LATITUDE)));
                appointmentModel.setLongitude(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_LONGITUDE)));
                appointmentModel.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_ADDRESS)));
                appointmentModel.setType(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_TYPE)));
                appointmentModel.setOpenHour(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_OPEN_HOUR)));
                appointmentModel.setCloseHour(cursor.getString(cursor.getColumnIndex(COLUMN_APPOINTMENT_CLOSE_HOUR)));
                // Adding appointment record to list
                appointmentModelList.add(appointmentModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return appointment list
        return appointmentModelList;
    }

    /**
     * This method to update appointment record
     *
     * @param appointmentModel
     */
    public void updateAppointmentModel(AppointmentModel appointmentModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_APPOINTMENT_DATE, appointmentModel.getDate());
        values.put(COLUMN_APPOINTMENT_DURATION, appointmentModel.getDuration());
        values.put(COLUMN_APPOINTMENT_LATITUDE, appointmentModel.getLatitude());
        values.put(COLUMN_APPOINTMENT_LONGITUDE, appointmentModel.getLongitude());
        values.put(COLUMN_APPOINTMENT_ADDRESS, appointmentModel.getAddress());
        values.put(COLUMN_APPOINTMENT_TYPE, appointmentModel.getType());
        values.put(COLUMN_APPOINTMENT_OPEN_HOUR, appointmentModel.getOpenHour());
        values.put(COLUMN_APPOINTMENT_CLOSE_HOUR, appointmentModel.getCloseHour());


        // updating row
        db.update(TABLE_APPOINTMENT, values, COLUMN_APPOINTMENT_ID + " = ?",
                new String[]{String.valueOf(appointmentModel.getId())});
        db.close();
    }

    /**
     * This method is to delete appointment record
     *
     * @param appointmentModel
     */
    public void deleteAppointmentModel(AppointmentModel appointmentModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete appointment record by id
        db.delete(TABLE_APPOINTMENT, COLUMN_APPOINTMENT_ID + " = ?",
                new String[]{String.valueOf(appointmentModel.getId())});
        db.close();
    }

    public void dropAppointmentTable(SQLiteDatabase db){
        //Drop Appointment Table if exist
        db.execSQL(DROP_APPOINTMENT_TABLE);
    }



}
