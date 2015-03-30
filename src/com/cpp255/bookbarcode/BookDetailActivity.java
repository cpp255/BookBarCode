package com.cpp255.bookbarcode;

import com.cpp255.bookbarcode.book.BookInfo;
import com.cpp255.bookbarcode.book.BookInfoColumns;
import com.cpp255.bookbarcode.book.Utilities;
import com.google.zxing.client.android.Intents.ViewBookInfo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 这个activity主要用来显示两个界面，只是因为数据来源不一样，所以有部分功能会不一样：
 * 1.显示扫描后网络返回的信息；
 * 2.显示从本地数据库中获取的信息；
 * @author jdk
 *
 */
public class BookDetailActivity extends Activity {
	private static final String TAG = BookDetailActivity.class.getSimpleName();
	
	private BookInfo bookInfo;
	private boolean isScanMode;
	private Context mContext;
	private Uri mUri;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.book_detail_activity);
		mContext = this;
		Intent intent = getIntent();
		if(intent != null) {
			final String action = intent.getAction();
			if(action.equals(ViewBookInfo.SCAN_BOOKINFO_INTENT)) {
				isScanMode = true;
				bookInfo = (BookInfo) intent.getParcelableExtra(BookInfo.class
						.getName());
			} else if(action.equals(ViewBookInfo.VIEW_BOOKINFO_INTENT)) {
				isScanMode = false;
				mUri = intent.getData();
				bookInfo = getBookInfo();
			}
		}
		
		onInitUIData();
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
					| ActionBar.DISPLAY_SHOW_TITLE,
					ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE
					| ActionBar.DISPLAY_SHOW_HOME);
			actionBar.setTitle(R.string.book_info_title);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	private void onInitUIData() {
		if(bookInfo == null) {
			return;
		}
		
		TextView textView = (TextView) findViewById(R.id.book_title);
		textView.setText(bookInfo.getTitle());
		ImageView imageView = (ImageView) findViewById(R.id.book_cover);
		imageView.setImageBitmap(bookInfo.getThumbnail());
		textView = (TextView) findViewById(R.id.book_author);
		String textTitle = getResources().getString(R.string.text_author) + bookInfo.getAuthor();
		textView.setText(textTitle);
		textView = (TextView) findViewById(R.id.book_pubdate);
		textTitle = getResources().getString(R.string.text_publishdate) + bookInfo.getPublishDate();
		textView.setText(textTitle);
		textView = (TextView) findViewById(R.id.book_price);
		textTitle = getResources().getString(R.string.text_price) + bookInfo.getPrice();
		textView.setText(textTitle);
		textView = (TextView) findViewById(R.id.book_summary);
		textView.setText(bookInfo.getSummary());
		textView = (TextView) findViewById(R.id.book_publisher);
		textTitle = getResources().getString(R.string.text_publisher) + bookInfo.getPublisher();
		textView.setText(textTitle);
		textView = (TextView) findViewById(R.id.book_pages);
		textTitle = getResources().getString(R.string.text_pages) + bookInfo.getPages();
		textView.setText(textTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_book_info, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		final MenuItem saveBookMenu = menu.findItem(R.id.action_save_book);
		final MenuItem deleteBookMent = menu.findItem(R.id.action_delete_book);
		
		if(isScanMode) {
			deleteBookMent.setVisible(false);
			saveBookMenu.setVisible(true);
		} else {
			deleteBookMent.setVisible(true);
			saveBookMenu.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_save_book:
			saveBookInfo();
			Toast toast = Toast.makeText(mContext, R.string.msg_save_book_success, 200);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
			Intent intent = new Intent(BookDetailActivity.this, AllBookListActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.action_delete_book:
			deleteBookInfo();
			break;
		default:
			super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	private void  deleteBookInfo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(R.string.msg_delete_bookinfo_confirm);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						 ContentResolver contentResolver = getContentResolver();
						 contentResolver.delete(mUri, null, null);
						 finish();
					}
				});
		builder.setNegativeButton(R.string.cancel, 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		builder.show();	
	}
	
	private void saveBookInfo() {
		if (bookInfo != null) {
			String filePath = Utilities.saveBitmapToFile(bookInfo.getThumbnail(), Long.toString(bookInfo.getISBN()));
			
			ContentResolver contentResolver = getContentResolver();
			Uri insertUri = BookInfoColumns.CONTENT_URI;
			ContentValues values = new ContentValues();
			values.put(BookInfoColumns.TITLE, bookInfo.getTitle());
			values.put(BookInfoColumns.ISBN, bookInfo.getISBN());
			values.put(BookInfoColumns.AUTHOR, bookInfo.getAuthor());
			values.put(BookInfoColumns.THUMBNAILFILEPATH, filePath);
			values.put(BookInfoColumns.PUBLISHER, bookInfo.getPublisher());
			values.put(BookInfoColumns.PUBLISHDATE, bookInfo.getPublishDate());
			values.put(BookInfoColumns.PRICE, bookInfo.getPrice());
			values.put(BookInfoColumns.SUMMARY, bookInfo.getSummary());
			values.put(BookInfoColumns.RATING, bookInfo.getRating());
			values.put(BookInfoColumns.TRANSLATOR, bookInfo.getRating());
			values.put(BookInfoColumns.TRANSLATOR, bookInfo.getTranslator());
			values.put(BookInfoColumns.PAGES, bookInfo.getPages());
			values.put(BookInfoColumns.ILLUSTRATE, bookInfo.getIllustrate());
			Uri uri = contentResolver.insert(insertUri, values);
		}
	}

	private BookInfo getBookInfo() {
		//TODO 查询方式需要修改,CursorLoader
		BookInfo bookInfo = new BookInfo();
		Cursor cursor = managedQuery(mUri, null, null, null, null);
		while(cursor.moveToNext()) {
			String str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.TITLE));
			bookInfo.setTitle(str);
			str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.AUTHOR));
			bookInfo.setAuthor(str);
			str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.ISBN));
			bookInfo.setISBN(Long.parseLong(str));
			str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.SUMMARY));
			bookInfo.setSummary(str);
			str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.THUMBNAILFILEPATH));
			Bitmap bitmap = Utilities.getBitmapFromFile(str);
			bookInfo.setThumbnail(bitmap);
			str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.PUBLISHDATE));
			bookInfo.setPublishDate(str);
			str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.PRICE));
			bookInfo.setPrice(str);
			str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.PUBLISHER));
			bookInfo.setPublisher(str);
			str = cursor.getString(cursor.getColumnIndex(BookInfoColumns.PAGES));
			bookInfo.setPages(Integer.parseInt(str));
		}
		
		return bookInfo;
	}
}
