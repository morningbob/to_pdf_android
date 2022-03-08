package com.bitpunchlab.android.topdf.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "image_table")
@Parcelize
data class ImageItem (
    @PrimaryKey(autoGenerate = true) val imageId: Long = 0L,
    var imageUri: String,
    var jobId: Long,
    var rank: Int = 0
    ) : Parcelable


