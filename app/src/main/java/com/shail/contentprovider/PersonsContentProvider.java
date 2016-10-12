package com.shail.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by iTexico Developer on 10/11/2016.
 */

public class PersonsContentProvider extends ContentProvider {

    private static final String TAG = PersonsContentProvider.class.getSimpleName();

    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    public static final String AUTHORITY = PACKAGE_NAME + ".provider";

    private static final int PERSON_DIR = 0;
    private static final int PERSON_ID = 1;

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri PERSON_CONTENT_URI = Uri.withAppendedPath(PersonsContentProvider.AUTHORITY_URI, PersonContract.CONTENT_PATH);
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, PersonContract.CONTENT_PATH, PERSON_DIR);
        URI_MATCHER.addURI(AUTHORITY, PersonContract.CONTENT_PATH + "/#", PERSON_ID);
    }

    private PersonsSQLiteOpenHelper mPersonsSQLiteOpenHelper;

    public static final class PersonContract implements BaseColumns {
        public static final String CONTENT_PATH = PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE;
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE;
    }

    @Override
    public boolean onCreate() {
        mPersonsSQLiteOpenHelper = new PersonsSQLiteOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(final Uri uri, String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final SQLiteDatabase dbConnection = mPersonsSQLiteOpenHelper.getReadableDatabase();
        queryBuilder.setTables(PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE);

        switch (URI_MATCHER.match(uri)) {
            case PERSON_DIR:
                break;

            case PERSON_ID:
                queryBuilder.appendWhere(PersonsSQLiteOpenHelper.PERSON_ID + "=" + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Failed to retrieve Data from URI:" + uri);
        }

        Cursor cursor = queryBuilder.query(dbConnection, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase dbConnection = mPersonsSQLiteOpenHelper.getWritableDatabase();
        try {
            dbConnection.beginTransaction();
            switch (URI_MATCHER.match(uri)) {
                case PERSON_DIR:
                case PERSON_ID:
                    final long personId = dbConnection.insert(PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE, null, contentValues);
                    final Uri newPersonUri = ContentUris.withAppendedId(PERSON_CONTENT_URI, personId);

                    getContext().getContentResolver().notifyChange(newPersonUri, null);
                    dbConnection.setTransactionSuccessful();
                    Log.i(TAG, "Added a record to " + uri);
                    return newPersonUri;
                default:
                    throw new IllegalArgumentException("Failed to add a record to " + uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Failed to add a record to " + uri);
        } finally {
            dbConnection.endTransaction();
        }
        return null;
    }


    @Override
    public final int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase dbConnection = mPersonsSQLiteOpenHelper.getWritableDatabase();
        int updateCount = 0;
        try {
            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {
                case PERSON_DIR:
                    updateCount = dbConnection.update(PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE, values, selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    Log.i(TAG, "Updated a record to " + uri);
                    break;

                case PERSON_ID:
                    String whereClause = PersonsSQLiteOpenHelper.PERSON_ID + " = " + uri.getLastPathSegment();
                    if (!selection.isEmpty()) {
                        whereClause += " AND " + selection;
                    }
                    updateCount = dbConnection.update(PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE, values, whereClause
                            , selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    Log.i(TAG, "Updated a record to " + uri);
                    break;

                default:
                    throw new IllegalArgumentException("Failed to update a record to " + uri);
            }

        } catch (SQLiteException exe) {
            exe.printStackTrace();
            Log.i(TAG, "Failed to update a record to " + uri);
        } finally {
            dbConnection.endTransaction();
        }

        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    @Override
    public final int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase dbConnection = mPersonsSQLiteOpenHelper.getWritableDatabase();
        int deleteCount = 0;
        try {
            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {
                case PERSON_DIR:
                    deleteCount = dbConnection.delete(PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE, selection, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    Log.i(TAG, "Deleted a record to " + uri);
                    break;

                case PERSON_ID:
                    String whereClause = PersonsSQLiteOpenHelper.PERSON_ID + " = " + uri.getLastPathSegment();
                    if (!selection.isEmpty()) {
                        whereClause += " AND " + selection;
                    }
                    deleteCount = dbConnection.delete(PersonsSQLiteOpenHelper.DATABASE_PERSON_TABLE, whereClause, selectionArgs);
                    dbConnection.setTransactionSuccessful();
                    Log.i(TAG, "Deleted a record to " + uri);
                    break;

                default:
                    throw new IllegalArgumentException("Failed to delete a record to " + uri);
            }
        } catch (SQLiteException sqlite) {
            sqlite.printStackTrace();
            Log.i(TAG, "Failed to delete a record to " + uri);
        } finally {
            dbConnection.endTransaction();
        }
        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case PERSON_DIR:
                return PersonContract.CONTENT_TYPE;
            case PERSON_ID:
                return PersonContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


}
