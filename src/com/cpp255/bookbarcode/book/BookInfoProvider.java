package com.cpp255.bookbarcode.book;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BookInfoProvider extends ContentProvider {
	private static final String TAG = BookInfoProvider.class.getSimpleName();

	private BookDatabaseHelper DBhelper;

	private static final int Books = 1;
	private static final int Book_ID = 2;
	private static final int BOOKS_FILTER = 3;	//目前只匹配作者和图书名进行匹配
	
	private static final UriMatcher mUriMatcher;

	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(BookInfoColumns.AUTHORITY, "bookinfo", Books);
		mUriMatcher.addURI(BookInfoColumns.AUTHORITY, "bookinfo/#", Book_ID);
		mUriMatcher.addURI(BookInfoColumns.AUTHORITY, "bookinfo/filter/*", BOOKS_FILTER);
	}

	@Override
	public boolean onCreate() {
		DBhelper = new BookDatabaseHelper(getContext());
		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = DBhelper.getWritableDatabase();
		int count = 0;
		switch(mUriMatcher.match(uri)) {
		case Books:
			count = db.delete(BookDatabaseHelper.BOOKS_TABLE_NAME, selection, selectionArgs);
			break;
		case Book_ID:
			String bookID = uri.getPathSegments().get(1);
			count = db.delete(BookDatabaseHelper.BOOKS_TABLE_NAME, BookInfoColumns._ID + "="
					+ bookID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "")
					, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch(mUriMatcher.match(uri)) {
		case Books:
			return BookInfoColumns.CONTENT_TYPE;
		case Book_ID:
			return BookInfoColumns.CONTENT_ITEM_TYPE;
		case BOOKS_FILTER:
			return BookInfoColumns.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (mUriMatcher.match(uri) != Books) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = DBhelper.getWritableDatabase();
		long rowId = db.insert(BookDatabaseHelper.BOOKS_TABLE_NAME,
				BookInfoColumns._ID, values);
		if (rowId > 0) {
			Uri bookUri = ContentUris.withAppendedId(
					BookInfoColumns.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(bookUri, null);
			return bookUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		SQLiteDatabase db = DBhelper.getReadableDatabase();
		StringBuilder where;
		
		switch (mUriMatcher.match(uri)) {
		case Books:
			cursor = db.query(BookDatabaseHelper.BOOKS_TABLE_NAME, projection,
					selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		case Book_ID:
			long bookid = ContentUris.parseId(uri);
			where = new StringBuilder();
			where.append("_id = ").append(bookid);	// 获取指定id的记录
			where.append(!TextUtils.isEmpty(selection) ? " and (" + selection + ")": "");	// 把其它条件附加上
			cursor = db.query(BookDatabaseHelper.BOOKS_TABLE_NAME, projection, where.toString(), selectionArgs, null,
					null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		case BOOKS_FILTER:
			where = new StringBuilder();
			String queryStr = uri.getPathSegments().get(2);
			where.append(BookInfoColumns.TITLE).append(" like ")
				.append("'%").append(queryStr).append("%'");
			where.append(" or ").append(BookInfoColumns.AUTHOR).append(" like ")
				.append("'%").append(queryStr).append("%'");
			cursor = db.query(BookDatabaseHelper.BOOKS_TABLE_NAME, projection, where.toString(), selectionArgs, null,
					null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = DBhelper.getWritableDatabase();
		int count = 0;
		switch(mUriMatcher.match(uri)) {
		case Books:
			count = db.update(BookDatabaseHelper.BOOKS_TABLE_NAME, values, selection, selectionArgs);
			break;
		case Book_ID:
			String bookID = uri.getPathSegments().get(1);
			count = db.update(BookDatabaseHelper.BOOKS_TABLE_NAME, values, BookInfoColumns._ID + "="
					+ bookID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
		break;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
