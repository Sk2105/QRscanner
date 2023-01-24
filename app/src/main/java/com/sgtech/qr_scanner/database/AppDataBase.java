package com.sgtech.qr_scanner.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

public class AppDataBase extends SQLiteOpenHelper {
    public static final String QR_CODE_DATABASE = "ScannerDatabase";
    public static final String DATABASE_TABLE = "DATABASE_TABLE";
    public static final String keyID = "_ID";
    public static final String DATA_TYPE = "DATA_TYPE";
    public static final String DATA_NAME = "DATA_NAME";
    public static final String DATA_DATE = "DATA_DATE";

    public AppDataBase(Context context) {
        super(context, QR_CODE_DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table =
                "create table " + DATABASE_TABLE + " ( " + keyID + " INTEGER Primary key " +
                        "Autoincrement," + DATA_TYPE + " TEXT," + DATA_NAME + " TEXT," + DATA_DATE +
                        " TEXT)";
        db.execSQL(table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DATABASE_TABLE);
        onCreate(db);
    }

    public void insertDATA(String type, String data) {
        Date date = new Date();
        SQLiteDatabase dataBase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATA_TYPE, type);
        contentValues.put(DATA_NAME, data);
        contentValues.put(DATA_DATE, date.getDate());
        dataBase.insert(DATABASE_TABLE, null, contentValues);

    }

    public ArrayList<DataModel> fetchData() {
        ArrayList<DataModel> dataModels = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(" SELECT * FROM " + DATABASE_TABLE, null);
        while (cursor.moveToNext()) {
            DataModel dataModel = new DataModel(cursor.getString(1), cursor.getString(2));
            dataModels.add(dataModel);
        }
        return dataModels;
    }
}
