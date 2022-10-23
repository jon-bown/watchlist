package edu.utap.watchlist.glide

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.Glide as GlideApp
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

import edu.utap.watchlist.R


@GlideModule
class AppGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

object Glide {
    private var glideOptions: RequestOptions = RequestOptions()
        // Options like CenterCrop are possible, but I like this one best
        .fitCenter()
        // A placeholder image for when the network is slow
        .placeholder(R.drawable.ic_baseline_cloud_24)
        // Rounded corners are so lovely.
        .transform(RoundedCorners(20))
        // If we can't fetch, give the user an indication  maybe it should
        // say "network error"
        .error(ColorDrawable(Color.RED))

    private var glideBackdropOptions: RequestOptions = RequestOptions()
        .fitCenter()
        // A placeholder image for when the network is slow
        .placeholder(R.drawable.ic_baseline_cloud_24)
        // Rounded corners are so lovely.
        .transform(RoundedCorners(20))
        // If we can't fetch, give the user an indication  maybe it should
        // say "network error"
        .error(ColorDrawable(Color.RED))
        .skipMemoryCache( true )
        .diskCacheStrategy(DiskCacheStrategy.NONE)

    fun fetch(
        urlPrimary: String,
        urlFallback: String,
        imageView: ImageView
    ) {
        Log.d("URL", urlPrimary)
        GlideApp.with(imageView.context).clear(imageView)

        GlideApp.with(imageView.context)
            .asBitmap()
            .load(urlPrimary)
            .apply(glideOptions)
            .error(
                GlideApp.with(imageView.context)
                    .asBitmap()
                    .load(urlFallback)
                    .apply(glideOptions)
                    .error(R.color.button_checked)
            )

            .into(imageView)
    }



    fun fetchBackdrop(
        urlPrimary: String,
        urlFallback: String,
        imageView: ImageView
    ) {
        Log.d("URL", urlPrimary)
        GlideApp.with(imageView.context).clear(imageView)

        GlideApp.with(imageView.context)
            .asBitmap()
            .load(urlPrimary)
            .apply(glideBackdropOptions)
            .error(
                GlideApp.with(imageView.context)
                    .asBitmap()
                    .load(urlFallback)
                    .apply(glideOptions)
                    .error(R.color.button_checked)
            )

            .into(imageView)
    }


}
