package com.example.qrky;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;


/**
 * A module for Glide to use Firebase Storage Images
 */
@GlideModule
public class MyGlideApp extends AppGlideModule {

    /**
     * Register FirebaseImageLoader to handle StorageReference
     * @param context Context
     * @param glide Glide
     * @param registry Registry
     */
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
    }

    /**
     * Apply options to the builder here.
     * @param context Context
     * @param builder GlideBuilder
     */
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
    }
}

