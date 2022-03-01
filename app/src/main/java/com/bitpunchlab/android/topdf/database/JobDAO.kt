package com.bitpunchlab.android.topdf.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bitpunchlab.android.topdf.models.PDFJob

@Dao
interface JobDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(PDFJob: PDFJob)

    @Query("SELECT * FROM job_table")
    fun getAllJobs() : LiveData<List<PDFJob>>

    @Delete
    fun delete(PDFJob: PDFJob)
}