package com.cpp255.bookbarcode.book;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cpp255.bookbarcode.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Utilities {
	private static final String BitmapFilePath = "/MyLiberary/";
	private static Context mContext;
	
	private Utilities(Context context) {
		 mContext = context;
	}
	
/**
 * 读取本地的图书封面，如果存有，直接读取，返回；否则返回统一的一张图
 * @return book cover
 */
	public static Bitmap getBitmapFromFile(String filePath) {
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			if(file.exists()) {
				bitmap = BitmapFactory.decodeFile(filePath);
			} else {
				Resources res = mContext.getResources();
				bitmap = BitmapFactory.decodeResource(res, R.drawable.default_book_cover);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 *  通过传进来的图像，用ISBN命名存在本地，然后返回路径
	 * @param bitmap 
	 * @param ISBN
	 * @return book cover file path
	 */
	public static String saveBitmapToFile(Bitmap bitmap, String ISBN) {
		String fileName = Environment.getExternalStorageDirectory().toString() + BitmapFilePath + ISBN + ".png";
		File file = new File(fileName);
		FileOutputStream fos = null;
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {

			if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileName;
	}
	
	/**
	 * 创建SD卡上的文件夹，用于保存书封面
	 */
	public static void createFold() {
		String fileName = Environment.getExternalStorageDirectory().toString() + BitmapFilePath;
		String status = Environment.getExternalStorageState();
		if(status.equals(Environment.MEDIA_MOUNTED)) {
			File destDir = new File(fileName);
			if(!destDir.exists()) {
				destDir.mkdirs();
			}
		}
	}
}
