package com.bitpunchlab.android.topdf.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "job_table")
@Parcelize
data class Job constructor(
    @PrimaryKey(autoGenerate = true) var jobId: Long = 0L,
    var jobName: String,
    var numImage: Int = 0

) : Parcelable
