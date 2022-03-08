package com.bitpunchlab.android.topdf.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.IOException

class AppUtils {

    companion object {
        fun getPhotoFromUri(imageUri: Uri, context: Context): Bitmap? {

            //val uriFilePath = Uri.fromFile(imageFile)
            var imageBitmap: Bitmap? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val imageCapturedSource: ImageDecoder.Source =
                    ImageDecoder.createSource(context?.contentResolver!!, imageUri)

                imageBitmap = ImageDecoder.decodeBitmap(imageCapturedSource)

            } else {
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(
                        context?.contentResolver,
                        imageUri
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return imageBitmap
        }
    }
}