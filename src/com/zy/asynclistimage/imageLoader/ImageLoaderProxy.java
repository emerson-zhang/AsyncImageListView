package com.zy.asynclistimage.imageLoader;

import java.io.IOException;

import android.graphics.Bitmap;

/**
 * [简要描述]:图片加载器的代理类 [详细描述]:代理了从网络加载图片的方法，在加载图片后配合缓存管理器进行缓存管理
 * 
 * @author [Emerson Zhang]
 * @email [emerson.zhang@b5m.com]
 * @version [版本号,Aug 23, 2012]
 * @see [ImageLoaderProxy]
 * @package [com.b5mandroid.utils.imageloader]
 * @since [comb5mandroid]
 */
public class ImageLoaderProxy implements ImageLoader {
	/**
	 * 从网络加载资源，此对象为真实调用对象
	 */
	private ImageLoader internetImageLoader;
	private CacheImageManager cacheImageManager;

	public ImageLoaderProxy() {
		this.internetImageLoader = new InternetImageLoader();
		cacheImageManager = CacheImageManager.getCacheImageManager();
	}

	@Override
	public Bitmap getImage(String imageUrl) throws IOException {
		return getImage(imageUrl, 0, 0);
	}

	@Override
	public Bitmap getImage(String imageUrl, int width, int height) throws IOException {
		// 尝试从网络读取
		Bitmap resultBitmap = null;
		resultBitmap = internetImageLoader.getImage(imageUrl, width, height);

		// 更新缓存
		updateCache(resultBitmap, imageUrl);
		return resultBitmap;
	}

	private void updateCache(Bitmap bitmap, String url) {
		if (bitmap != null) {
			cacheImageManager.updateDrawableCache(url, bitmap, true);
		}
	}
}