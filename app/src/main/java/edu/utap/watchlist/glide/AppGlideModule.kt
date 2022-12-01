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
        .fitCenter()
        .placeholder(R.drawable.large_movie_poster)
        .transform(RoundedCorners(20))

        .error(R.drawable.large_movie_poster)

    private var glideBackdropOptions: RequestOptions = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.movie_back_wide)
        .transform(RoundedCorners(20))
        .skipMemoryCache( true )
        .diskCacheStrategy(DiskCacheStrategy.NONE)

    fun fetch(
        urlPrimary: String,
        urlFallback: String,
        imageView: ImageView
    ) {
        GlideApp.with(imageView.context).clear(imageView)

            GlideApp.with(imageView.context)
                .asBitmap()
                .load(urlPrimary)
                .apply(glideOptions)
                .error(R.drawable.large_movie_poster)
                .into(imageView)

    }



    fun fetchBackdrop(
        urlPrimary: String,
        urlFallback: String,
        imageView: ImageView
    ) {
        GlideApp.with(imageView.context).clear(imageView)
            GlideApp.with(imageView.context)
                .asBitmap()
                .load(urlPrimary)
                .apply(glideBackdropOptions)
                .error(R.drawable.movie_back_wide)
                .into(imageView)

    }


}
