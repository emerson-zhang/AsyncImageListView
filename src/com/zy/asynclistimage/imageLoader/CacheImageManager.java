package com.zy.asynclistimage.imageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.zy.asynclistimage.util.MD5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * [简要描述]:缓存管理中心，主要用于管理图片缓存 [详细描述]:本类目前主要功能为搜索列表的图片提供缓存支持，缓存的位置有RAM和外置存储卡两种
 * 
 * @author [Emerson Zhang]
 * @email [emerson.zhang@b5m.com]
 * @version [版本号,Aug 23, 2012]
 * @see [CacheImageManager]
 * @package [com.b5mandroid.utils.imageloader]
 * @since [comb5mandroid]
 */
public class CacheImageManager {

	/**
	 * 图像内存缓存
	 */
	private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private static final String path = Environment
			.getExternalStorageDirectory() + "/zy_image_cache/";

	private static CacheImageManager cacheImageManager;

	/**
	 * [简要描述]:返回一个管理缓存图片的实例 [详细描述]:
	 * 
	 * @author [lscm]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 23, 2012]
	 * @method [getCacheImageManager]
	 * @return
	 */
	public static CacheImageManager getCacheImageManager() {
		if (cacheImageManager == null) {
			cacheImageManager = new CacheImageManager();
		}
		return cacheImageManager;
	}

	/**
	 * [简要描述]:屏蔽其他类对其进行实例化对象 [详细描述]: <构造器>
	 */
	private CacheImageManager() {
	}

	private void checkDir() {
		File saveDir = new File(path);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
	}

	/**
	 * [简要描述]:从缓存中获取图像 [详细描述]:
	 * 
	 * @author [lscm]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 22, 2012]
	 * @method [getImageFromCache]
	 * @param imageUrl
	 * @return
	 */
	public Bitmap getImageFromCache(String imageUrl) {
		if (imageUrl == null || imageUrl.equals("")) {
			return null;
		}
		Bitmap bitmap = null;
		imageUrl = urlToFilename(imageUrl);
		bitmap = getImageFromRAMCache(imageUrl);
		if (bitmap != null) {
			return bitmap;
		}

		bitmap = getImageFromExternalStorage(imageUrl);
		if (bitmap != null) {
			return bitmap;
		}
		return bitmap;
	}

	/**
	 * [简要描述]:从内存中读取图片缓存 [详细描述]:
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 22, 2012]
	 * @method [getImageFromMemoryCache]
	 * @param imageUrl
	 * @return 从内存缓存中取出的图像，有可能为null
	 */
	private Bitmap getImageFromRAMCache(String imageUrl) {
		Bitmap bitmap = null;
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			bitmap = softReference.get();
			// Log.d("CacheImageManager.getImageFromMemoryCache",
			// "try to read from RAM cache");
			if (bitmap != null && !bitmap.isRecycled()) {
				return bitmap;
			}
		}
		return bitmap;
	}

	/**
	 * [简要描述]:从外部存储器上尝试获得缓存图片 [详细描述]:
	 * 
	 * @author [lscm]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 22, 2012]
	 * @method [getImageFromExternalStorage]
	 * @param imageUrl
	 * @return 从外存中取出的图像，有可能为null
	 */
	private Bitmap getImageFromExternalStorage(String imageUrl) {
		checkDir();
		Bitmap bitmap = null;

		if (imageUrl == null) {
			return null;
		}

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				bitmap = BitmapFactory.decodeFile(imageUrl);
				updateDrawableCache(imageUrl, bitmap, false);
			} catch (Exception e) {
				e.printStackTrace();
				bitmap = null;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				bitmap = null;
				recycleRAMBitmaps();
			}
		}
		return bitmap;
	}

	/**
	 * [简要描述]:更新缓存 [详细描述]:
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 22, 2012]
	 * @method [updateDrawableCache]
	 * @param url
	 *            文件url，作为缓存的key或者保存文件时的文件名来源
	 * @param drawable
	 *            加载得到的drawable文件
	 * @param saveToFile
	 *            是否需要以文件形式保存
	 */
	public void updateDrawableCache(String url, Bitmap bitmap,
			boolean saveToFile) {
		imageCache.put(url, new SoftReference<Bitmap>(bitmap));

		// 只有在存储卡为挂载状态的时候，才尝试像存储卡写入
		if ((saveToFile)
				&& (Environment.getExternalStorageState()
						.equals(Environment.MEDIA_MOUNTED))) {
			saveBitmapToFile(bitmap, url);
		}
	}

	/**
	 * [简要描述]:释放内存中的bitmap [详细描述]:
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 23, 2012]
	 * @method [recycleRAMBitmaps]
	 * @retruntype [void]
	 */
	public void recycleRAMBitmaps() {
		for (Map.Entry<String, SoftReference<Bitmap>> entry : imageCache
				.entrySet()) {
			SoftReference<Bitmap> softReference = entry.getValue();
			if (softReference.get() != null
					&& !softReference.get().isRecycled()) {
				softReference.get().recycle();
			}
			softReference.clear();
		}
		imageCache.clear();
		System.gc();
	}

	/**
	 * 将bitmap保存到file [简要描述]: [详细描述]:
	 * 
	 * @author [lscm]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 23, 2012]
	 * @method [saveBitmapToFile]
	 * @param bm
	 * @param bitName
	 */
	private void saveBitmapToFile(Bitmap bm, String bitName) {
		checkDir();
		File f = new File(urlToFilename(bitName));
		if (f.exists()) {
			return;
		}
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			recycleRAMBitmaps();
		} finally {
			if (fOut != null) {
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * [简要描述]:从url值转换为文件名 [详细描述]:
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 23, 2012]
	 * @method [urlToFilename]
	 * @param imageUrl
	 * @return 转换后的以.jpg结尾的文件名
	 * @retruntype [String]
	 */
	public String urlToFilename(String imageUrl) {
		// System.out.println("== " + imageUrl + " ==");
		String filename = MD5.getMD5(imageUrl.getBytes()) + ".jpg";
		return path + filename;
	}
}
