package com.cpp255.bookbarcode.list;

import java.util.List;

import com.cpp255.bookbarcode.R;
import com.cpp255.bookbarcode.book.BookInfo;
import com.cpp255.bookbarcode.book.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BooksViewAdapter extends BaseAdapter{
	private List<BookInfo> mBookInfoList = null;
	private LayoutInflater mLayoutInflater;
	
	public BooksViewAdapter(Context context, List<BookInfo> bookInfoList) {
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mBookInfoList = bookInfoList;
	}
	
	@Override
	public int getCount() {
		return mBookInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBookInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mBookInfoList.get(position).get_id();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			view = mLayoutInflater.inflate(R.layout.bookinfo_list_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) convertView.getTag();
		}
		
		BookInfo bookInfo = (BookInfo) getItem(position);
		Bitmap bitmap = Utilities.getBitmapFromFile(bookInfo.getThumbnailFilePath());
		holder.bookCover.setImageBitmap(bitmap);
		holder.bookTitle.setText(bookInfo.getTitle());
		holder.bookAuthor.setText(bookInfo.getAuthor());
		
		return view;
	}

	class ViewHolder {
		ImageView bookCover;
		TextView bookTitle;
		TextView bookAuthor;
		public ViewHolder(View parent) {
			this.bookCover = (ImageView) parent.findViewById(R.id.book_item_cover);
			this.bookTitle = (TextView) parent.findViewById(R.id.book_item_title);
			this.bookAuthor = (TextView) parent.findViewById(R.id.book_item_author);
		}
	}
}
