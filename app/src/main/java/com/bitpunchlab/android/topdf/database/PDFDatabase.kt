package com.bitpunchlab.android.topdf.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bitpunchlab.android.topdf.models.ImageItem
import com.bitpunchlab.android.topdf.models.Job
import kotlinx.coroutines.InternalCoroutinesApi

@Database(entities = [Job::class, ImageItem::class], version = 1, exportSchema = false)

abstract class PDFDatabase : RoomDatabase() {

    abstract val imageDAO: ImageDAO
    abstract val jobDAO: JobDAO

    companion object {

        @Volatile
        private var INSTANCE: PDFDatabase? = null


        @InternalCoroutinesApi
        fun getInstance(context: Context?): PDFDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context!!.applicationContext,
                        PDFDatabase::class.java,
                        "pdf_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }

    }
}