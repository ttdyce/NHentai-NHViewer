package com.github.ttdyce.nhviewer.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.volley.VolleyUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.github.ttdyce.nhviewer.model.proxy.NHVProxyStack;
import com.github.ttdyce.nhviewer.view.MainActivity;

import java.io.InputStream;

@GlideModule
public class CustomGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        VolleyUrlLoader.Factory factory = new VolleyUrlLoader.Factory(MainActivity.isProxied()
                ? Volley.newRequestQueue(context, new NHVProxyStack(MainActivity.proxyHost, MainActivity.proxyPort))
                : Volley.newRequestQueue(context)
        );
        glide.getRegistry().replace(GlideUrl.class, InputStream.class, factory);
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // disable glide/volley disk cache to avoid duplicate data...?
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
        );
    }
}