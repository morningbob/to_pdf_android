package com.bitpunchlab.android.topdf.processingtasks

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ReadPDFTask(private val passedContext: Context) {

    var dirPath = ""
    private lateinit var coroutineScope: CoroutineScope
    var fileName = ""
    private lateinit var pdfFile: File
    var currentPageIndex = 0
    var pageCount = 0
    private lateinit var pdfRenderer: PdfRenderer
    var resultPDFBitmap = MutableLiveData<Bitmap?>()

    private fun openPDFFile() {
        dirPath = passedContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() +
                "/pdf"
        val filePath = "$dirPath/$fileName.pdf"
        pdfFile = File(filePath)
        val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)
        pageCount = pdfRenderer.pageCount

        Log.i("openPDFFile", "reading")
    }

    fun openPDFFileCoroutine(filename: String) {
        fileName = filename
        Log.i("filename received", filename)
        currentPageIndex = 0  // reset
        coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            openPDFFile()
            prepareCurrentPage()
        }
    }

    fun prepareCurrentPage() {
        if (currentPageIndex < pageCount) {
            val currentPage = pdfRenderer.openPage(currentPageIndex)
            Log.i("prepareCurPage", "cur page $currentPageIndex")
            val pdfBitmap =
                Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
            currentPage.render(pdfBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            currentPage.close()
            resultPDFBitmap.postValue(pdfBitmap)
        } else {
            Log.i("prepareCurrentPage", "end of pdf.")
        }

    }

}