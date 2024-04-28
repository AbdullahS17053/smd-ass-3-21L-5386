package com.example.smd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PasswordManager.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_PASSWORDS = "passwords";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_URL = "url";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_DELETED = "deleted";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_PASSWORD + " TEXT" + ")";

    private static final String CREATE_TABLE_PASSWORDS = "CREATE TABLE " + TABLE_PASSWORDS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_WEBSITE + " TEXT," +
            COLUMN_URL + " TEXT," +
            COLUMN_USERNAME + " TEXT," +
            COLUMN_PASSWORD + " TEXT," +
            COLUMN_USER_ID + " INTEGER," +
            COLUMN_DELETED + " INTEGER DEFAULT 0," +
            "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public Cursor getAllEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_WEBSITE, COLUMN_URL, COLUMN_USERNAME, COLUMN_PASSWORD};
        String selection = COLUMN_DELETED + " = ?";
        String[] selectionArgs = {"0"}; // Assuming '0' means not deleted
        return db.query(TABLE_PASSWORDS, columns, selection, selectionArgs, null, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PASSWORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
        onCreate(db);
    }

    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }
    public void restoreAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DELETED, 0);  // Assuming 0 means the entry is not deleted

        // Update all entries where deleted is 1 to be 0
        db.update(TABLE_PASSWORDS, values, COLUMN_DELETED + "=?", new String[]{"1"});
    }

    public boolean getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public long addPasswordEntry(String website, String url, String username, String password, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEBSITE, website);
        values.put(COLUMN_URL, url);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_USER_ID, userId);
        return db.insert(TABLE_PASSWORDS, null, values);
    }

    public int updatePasswordEntry(int id, String website, String url, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEBSITE, website);
        values.put(COLUMN_URL, url);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.update(TABLE_PASSWORDS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deletePasswordEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DELETED, 1);  // Set the deleted flag
        db.update(TABLE_PASSWORDS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public Cursor getDeletedEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PASSWORDS, new String[]{COLUMN_ID, COLUMN_WEBSITE, COLUMN_URL, COLUMN_USERNAME, COLUMN_PASSWORD},
                COLUMN_DELETED + "=?", new String[]{"1"}, null, null, null);
    }

    public void restoreEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DELETED, 0);  // Reset the deleted flag
        db.update(TABLE_PASSWORDS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
}
