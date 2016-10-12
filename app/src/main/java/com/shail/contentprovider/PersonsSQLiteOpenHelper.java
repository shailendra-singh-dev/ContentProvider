package com.shail.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iTexico Developer on 10/11/2016.
 */

public final class PersonsSQLiteOpenHelper extends SQLiteOpenHelper {

    final public static String DATABASE_NAME = "persons.db";
    final public static int DATABASE_VERSION = 1;

    final public static String DATABASE_PERSON_TABLE = "person";

    final public static String PERSON_ID = "_id";
    final public static String PERSON_NAME = "name";
    final public static String PERSON_SSN = "ssn";
    final public static String DATABASE_CREATE = "create table "
            + DATABASE_PERSON_TABLE + " ("
            + PERSON_ID + " integer primary key autoincrement,"
            + PERSON_SSN + " int ,"
            + PERSON_NAME + " text not null);";

    public PersonsSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists " + DATABASE_PERSON_TABLE);
        onCreate(sqLiteDatabase);
    }
}
