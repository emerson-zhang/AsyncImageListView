
package com.zy.asynclistimage.imageLoader;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Handler;

public class AsyncImageManager
{
    /**
    *用于下载图片
    */
    private ImageLoader imageLoader;
    
    final Handler handler = new Handler();

    public AsyncImageManager()
    {
        // 使用代理
        imageLoader = new ImageLoaderProxy();
    }

    /**
     *	[简要描述]:
     *	[详细描述]:
     *	@author	[lscm]
     *	@email	[emerson.zhang@b5m.com]
     *	@date	[Sep 6, 2012]
     *	@method	[getImage]
     *	@param imageUrls
     *	@param width
     *	@param listener
     */
    public void getImage(final String[] imageUrls , final ImageLoadListener listener)
    {
        Thread loadImageThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // System.out.println("first load");
                loadImage(imageUrls,  listener);

            }

        });
        loadImageThread.start();
    }
    
    /**
     *  [简要描述]:尝试读取图片，结束后通知主线程
     *  [详细描述]:
     *  @author [lscm]
     *  @email  [emerson.zhang@b5m.com]
     *  @date   [Aug 23, 2012]
     *  @method [loadImage]
     *  @param imageUrls
     *  @param width
     *  @param height
     *  @param rowPosition
     *  @param listener
     */
    private void loadImage(final String[] imageUrls ,final ImageLoadListener listener)
    {
        for (int i = 0; i < imageUrls.length; i++)
        {
            System.out.println("loadImage calling. url is:" + imageUrls[i]);
            try
            {
                final Bitmap bitmap = imageLoader.getImage(imageUrls[i]);
                if (bitmap != null)
                {
                    // 通知主线程图像已经加载完成
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            listener.onImageLoad(bitmap);
                        }
                    });

                    // 因为已经加载到图像，就跳出循环
                    return;
                }
            }
            catch (final IOException e)
            {
                // 遇到IO问题，通知主线程读取出错
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        listener.onError(e);
                    }
                });
                e.printStackTrace();
            }
        }

    }
}
