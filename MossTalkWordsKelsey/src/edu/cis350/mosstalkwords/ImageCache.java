package edu.cis350.mosstalkwords;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageCache {
	private LruCache<String, Bitmap> imCache; 
	
	public ImageCache() {
	//set up cache for images
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);	
		
		//use 1/2 size of available memory for cache (probably a bad idea, but YOLO) 
		final int cacheSize = maxMemory/2;
		imCache = new LruCache<String, Bitmap>(cacheSize) {
			//had to add this line to get it to compile; it won't like anything
			//less than api v12
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
	}
	
	public void addBitmapToCache(String key, Bitmap bitmap) {
		if (getBitmapFromCache(key)==null) {
			imCache.put(key, bitmap);
		}
	}
	
	public Bitmap getBitmapFromCache(String key) {
		return imCache.get(key);
	}
	
	public void clearCache() {
		imCache.evictAll();
	}
	

	//scale down images based on display size; helps with OOM errors
	public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 0;
	    int newHeight = height;
	    int newWidth = width;
	    
	    while (newHeight > reqHeight || newWidth > reqWidth) {
	    	newHeight = newHeight/2; //should be power of two 
	    	newWidth = newWidth/2;
	    	inSampleSize += 2;	
	    }
	    if (inSampleSize == 0) { inSampleSize = 2; }
	    Log.d("async task","in sample size is:" + (inSampleSize));
	
	    return inSampleSize;
	}
		
	
	
}
