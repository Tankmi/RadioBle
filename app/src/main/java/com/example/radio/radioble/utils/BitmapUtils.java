package com.example.radio.radioble.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import com.example.radio.radioble.context.PreferenceEntity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
	
	/* 拍照的照片存储位置 */
	private final static File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + PreferenceEntity.KEY_CACHE_PATH);
	
	/**
	 * 根据图片，获取旋转角度
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) { 

		int degree  = 0;  
		try {  
			ExifInterface exifInterface = new ExifInterface(path);  
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
			switch (orientation) {  
			case ExifInterface.ORIENTATION_ROTATE_90:  
				degree = 90;  
				break;  
			case ExifInterface.ORIENTATION_ROTATE_180:  
				degree = 180;  
				break;  
			case ExifInterface.ORIENTATION_ROTATE_270:  
				degree = 270;  
				break;  
			}  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		return degree;  
	} 

	/**
	 * 根据给定的旋转角度，返回一张图片
	 * @param bitmap
	 * @param rotate
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int rotate){

		if(bitmap == null)
			return null ;

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	/**
	 * 根据给定的图片对象，以及想要的宽高，返回一个压缩比例
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}

		return inSampleSize;
	}

	/**
	 * 将sd卡上的图片压缩到指定的比例，并旋转角度
	 * @param path
	 * @return
	 */
	public static String compressImg(String path){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path,options);
		options.inSampleSize = calculateInSampleSize(options,480,800);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path,options);	//压缩对应的倍数
		if(bitmap == null){
			return "";
		}
		int degree = readPictureDegree(path);
		bitmap = BitmapUtils.rotateBitmap(bitmap, degree);

//		File file=new File("/mnt/sdcard/pic/01.jpg");//将要保存图片的路径
		File file=new File(PHOTO_DIR,System.currentTimeMillis() + ".jpg");//将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
		if (file.exists() && file.length() > 0) {
			return file.getAbsolutePath();
		}else{
			return "";
		}
		
	}
	
	/**
	 * 将sd卡上的图片压缩到指定的比例，并旋转角度
	 * @param path
	 * @return
	 */
	public static String compressImg(String path,int width,int height){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path,options);
		options.inSampleSize = calculateInSampleSize(options,width,height);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path,options);	//压缩对应的倍数
		if(bitmap == null){
			return "";
		}
		int degree = readPictureDegree(path);
		bitmap = BitmapUtils.rotateBitmap(bitmap, degree);
		
//		File file=new File("/mnt/sdcard/pic/01.jpg");//将要保存图片的路径
		File file=new File(PHOTO_DIR,System.currentTimeMillis() + ".jpg");//将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
		if (file.exists() && file.length() > 0) {
			return file.getAbsolutePath();
		}else{
			return "";
		}
		
	}
	
	/**
	 * 将bitmap对象保存到本地，并返回保存地址
	 * @param bitmap
	 * @return
	 */
	public static String getImg(Bitmap bitmap){
		if(bitmap == null){
			return "";
		}
		File file=new File(PHOTO_DIR,System.currentTimeMillis() + ".jpg");//将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file.exists() && file.length() > 0) {
			return file.getAbsolutePath();
		}else{
			return "";
		}
	}
}
