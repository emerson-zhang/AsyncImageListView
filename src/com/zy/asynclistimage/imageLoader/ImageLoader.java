package com.zy.asynclistimage.imageLoader;

import java.io.IOException;

import android.graphics.Bitmap;

/**
 * [简要描述]:加载图片资源的接口类 [详细描述]:
 * 
 * @author [Emerson]
 * @email [emerson.zhang@b5m.com]
 * @version [版本号,Aug 22, 2012]
 */
public interface ImageLoader {

	/**
	 * [简要描述]:获取图片资源 [详细描述]:给定图片url，获得Drawable对象
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 22, 2012]
	 * @method [getImage]
	 * @param imageUrl
	 *            图片地址
	 * @return 获取的Drawable对象，有可能是null
	 * @throws IOException
	 *             当发生IO错误时，抛出此异常
	 */
	public Bitmap getImage(String imageUrl) throws IOException;

	/**
	 * [简要描述]:获取图片资源 [详细描述]:给定图片url，获得Drawable对象
	 * 
	 * @author [Emerson]
	 * @email [emerson.zhang@b5m.com]
	 * @date [Aug 28, 2012]
	 * @method [getImage]
	 * @param imageUrl
	 *            图片地址
	 * @param width
	 *            图像宽，如果是0，则加载原图；否则按照指定大小压缩
	 * @param height
	 *            图像高，如果是0，则加载原图；否则按照指定大小压缩
	 * @return 获取的Drawable对象，有可能是null
	 * @throws IOException
	 */
	public Bitmap getImage(String imageUrl, int width, int height) throws IOException;

}
