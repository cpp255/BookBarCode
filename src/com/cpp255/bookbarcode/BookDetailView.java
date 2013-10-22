package com.cpp255.bookbarcode;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
/**
 * 根据获得的值生成一个图书的信息View
 * @author jdk
 *
 */
public class BookDetailView extends LinearLayout{

	public BookDetailView(Context context) {
		super(context);
	}

	public BookDetailView(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	
}
