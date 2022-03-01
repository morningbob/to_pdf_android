package com.bitpunchlab.android.topdf.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.bitpunchlab.android.topdf.models.Job

@Dao
interface JobDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(job: Job)

    @Delete
    fun delete(job: Job)
}