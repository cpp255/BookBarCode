package com.cpp255.bookbarcode.book;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * 
 * @author jdk
 *  2013-10-17
 *
 */

public class BookInfo implements Parcelable{
	private Long _id;
	private String mTitle;
	private long mISBN;
	private String mAuthor;
	private Bitmap mThumbnail;
	private String mThumbnailFilePath;
	private String mPublisher;
	private String mPublishDate;
	private String mPrice;
	private String mSummary;
	private String mRating;
	private String mTranslator;
	private int mPages;
	private String mIllustrate;
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String mName) {
		this.mTitle = mName;
	}
	public long getISBN() {
		return mISBN;
	}
	public void setISBN(long mISBN) {
		this.mISBN = mISBN;
	}
	public String getAuthor() {
		return mAuthor;
	}
	public void setAuthor(String mAuthor) {
		this.mAuthor = mAuthor;
	}
	public String getThumbnailFilePath() {
		return mThumbnailFilePath;
	}
	public void setThumbnailFilePath(String mThumbnailFilePath) {
		this.mThumbnailFilePath = mThumbnailFilePath;
	}
	public String getPublisher() {
		return mPublisher;
	}
	public void setPublisher(String mPress) {
		this.mPublisher = mPress;
	}
	public String getPublishDate() {
		return mPublishDate;
	}
	public void setPublishDate(String mPublishDate) {
		this.mPublishDate = mPublishDate;
	}
	public String getPrice() {
		return mPrice;
	}
	public void setPrice(String mPrice) {
		this.mPrice = mPrice;
	}
	public String getSummary() {
		return mSummary;
	}
	public void setSummary(String mSummary) {
		this.mSummary = mSummary;
	}
	public String getRating() {
		return mRating;
	}
	public void setRating(String mRating) {
		this.mRating = mRating;
	}
	public String getTranslator() {
		return mTranslator;
	}
	public void setTranslator(String mTranslator) {
		this.mTranslator = mTranslator;
	}
	public int getPages() {
		return mPages;
	}
	public void setPages(int mPages) {
		this.mPages = mPages;
	}

	public String getIllustrate() {
		return mIllustrate;
	}
	public void setIllustrate(String mIllustrate) {
		this.mIllustrate = mIllustrate;
	}
	public Bitmap getThumbnail() {
		return mThumbnail;
	}
	public void setThumbnail(Bitmap mThumbnail) {
		this.mThumbnail = mThumbnail;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(mTitle);
		dest.writeLong(mISBN);
		dest.writeString(mAuthor);
		dest.writeParcelable(mThumbnail, flag);
		dest.writeString(mThumbnailFilePath);
		dest.writeString(mPublisher);
		dest.writeString(mPublishDate);
		dest.writeString(mPrice);
		dest.writeString(mSummary);
		dest.writeString(mRating);
		dest.writeString(mTranslator);
		dest.writeInt(mPages);
		dest.writeString(mIllustrate);
	}

	public Long get_id() {
		return _id;
	}
	public void set_id(Long _id) {
		this._id = _id;
	}

	public static final Parcelable.Creator<BookInfo> CREATOR = new Creator<BookInfo>() {
		@Override
		public BookInfo createFromParcel(Parcel source) {
			BookInfo bookInfo = new BookInfo();
			bookInfo.mTitle = source.readString();
			bookInfo.mISBN = source.readLong();
			bookInfo.mAuthor = source.readString();
			bookInfo.mThumbnail = source.readParcelable(Bitmap.class.getClassLoader());
			bookInfo.mThumbnailFilePath = source.readString();
			bookInfo.mPublisher = source.readString();
			bookInfo.mPublishDate = source.readString();
			bookInfo.mPrice = source.readString();
			bookInfo.mSummary = source.readString();
			bookInfo.mRating = source.readString();
			bookInfo.mTranslator = source.readString();
			bookInfo.mPages = source.readInt();
			bookInfo.mIllustrate = source.readString();
			return bookInfo;
		}

		@Override
		public BookInfo[] newArray(int size) {
			return new BookInfo[size];
		}
		
	};
}
