package com.bitpunchlab.android.topdf.displayPDF

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitpunchlab.android.topdf.models.PDFItem

class PDFViewModel : ViewModel() {

    var _pdfList = MutableLiveData<List<PDFItem>>()
    val pdfList get() = _pdfList

    var _chosenPDF = MutableLiveData<PDFItem?>()
    val chosenPDF get() = _chosenPDF

    fun onPDFClicked(pdf: PDFItem) {
        _chosenPDF.value = pdf
    }

    fun doneNavigating() {
        _chosenPDF.value = null
    }
}