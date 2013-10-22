package com.cpp255.bookbarcode.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListAdapter;
/**
 * 图书列表显示信息
 * @author jdk
 *
 */
public class BookInfoListView extends ListView implements OnItemSelectedListener{
	private BooksViewAdapter mAdapter;
	private OnItemSelectedListener mOnItemSelectedListener;
	
	public BookInfoListView(Context context) {
		super(context);
	}

	public BookInfoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BookInfoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mAdapter = (BooksViewAdapter) adapter;
		super.setAdapter(adapter);
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelectedListener = listener;
		super.setOnItemSelectedListener(listener);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(parent, view, position, id);
        }
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onNothingSelected(parent);
        }
	}
}
