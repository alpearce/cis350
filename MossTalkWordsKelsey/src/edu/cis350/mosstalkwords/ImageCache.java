package edu.cis350.mosstalkwords;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

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
	

	
	
}
