package com.jacr.photoapp.model.database.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.jacr.photoapp.model.DataSource;
import com.jacr.photoapp.model.database.dtos.Dto;
import com.jacr.photoapp.model.database.dtos.SQLField;
import com.jacr.photoapp.utilities.LogHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager
 * Created by Jesus Castro on 13/11/2015.
 */
class DatabaseManager extends SQLiteOpenHelper {

    //<editor-fold desc="Constants & Variables"

    private static final Class<?> LOG_TAG = DatabaseManager.class;
    private static final String DATABASE = DataSource.Database.NAME;

    private static DatabaseManager singleton;

    //</editor-fold>

    private DatabaseManager(final Context context) {
        // This required constructor creates (or read if it exits) a database in DATABASE_PATH automatically
        super(context, DATABASE, null, DataSource.Database.SQLITE_VERSION);
    }

    public static DatabaseManager getInstance(Context context) {
        if (singleton == null) {
            importDatabase(context);
            singleton = new DatabaseManager(context);
        }
        return singleton;
    }

    //<editor-fold desc="Overrides">

    @Override
    public void onCreate(SQLiteDatabase db) {
        // blank
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // Sqlite >= 3.7 in order to support foreign keys
        db.execSQL("PRAGMA foreign_keys=ON;"); //Enable them
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // blank
    }

    //</editor-fold>

    //<editor-fold desc="About Database">

    private static String createDatabasePath(Context context) {
        // -- DATABASE_PATH: SQLiteOpenHelper ALWAYS will read or create database in this path
        // DON't use another path --
        String fileDir = context.getFilesDir().getPath();
        String databasePath = fileDir.substring(0, fileDir.lastIndexOf("/")) + "/databases/";
        File databaseFile = new File(databasePath);
        return !databaseFile.exists() ? (databaseFile.mkdirs() ?
                databaseFile.getPath() + "/" : "") : databaseFile.getPath() + "/";
    }

    private static void importDatabase(Context context) {
        String databasePath = createDatabasePath(context);
        if (!checkIfDatabaseExists(databasePath)) {
            try {
                copyDataBaseFromAssets(context, databasePath);
            } catch (Exception ex) {
                LogHelper.getInstance().exception(LOG_TAG, ex, ex.getMessage());
            }
        }
    }

    private static void copyDataBaseFromAssets(Context context, String databasePath) throws IOException {
        InputStream databaseStream = context.getAssets().open(DataSource.Database.ASSETS_PATH + DATABASE);
        OutputStream outputStream = new FileOutputStream(databasePath + DATABASE);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = databaseStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        databaseStream.close();
    }

    private static boolean checkIfDatabaseExists(String databasePath) {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(databasePath + DATABASE, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //LogHelper.getInstance().exception(LOG_TAG, e, e.getMessage());
        }
        if (db != null) {
            db.close();
        }
        return db != null;
    }

    //</editor-fold>

    //<editor-fold desc="About CRUD transactions">

    private ContentValues getColumnValues(Dto dto) {
        ContentValues values = new ContentValues();
        for (Field f : dto.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            SQLField columnName = f.getAnnotation(SQLField.class);
            if (columnName != null) {
                try {
                    Object propertyValue = f.get(dto);
                    values.put(columnName.name(), f.get(dto) != null ? propertyValue.toString() : null);
                } catch (IllegalAccessException e) {
                    LogHelper.getInstance().exception(LOG_TAG, e, e.toString());
                }
            }
        }
        return values;
    }

    private <T extends Dto> List<T> createDtos(Class<T> dtoClass, Cursor queryValues) {
        List<T> dtos = new ArrayList<>();
        try {
            for (int i = 0; i < queryValues.getCount(); i++) {
                queryValues.moveToPosition(i);
                T dto = dtoClass.newInstance();
                for (String columnName : queryValues.getColumnNames()) {
                    for (Field f : dtoClass.getDeclaredFields()) {
                        SQLField column = f.getAnnotation(SQLField.class);
                        if (column != null && column.name().equals(columnName)) {
                            f.setAccessible(true);
                            f.set(dto, queryValues.getString(queryValues.getColumnIndex(columnName)));
                            break;
                        }
                    }
                }
                dtos.add(dto);
            }
        } catch (InstantiationException e) {
            LogHelper.getInstance().exception(LOG_TAG, e, e.toString());
        } catch (IllegalAccessException e) {
            LogHelper.getInstance().exception(LOG_TAG, e, e.toString());
        }
        return dtos;
    }

    public <T extends Dto> List<T> sendQuery(String sql, Class<T> dtoClass) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor queryValues = db.rawQuery(sql, null);
        List<T> results = queryValues.moveToFirst() ? createDtos(dtoClass, queryValues) : new ArrayList<T>();
        if (!queryValues.isClosed()) queryValues.close();
        return results;
    }

    // http://stackoverflow.com/questions/11219361/select-distinct-value-in-android-sqlite
    public <T extends Dto> List<T> sendDistinctQuery(String table, String column, Class<T> dtoClass) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor queryValues = db.query(true, table,
                new String[]{column},
                null, null, column, null, null, null);
        List<T> results = queryValues.moveToFirst() ? createDtos(dtoClass, queryValues) : new ArrayList<T>();
        if (!queryValues.isClosed()) queryValues.close();
        return results;
    }

    public boolean insert(Dto dto) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(dto.getTableName(), null, getColumnValues(dto)) != -1;
    }

    public boolean delete(String tableName, String whereClause) {
        SQLiteDatabase bd = getWritableDatabase();
        return bd.delete(tableName, whereClause, null) > 0;
    }

    public void update(String tableName, ContentValues columnsValues, String whereClause) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = " UPDATE " + tableName + " SET ";
        for (String columnName : columnsValues.keySet()) {
            sql += String.format(" %s = %s, ", columnName, columnsValues.getAsString(columnName));
        }
        sql = sql.substring(0, sql.lastIndexOf(","));
        sql += " WHERE " + whereClause;
        db.execSQL(sql);
    }

    //</editor-fold>

}