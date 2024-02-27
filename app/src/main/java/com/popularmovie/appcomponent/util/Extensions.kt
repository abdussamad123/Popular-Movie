package com.popularmovie.appcomponent.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.popularmovie.BuildConfig
import com.popularmovie.R

fun ImageView.loadImage(isOriginalImage : Boolean, imageName:String, error_image: Int = R.drawable.ic_no_image) {
    var imageType = "w200/"

    if (isOriginalImage){
        imageType = "original/"
    }
        val url = "${BuildConfig.MOVIE_IMAGE_URL}$imageType$imageName"
        url.let {
             Glide.with(this)
                .load(it.trim())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(error_image)
                .placeholder(error_image)
                .into(this)
    }
      log("ImageUrlConstructed", url)
}

fun log(tag: String = "", message: String) {
    if (BuildConfig.DEBUG)
        Log.d(tag, "$tag: $message")
}

fun Activity.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun String.removeCommaFromLast(): String {
    return if (this.length>2){
        this.substring(0,this.length-1)
    }else ""
}
