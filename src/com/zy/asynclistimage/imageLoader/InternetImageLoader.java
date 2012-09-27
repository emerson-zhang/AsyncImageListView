package com.zy.asynclistimage.imageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 *	[简要描述]:从网络加载图片
 *	[详细描述]:
 *	@author	[Emerson Zhang]
 *	@email	[emerson.zhang@b5m.com]
 *	@version	[版本号,Sep 27, 2012]
 *	@see		[InternetImageLoader]
 *	@package	[com.b5mandroid.utils.imageloader]
 *	@since	[AsyncListImage]
 */
public class InternetImageLoader implements ImageLoader {

	@Override
	public Bitmap getImage(String imageUrl) throws IOException {
		return getImage(imageUrl, 0, 0);
	}

	@Override
	public Bitmap getImage(String imageUrl, int width, int height) {
		Bitmap bitmap = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(imageUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			int length = (int) conn.getContentLength();

			//压缩
			if ((length != -1) && (height != 0) && (width != 0)) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				bitmap = BitmapFactory.decodeByteArray(imgData, 0, length,
						options);
				options.inJustDecodeBounds = false;
				int be = (int) (options.outHeight / (float) height);
				if (be <= 0)
					be = 1;
				options.inSampleSize = be;
				bitmap = BitmapFactory.decodeByteArray(imgData, 0, length,
						options);
			} else {
				bitmap = BitmapFactory.decodeStream(is);
			}
		} catch (Exception e) {
			bitmap = null;
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.disconnect();
		}

		return bitmap;
	}
}