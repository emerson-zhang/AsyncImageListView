package com.zy.asynclistimage.imageLoader;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * [简要描述]:异步图片加载管理器 [详细描述]:用于管理异步的图片加载，内部采用多线程进行图片下载
 * 
 * @author [Emerson Zhang]
 * @email [emerson.zhang@b5m.com]
 * @version [版本号,Aug 23, 2012]
 * @see [AsyncImageManager]
 * @package [com.b5mandroid.utils.imageloader]
 * @since [comb5mandroid]
 */
public class AsyncListImageManager {
	/**
	 * 用于下载图片
	 */
	private ImageLoader imageLoader;

	/**
	 * 同步锁
	 */
	private Object lock = new Object();

	/**
	 * 允许开始加载图片的标志
	 */
	private boolean mAllowImageLoad = true;

	/**
	 * 是否第一次加载
	 */
	private boolean mFirstLoad = true;

	/**
	 * 加载的起始位置
	 */
	private int mStartLoadLimit = 0;

	/**
	 * 加载的终止位置
	 */
	private int mStopLoadLimit = 0;

	final Handler handler = new Handler();

	public AsyncListImageManager() {
		// 使用代理
		imageLoader = new ImageLoaderProxy();
	}

	public void restore() {
		mAllowImageLoad = true;
		mFirstLoad = true;
	}

	public void lock() {
		// System.out.println("!!!!!!!!!!!!lock!!!!!!!!!!!!!");
		mAllowImageLoad = false;
		mFirstLoad = false;
	}

	public void unlock() {
		mAllowImageLoad = true;
		// System.out.println("????????????  unlock  ???????");
		synchronized (lock) {
			// System.out.println("========= notify ========");
			lock.notifyAll();
		}
	}

	public void setLoadLimit(int start, int end) {
		if (start > end) {
			return;
		}
		this.mStartLoadLimit = start;
		this.mStopLoadLimit = end;
	}

	/**
	 * [简要描述]:新建一个线程进行图片加载。 [详细描述]:收到调用请求后，新建一个线程，准备加载图片。
	 * 加载图片的线程启动后会首先判断当前是否允许加载图片（目前的逻辑是滚动时不允许加载图片）。 如果允许，再次判断自己是否处于需要加载图片的范围中，
	 * 也就是说，虽然线程启动了，但判断出当前线程需要加载的图片的所在的行已经在手机的可视范围之外， 就什么都不做，run方法自然结束
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 23, 2012]
	 * @method [prepareLoadImageThread]
	 * @param rowPosition
	 *            要加载图像的行位置
	 * @param imageUrls
	 *            期望得到的图像URL
	 * @param width
	 *            期望得到的图像宽
	 * @param height
	 *            期望得到的图像高
	 * @param listener
	 *            图像加载完成后的回调
	 */
	public void prepareLoadImageThread(final Integer rowPosition,
			final String imageUrl,
			final ScrollableViewImageLoadListener listener) {
		Thread workerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// 如果当前不允许加载图片，通常发生在当前正在滚动视图的情况
				if (!mAllowImageLoad) {
					synchronized (lock) {
						try {
							// System.out.println("waitting here, my position: "+rowPosition);
							// 先挂起，等待被唤醒（）
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				// 当符合加载条件后开始加载图片

				// 第一次加载（没有滚动事件）
				if (mAllowImageLoad && mFirstLoad) {
					// System.out.println("first load");
					loadImage(imageUrl, rowPosition, listener);
					return;
				}

				// 当前行的位置处于用户可见范围内，执行加载。然后其余的线程自动结束
				if (mAllowImageLoad && rowPosition <= mStopLoadLimit
						&& rowPosition >= mStartLoadLimit) {
					// System.out.println("start to load image,my position:" +
					// rowPosition);
					loadImage(imageUrl, rowPosition, listener);
					return;
				}
			}
		});

		workerThread.start();

	}

	/**
	 * [简要描述]:尝试读取图片，结束后通知主线程 [详细描述]:
	 * 
	 * @author [lscm]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 23, 2012]
	 * @method [loadImage]
	 * @param imageUrl
	 * @param width
	 * @param height
	 * @param rowPosition
	 * @param listener
	 */
	private void loadImage(final String imageUrl, final Integer rowPosition,
			final ScrollableViewImageLoadListener listener) {
		// System.out.println("loadImage calling. position is:" + rowPosition);
		try {
			final Bitmap drawable = imageLoader.getImage(imageUrl);
			if (drawable != null) {
				// 通知主线程图像已经加载完成
				handler.post(new Runnable() {
					@Override
					public void run() {
						// System.out.println("really do notify here, position:"
						// + rowPosition);
						listener.onImageLoad(rowPosition, drawable);
					}
				});

				// 因为已经加载到图像，就跳出循环
				return;
			}
		} catch (IOException e) {
			// 遇到IO问题，通知主线程读取出错
			handler.post(new Runnable() {
				@Override
				public void run() {
					listener.onError(rowPosition);
				}
			});
			e.printStackTrace();
		}
	}
}