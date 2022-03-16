package com.bitpunchlab.android.topdf.joblist

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.topdf.database.PDFDatabase
import com.bitpunchlab.android.topdf.models.ImageItem
import com.bitpunchlab.android.topdf.models.PDFJob

class JobsViewModel(private val database: PDFDatabase) : ViewModel() {

    var _allJobs = database.jobDAO.getAllJobs()
    val allJobs get() = _allJobs

    var _chosenJob = MutableLiveData<PDFJob?>()
    val chosenJob : LiveData<PDFJob?>
        get() = _chosenJob

    lateinit var allImagesOfJob : LiveData<List<ImageItem>>

    var all = MutableLiveData<List<ImageItem>>()

    var currentJob: PDFJob? = null

    var imageBitmaps = MutableLiveData<List<Bitmap?>>()

    fun onJobClicked(job: PDFJob) {
        _chosenJob.value = job
    }

    fun doneNavigating() {
        _chosenJob.value = null
    }
}

class JobsViewModelFactory(private val database: PDFDatabase)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobsViewModel::class.java)) {
            return JobsViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}