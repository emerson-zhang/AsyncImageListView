
package com.zy.asynclistimage.imageLoader;

import android.graphics.Bitmap;

/**
 *	[简要描述]:适用于滚动视图的图像加载监听，配合AsyncImageManager类使用
 *	[详细描述]:
 *	@author	[Emerson Zhang]
 *	@email	[emerson.zhang@b5m.com]
 *	@version	[版本号,Aug 23, 2012]
 *	@see		[ScrollableViewImageLoadListener]
 *	@package	[com.b5mandroid.utils.imageloader]
 *	@since	[comb5mandroid]
 */
public interface ScrollableViewImageLoadListener
{
    /**
     * 图像加载完成后调用此函数
     * 
     * @param rowPosition
     *            完成加载的行位置
     * @param bitmap
     *            加载好的图像
     */
    public void onImageLoad(Integer rowPosition , Bitmap bitmap);

    /**
     * 图像加载失败，调用此函数
     * 
     * @param rowPosition
     *            失败的行位置
     */
    public void onError(Integer rowPosition);
}
