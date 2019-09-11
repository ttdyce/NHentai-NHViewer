package github.ttdyce.nhviewer.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import github.ttdyce.nhviewer.R;

import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

@GlideExtension
public class MyAppExtension {
    // Size of mini thumb in pixels.
    private static final int MINI_THUMB_SIZE = 100;

    private MyAppExtension() {
    } // utility class

    @GlideOption
    @NonNull
    public static RequestOptions customFormat(RequestOptions options) {
        return options
                .placeholder(R.color.cardview_dark_background)
                .format(PREFER_ARGB_8888)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache image after loaded for first time

                ;
    }

}