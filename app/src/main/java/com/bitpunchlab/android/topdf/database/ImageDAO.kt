package com.bitpunchlab.android.topdf.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bitpunchlab.android.topdf.models.ImageItem

@Dao
interface ImageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: ImageItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg images: ImageItem)

    @Query("SELECT * FROM image_table WHERE :key == jobId")
    fun getAllImagesOfJob(key: Long) : LiveData<List<ImageItem>>

    @Delete
    fun delete(image: ImageItem)
}