package com.bclymer.pokecontacts;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;

public class ImageManager implements OnScrollListener, ComponentCallbacks2 {

	private LruCache<String, Bitmap> mCache;
	private Context mContext;
	private boolean loadOnFling;
	private boolean isFling;

	public ImageManager(Context context) {
		mContext = context;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass() * 1024 * 1024;
		mCache = new LruCache<String, Bitmap>(memoryClass);
	}

	public void setLoadOnFling(boolean load) {
		loadOnFling = load;
	}

	public void display(String url, ImageView imageview) {
		if (isFling && !loadOnFling) {
			return;
		}
		Bitmap image = mCache.get(url);
		if (image != null) {
			imageview.setImageBitmap(image);
		} else {
			imageview.setImageBitmap(null);
			new SetImageTask(imageview).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
		}
	}

	private class SetImageTask extends AsyncTask<String, Void, Integer> {
		private ImageView imageview;
		private Bitmap bmp;

		public SetImageTask(ImageView imageview) {
			this.imageview = imageview;
		}

		@Override
		protected Integer doInBackground(String... params) {
			Uri uri = Uri.parse(params[0]);
			try {
				Bitmap bmpLarge = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
				bmp = Bitmap.createScaledBitmap(bmp, 30, 30, false);
				bmpLarge.recycle();
				if (bmp != null) {
					mCache.put(params[0], bmp);
				} else {
					return 0;
				}
			} catch (Exception e) {
				return 0;
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				imageview.setImageBitmap(bmp);
			}
			super.onPostExecute(result);
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_IDLE:
		case SCROLL_STATE_TOUCH_SCROLL:
			isFling = false;
			break;
		case SCROLL_STATE_FLING:
			isFling = true;
			break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
	}

	@Override
	public void onLowMemory() {
		
	}

	@Override
	public void onTrimMemory(int level) {
		if (level >= TRIM_MEMORY_MODERATE) {
            mCache.evictAll();
        } else if (level >= TRIM_MEMORY_BACKGROUND) {
            mCache.trimToSize(mCache.size() / 2);
        }
	}

}
