package com.bclymer.pokecontacts;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class ImageManager implements ComponentCallbacks2 {

	private LruCache<String, Bitmap> mCache;
	public static Bitmap defaultIconThumb;
	public static Bitmap defaultIconFull;
	private Context mContext;
	
	public ImageManager(Context context) {
		mContext = context;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass() * 1024 * 1024;
		mCache = new LruCache<String, Bitmap>(memoryClass);
		defaultIconThumb = getRoundedCornerBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.blank_contact), 7, 30, 30);
		defaultIconFull = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.blank_contact_full);
	}

	public void display(String url, ImageView imageview) {
		Bitmap image = mCache.get(url);
		if (image != null) {
			imageview.setImageBitmap(image);
		} else {
			imageview.setImageBitmap(defaultIconThumb);
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
				bmp = getRoundedCornerBitmap(bmpLarge, 7, 30, 30);
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
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels, int width, int height) {
		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
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
