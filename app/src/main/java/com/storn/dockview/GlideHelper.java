package com.storn.dockview;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

/**
 * Created by tianshutong on 2018/2/13.
 */

public class GlideHelper {

    public static void loadGif(Context context, String url, ImageView view, OnImageLoadListener listener) {
        try {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                            if (resource instanceof Animatable) {
                                view.setImageDrawable(resource);
                                ((Animatable) resource).start();
                            } else {
                                view.setImageDrawable(resource);
                            }
                            listener.onSuccess(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            listener.onFailed();
                        }
                    });
        } catch (Exception e) {
            listener.onFailed();
        }
    }

    public static void downloadImage(Context context, String url, OnImageLoadListener listener) {
        try {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                            listener.onSuccess(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            listener.onFailed();
                        }
                    });
        } catch (Exception e) {
            listener.onFailed();
        }
    }
}
