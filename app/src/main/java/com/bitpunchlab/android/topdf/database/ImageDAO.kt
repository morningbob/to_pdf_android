package com.bitpunchlab.android.topdf.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.bitpunchlab.android.topdf.models.ImageItem

@Dao
interface ImageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: ImageItem)
}