package com.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.imageload.transform.GlideCircleTransform;
import com.imageload.transform.GlideRoundTransform;
import com.imageload.utils.GlideCacheUtils;
import com.mill.thread.ThreadUtils;
import com.mill.utils.ContextUtils;

import java.io.File;

/**
 * Created by lulei-ms on 2018/3/2.
 */
public class ImageLoadImp implements IImageLoader {

    private static IImageLoader mInstance;

    private ImageLoadImp() {

    }

    public static IImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoadImp.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoadImp();
                }
            }
        }
        return mInstance;
    }

    public Drawable getPlaceholder(ImageView imageView) {
        Drawable placeholder = (Drawable) imageView.getTag(R.id.glide_placeholder);
        if (placeholder == null) {
            placeholder = imageView.getDrawable();
            if (placeholder == null) {
                placeholder = ContextUtils.getApplicationContext().getResources().getDrawable(android.R.color.transparent);
            }
            imageView.setTag(R.id.glide_placeholder, placeholder);
        }
        return placeholder;
    }


    @Override
    public void setImageByGif(ImageView imageView, String url, int count) {
        Glide.with(imageView.getContext()).load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideDrawableImageViewTarget(imageView, count));
    }

    @Override
    public void setImageByUrl(ImageView imageView, String url) {
        setImageByUrl(imageView, url, 0, 0, null);
    }

    public void setImageByUrl(ImageView imageView, String url, int width, int height) {
        setImageByUrl(imageView, url, width, height, null);
    }

    public void setImageByUrl(ImageView imageView, String url, int width, int height, final ImageLoadListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        RequestListener requestListener = null;
        if (listener != null) {
            requestListener = new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                    listener.onResult(-1, e);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                    listener.onResult(1, glideDrawable);
                    return false;
                }
            };
        }
        DrawableTypeRequest<String> dtr = Glide.with(imageView.getContext()).load(url);
        if (width != 0 && height != 0) {
            dtr.override(width, height);
        }
        dtr.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        dtr.placeholder(getPlaceholder(imageView));
        dtr.crossFade();
        dtr.listener(requestListener).into(imageView);
    }

    @Override
    public void setImageByFilePath(ImageView imageView, String filePath, int width, int height) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        Glide.with(imageView.getContext()).load(new File(filePath))
                .override(width, height)
                .placeholder(getPlaceholder(imageView))
                .crossFade().into(imageView);
    }

    /**
     * 不建议使用该方法了，
     * placeholder可以通过 ImageView直接设置 android:src 属性 搞定
     */
    @Override
    @Deprecated
    public void setImageByFilePath(ImageView imageView, String filePath, int placeholder, int width, int height) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        Glide.with(imageView.getContext()).load(new File(filePath))
                .override(width, height)
                .placeholder(placeholder)
                .crossFade().into(imageView);
    }

    @Override
    public void setImageByResId(ImageView imageView, int resId) {
        Glide.with(imageView.getContext()).load(resId).crossFade().into(imageView);
    }

    @Override
    public void setViewBackGround(View view, String url) {
        ViewTarget viewTarget = new ViewTarget<View, GlideDrawable>(view) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                this.view.setBackgroundDrawable(resource);
            }
        };
        Glide.with(view.getContext()).load(url).crossFade().into(viewTarget);
    }

    @Override
    public void getBitmapFromUrl(Context context, String url, final ImageLoadListener listener) {
        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (listener != null) {
                    listener.onResult(1, resource);
                }
            }
        });
    }

    public void getPathFromUrl(final Context context, final String url, final ImageLoadListener listener) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(context)
                        .load(url)
                        .downloadOnly(new SimpleDownloadOnlyTarget() {
                            @Override
                            public void onLoadFailed(Exception e, Drawable drawable) {
                                if (listener != null) {
                                    listener.onResult(0, "");
                                }
                            }

                            @Override
                            public void onResourceReady(File file, GlideAnimation<? super File> glideAnimation) {
                                if (file == null) {
                                    if (listener != null) {
                                        listener.onResult(0, "");
                                    }
                                    return;
                                }
                                String path = file.getAbsolutePath();
                                if (listener != null) {
                                    listener.onResult(1, path);
                                }
                            }

                            @Override
                            public void getSize(SizeReadyCallback sizeReadyCallback) {
                                if (sizeReadyCallback != null) {
                                    sizeReadyCallback.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                }
                            }
                        });
            }
        });

    }

    @Override
    public void setRoundImageByUrl(ImageView imageView, String url, int dp) {
        setRoundImageByUrl(imageView, url, dp, 0, 0, GlideRoundTransform.CornerType.ALL);
    }

    @Override
    public void setRoundImageByUrl(ImageView imageView, String url, int dp, GlideRoundTransform.CornerType cornerType) {
        setRoundImageByUrl(imageView, url, dp, 0, 0, cornerType);
    }

    @Override
    public void setRoundImageByUrl(ImageView imageView, String url, int dp, int width, int height, GlideRoundTransform.CornerType cornerType) {
        BitmapTransformation[] transformations = null;
        if (imageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            transformations = new BitmapTransformation[2];
            transformations[0] = new CenterCrop(imageView.getContext());
            transformations[1] = new GlideRoundTransform(imageView.getContext(), dp, cornerType);
        } else {
            transformations = new BitmapTransformation[1];
            transformations[0] = new GlideRoundTransform(imageView.getContext(), dp, cornerType);
        }

        DrawableTypeRequest<String> dtr = Glide.with(imageView.getContext()).load(url);
        if (width != 0 && height != 0) {
            dtr.override(width, height);
        }
        dtr.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        dtr.placeholder(getPlaceholder(imageView));
        dtr.crossFade();
        dtr.transform(transformations).into(imageView);
    }

    @Override
    public void setCircleImageByUrl(ImageView imageView, String url) {
        BitmapTransformation[] transformations = null;
//        if (imageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
        transformations = new BitmapTransformation[2];
        transformations[0] = new CenterCrop(imageView.getContext());
        transformations[1] = new GlideCircleTransform(imageView.getContext());
//        } else {
//            transformations = new BitmapTransformation[1];
//            transformations[0] = new GlideCircleTransform(imageView.getContext());
//        }

        Glide.with(imageView.getContext()).load(url)
                .placeholder(getPlaceholder(imageView))
                .crossFade()
                .transform(transformations).into(imageView);
    }

    @Override
    public void setStrokeCircleImgByUrl(ImageView imageView, String url, float strokeWidth, int strokeColor) {

        BitmapTransformation[] transformations = null;
//        if (imageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
        transformations = new BitmapTransformation[2];
        transformations[0] = new CenterCrop(imageView.getContext());
        transformations[1] = new GlideCircleTransform(imageView.getContext(), strokeWidth, strokeColor);
//        } else {
//            transformations = new BitmapTransformation[1];
//            transformations[0] = new GlideCircleTransform(imageView.getContext(), strokeWidth, strokeColor);
//        }

        Glide.with(imageView.getContext()).load(url)
                .placeholder(getPlaceholder(imageView))
                .crossFade()
                .transform(transformations).into(imageView);
    }

    @Override
    public void setRoundImageByFilePath(ImageView imageView, String filePath, int width, int height, int dp) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        BitmapTransformation[] transformations = null;
        if (imageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            transformations = new BitmapTransformation[2];
            transformations[0] = new CenterCrop(imageView.getContext());
            transformations[1] = new GlideRoundTransform(imageView.getContext(), dp, GlideRoundTransform.CornerType.ALL);
        } else {
            transformations = new BitmapTransformation[1];
            transformations[0] = new GlideRoundTransform(imageView.getContext(), dp, GlideRoundTransform.CornerType.ALL);
        }

        Glide.with(imageView.getContext()).load(new File(filePath))
                .override(width, height)
                .placeholder(getPlaceholder(imageView))
                .crossFade()
                .transform(transformations).into(imageView);
    }

    @Override
    public void resumeRequests(Context context) {
        Glide.with(context).resumeRequests();
    }

    @Override
    public void pauseRequests(Context context) {
        Glide.with(context).pauseRequests();
    }

    @Override
    public void clearCache(Context context) {
        GlideCacheUtils.clearImageAllCache(context);
    }

    public String getDiskCacheDir(Context context) {
        return context.getCacheDir() + File.separator + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
    }

    @Override
    public void clearMemoryCacheByUrl(Context context, String url) {
        // TODO 暂时无实现
    }
}
