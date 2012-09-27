package com.zy.asynclistimage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zy.asynclistimage.doman.Artical;
import com.zy.asynclistimage.imageLoader.AsyncListImageManager;
import com.zy.asynclistimage.imageLoader.CacheImageManager;
import com.zy.asynclistimage.imageLoader.ScrollableViewImageLoadListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewAdapter extends ArrayAdapter<Artical> {
	private ListView mListView;
	private AsyncListImageManager asyncImageManager;
	private CacheImageManager cacheImageManager;
	private ImageLoadCompleteListener imageLoadListener;
	private LayoutInflater mInflater;

	private Map<String, RowWrapper> wrappers;
	/**
	 * 列表行中ImageView对象的tag
	 */
	private static final int IMAGE_VIEW_TAG = 10100;

	public ListViewAdapter(Context context, int textViewResourceId,
			List<Artical> objects, ListView listView) {
		super(context, textViewResourceId, objects);
		mListView = listView;
		asyncImageManager = new AsyncListImageManager();
		cacheImageManager = CacheImageManager.getCacheImageManager();
		imageLoadListener = new ImageLoadCompleteListener();
		mInflater = LayoutInflater.from(context);

		wrappers = new HashMap<String, RowWrapper>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Artical artical = getItem(position);

		View row = convertView;

		RowWrapper wrapper = null;
		if (row == null) {
			row = mInflater.inflate(R.layout.row, parent, false);
			wrapper = new RowWrapper(row);

			// 因为row的tag会被position占用，所以将wrapper存在map中，以row的地址为key
			wrappers.put(row.toString(), wrapper);
		} else {
			wrapper = wrappers.get(row.toString());
		}

		// 为了读取图片的回调能够正确的找到对应的行
		row.setTag(position);

		wrapper.getTextView().setText(artical.text);
		setupImageViewDrawable(wrapper.getImageView(), artical.imageUrl,
				position);

		return row;
	}

	/**
	 * [简要描述]:尝试对listiew中的ImageView设置图片 [详细描述]:
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 22, 2012]
	 * @method [setupImageViewDrawable]
	 * @param imageView
	 * @param imageUrl
	 * @param position
	 */
	private void setupImageViewDrawable(ImageView imageView, String imageUrl,
			int position) {
		// 设置控件 tag，目的是为了在完成加载时用此tag找到控件
		imageView.setTag(IMAGE_VIEW_TAG);

		// 尝试从换从中读取图像
		Bitmap image = null;
		image = cacheImageManager.getImageFromCache(imageUrl);
		if (image != null) {
			imageView.setImageBitmap(image);
			image = null;
			return;
		}

		// 缓存中不存在，先设置默认图片，然后尝试从网络加载
		imageView.setImageResource(R.drawable.google_plus);
		asyncImageManager.prepareLoadImageThread(position, imageUrl,
				imageLoadListener);
	}

	/**
	 * 当列表在滚动的时候，应该调用此方法，阻止列表图片加载
	 */
	public void lockLoadingImageThreadWhenScrolling() {
		asyncImageManager.lock();
	}

	/**
	 * 当列表停止滚动，调用此方法，开始加载图片
	 */
	public void unlockloadingImageThread() {
		int start = mListView.getFirstVisiblePosition();
		int end = mListView.getLastVisiblePosition();
		if (end >= getCount()) {
			end = getCount() - 1;
		}
		// System.out.println("unlockloadingImageThread start "+start+";  end "+end);
		asyncImageManager.setLoadLimit(start, end);
		asyncImageManager.unlock();
	}

	/**
	 * [简要描述]: [详细描述]:
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @version [版本号,Aug 22, 2012]
	 */
	private class ImageLoadCompleteListener implements
			ScrollableViewImageLoadListener {
		@Override
		public void onImageLoad(Integer rowPosition, Bitmap bitmap) {
			// 找到图像所在行
			View row = mListView.findViewWithTag(rowPosition);
			if (row != null) {
				((ImageView) (row.findViewById(R.id.row_image)))
						.setImageBitmap(bitmap);
				bitmap = null;
			}
		}

		@Override
		public void onError(Integer rowPosition) {
			Log.d("onImageLoad", "onError! " + rowPosition);
		}
	}
}

class RowWrapper {
	private View base;
	private ImageView imageView;
	private TextView textView;

	public RowWrapper(View base) {
		this.base = base;
	}

	public ImageView getImageView() {
		if (imageView == null) {
			imageView = (ImageView) base.findViewById(R.id.row_image);
		}
		return imageView;
	}

	public TextView getTextView() {
		if (textView == null) {
			textView = (TextView) base.findViewById(R.id.row_text);
		}
		return textView;
	}
}
