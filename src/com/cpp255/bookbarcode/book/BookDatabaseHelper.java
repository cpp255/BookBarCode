package com.cpp255.bookbarcode.book;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookDatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = BookDatabaseHelper.class.getName();

	public static final String DATABASE_NAME = "library.db";
	private static final int DATABASE_VERSION = 1;
	public static final String BOOKS_TABLE_NAME = "bookinfo";

	public BookDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BOOKS_TABLE_NAME + " ("
                + BookInfoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookInfoColumns.TITLE + " TEXT,"
                + BookInfoColumns.ISBN + " TEXT,"
                + BookInfoColumns.AUTHOR + " TEXT,"
                + BookInfoColumns.THUMBNAILFILEPATH + " TEXT,"
                + BookInfoColumns.PUBLISHER + " TEXT,"
                + BookInfoColumns.PUBLISHDATE + " TEXT,"
                + BookInfoColumns.PRICE + " TEXT,"
                + BookInfoColumns.SUMMARY + " TEXT,"
                + BookInfoColumns.RATING + " DOUBLE,"
                + BookInfoColumns.TRANSLATOR + " TEXT,"
                + BookInfoColumns.PAGES + " INTEGER,"
                + BookInfoColumns.ILLUSTRATE + " TEXT"
                + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS bookinfo");
		onCreate(db);
	}

}
