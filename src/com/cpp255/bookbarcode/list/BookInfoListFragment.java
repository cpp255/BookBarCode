package com.cpp255.bookbarcode.list;

import java.util.ArrayList;
import java.util.List;

import com.cpp255.bookbarcode.R;
import com.cpp255.bookbarcode.Intents.ViewBookInfo;
import com.cpp255.bookbarcode.book.BookInfo;
import com.cpp255.bookbarcode.book.BookInfoColumns;
import com.cpp255.bookbarcode.book.Utilities;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 图书列表显示信息
 * @author jdk
 *
 */
public class BookInfoListFragment extends ListFragment implements LoaderCallbacks<Cursor>{
	private static final String TAG = BookInfoListFragment.class.getSimpleName();
	
	/**
	 * ID of the empty loader to defer other fragments.
	 */
	private static final int EMPTY_LOADER_ID = 0;
	
	private BooksViewAdapter mBooksViewAdapter;
	private List<BookInfo> mBookInfoList;
	
	private static final String[] LIST_PROJECTION = { BookInfoColumns._ID,
		BookInfoColumns.TITLE, BookInfoColumns.AUTHOR,
		BookInfoColumns.THUMBNAILFILEPATH, };
	
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		mBookInfoList = new ArrayList<BookInfo>();
	}
	
	@Override
	public void onStart() {
		// Start the empty loader now to defer other fragments.  We destroy it when both bookinfo
       // are fetched.
		getLoaderManager().initLoader(EMPTY_LOADER_ID, null, this);
		super.onStart();
	}
	@Override
	public void onDestroy() {
		getLoaderManager().destroyLoader(EMPTY_LOADER_ID);
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstancesState) {
		View view  = inflater.inflate(R.layout.all_bookinfo_list_fragment, container, false);
		return view;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				BookInfoColumns.CONTENT_URI, LIST_PROJECTION, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		getAllBooks(cursor);
		mBooksViewAdapter = new BooksViewAdapter(getActivity(), mBookInfoList);
		setListAdapter(mBooksViewAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		
	}
	
	private void getAllBooks(Cursor cursor) {
		while (cursor.moveToNext()) {
			BookInfo bookInfo = new BookInfo();
			String str = cursor.getString(cursor
					.getColumnIndex(BookInfoColumns.TITLE));
			bookInfo.setTitle(str);
			long bookID = cursor.getLong(cursor
					.getColumnIndex(BookInfoColumns._ID));
			bookInfo.set_id(bookID);
			str = cursor.getString(cursor
					.getColumnIndex(BookInfoColumns.AUTHOR));
			bookInfo.setAuthor(str);
			str = cursor.getString(cursor
					.getColumnIndex(BookInfoColumns.THUMBNAILFILEPATH));
			bookInfo.setThumbnailFilePath(str);
			Bitmap bitmap = Utilities.getBitmapFromFile(str);
			bookInfo.setThumbnail(bitmap);
			mBookInfoList.add(bookInfo);
		}
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		Uri bookUri = ContentUris.withAppendedId(BookInfoColumns.CONTENT_URI, id);
		Intent intent = new Intent(ViewBookInfo.VIEW_BOOKINFO_INTENT, bookUri);
		startActivity(intent);
	}
}
