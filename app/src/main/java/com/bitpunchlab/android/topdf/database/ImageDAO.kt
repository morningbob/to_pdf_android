package com.bitpunchlab.android.topdf.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bitpunchlab.android.topdf.models.ImageItem

@Dao
interface ImageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: ImageItem)

    @Query("SELECT * FROM image_table WHERE :key == jobId")
    fun getAllImagesOfJob(key: Long) : LiveData<List<ImageItem>>
}