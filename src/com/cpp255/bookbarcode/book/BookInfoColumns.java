package com.cpp255.bookbarcode.book;

import android.net.Uri;
import android.provider.BaseColumns;

public class BookInfoColumns implements BaseColumns{
	public static final String AUTHORITY = "com.cpp255.bookbarcode.provider.library";
	
	private BookInfoColumns() {}
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/bookinfo");
    /**
     * The content:// style URL for filtering this table on book title or author
     */
    public static final Uri CONTENT_FILTER_URI =
            Uri.parse("content://" + AUTHORITY + "/bookinfo/filter");
    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of bookinfos.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cpp255.bookinfo";

    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single bookinfo.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cpp255.bookinfo";

    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "modified ASC";

    /**
     * The title of the book
     * <P>Type: TEXT</P>
     */
    public static final String TITLE = "title";

    /**
     * The ISBN of the book
     * <P>Type: TEXT</P>
     */
    public static final String ISBN = "isbn";
    //TODO 有些字段可以替换成int类型，现在为了开发方便全部用了String,版本1开发完后，需要重新写过
    /**
     * The author of the book
     * <P>Type: TEXT</P>
     */
    public static final String AUTHOR = "author";
    
    /**
     * The thumbnail file path of the book
     * <P>Type: TEXT</P>
     */
    public static final String THUMBNAILFILEPATH = "thumbnail";
    
    /**
     * The publisher of the book
     * <P>Type: TEXT</P>
     */
    public static final String PUBLISHER = "publisher";
    
    /**
     * The publishDate of the book
     * <P>Type: TEXT</P>
     */
    public static final String PUBLISHDATE = "publishdate";
    
    /**
     * The price of the book
     * <P>Type: TEXT</P>
     */
    public static final String PRICE = "price";
    
    /**
     * The summary of the book
     * <P>Type: TEXT</P>
     */
    public static final String SUMMARY = "summary";
    
    /**
     * The rating of the book
     * <P>Type: DOUBLE</P>
     */
    public static final String RATING = "rating";
    
    /**
     * The translator of the book
     * <P>Type: TEXT</P>
     */
    public static final String TRANSLATOR = "translator";
    
    /**
     * The pages of the book
     * <P>Type: INTEGER</P>
     */
    public static final String PAGES = "pages";
    
    /**
     * The illustrate of the book
     * <P>Type: TEXT</P>
     */
    public static final String ILLUSTRATE = "illustrate";
}
