package com.bitpunchlab.android.topdf.images

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("loadImage")
fun fetchImage(view: ImageView, src: String) {
    src.let {

        val uri = src.toUri()
        Log.i("bindingAdapter", uri.toString())

        Glide
            .with(view)
            .asBitmap()
            .load(uri)
            .into(view)
    }
}
