/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.cpp255.bookbarcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.cpp255.bookbarcode.book.BookInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses XML feeds from stackoverflow.com. Given an InputStream
 * representation of a feed, it returns a List of entries, where each list
 * element represents a single entry (post) in the XML feed.
 */
public class DouBanBookInfoXmlParser {
	public static final String TAG = DouBanBookInfoXmlParser.class
			.getSimpleName();
	// 通过豆瓣获取图书信息
	public static final String ISBN_URL = "https://api.douban.com/v2/book/isbn/"; // 返回来的是JSON的编码信息
	public static final int RETURN_BOOKINFO_STATUS = 200;	//返回图书信息
	public static final int BOOK_NOT_FOUND_STATUS = 404;	//图书不存在
	// public static final String ISBN_URL =
	// "http://api.douban.com/book/subject/isbn/"; //返回来的是XML的编码信息

	/**
	 * 从根据isbn号从豆瓣获取数据
	 * 
	 * @param isbnNo
	 * @return
	 * @throws IOException
	 */
	public BookInfo fetchBookInfoByXML(String isbnNo) throws IOException {
		String requestUrl = ISBN_URL + isbnNo;
		URL url = new URL(requestUrl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.connect();
		if(conn.getResponseCode() == RETURN_BOOKINFO_STATUS) {
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			br.close();
			return readBookInfo(sb.toString());
		}
		
		return null;
	}

	// 读取获得的数据
	private BookInfo readBookInfo(String jsonStr) {
		JSONObject jsonObject;
		String nodeInfo = null;
		BookInfo bookInfo = new BookInfo();

		try {
			jsonObject = new JSONObject(jsonStr);
			nodeInfo = jsonObject.getString("title");
			bookInfo.setTitle(nodeInfo);
			bookInfo.setAuthor(parseJSONArraytoString(jsonObject.getJSONArray("author")));
			nodeInfo = jsonObject.getString("pubdate");
			bookInfo.setPublishDate(nodeInfo);
			nodeInfo = jsonObject.getString("publisher");
			bookInfo.setPublisher(nodeInfo);
			nodeInfo = jsonObject.getString("image");
			bookInfo.setThumbnail(DownloadBitmap(nodeInfo));
			nodeInfo = jsonObject.getString("price");
			bookInfo.setPrice(nodeInfo);
			nodeInfo = jsonObject.getString("summary");
			bookInfo.setSummary(nodeInfo);
			nodeInfo = jsonObject.getString("isbn13");
			bookInfo.setISBN(Long.parseLong(nodeInfo));
			nodeInfo = jsonObject.getString("rating");
			bookInfo.setRating(nodeInfo);
			nodeInfo = jsonObject.getString("translator");
			bookInfo.setTranslator(nodeInfo);	
			nodeInfo = jsonObject.getString("pages");
			bookInfo.setPages(Integer.parseInt(nodeInfo));	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bookInfo;
	}

	public Bitmap DownloadBitmap(String bitmapUrl) {
		Bitmap bitmap = null;
		BufferedInputStream bis = null;

		try {
			URL url = new URL(bitmapUrl);
			URLConnection conn = url.openConnection();
			bis = new BufferedInputStream(conn.getInputStream());
			bitmap = BitmapFactory.decodeStream(bis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null)
					bis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	public String parseJSONArraytoString(JSONArray array) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < array.length(); i++) {
			try {
				str = str.append(array.getString(i)).append(" ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str.toString();
	}
}
