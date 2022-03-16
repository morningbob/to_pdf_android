package com.bitpunchlab.android.topdf.processingtasks

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bitpunchlab.android.topdf.joblist.JobsViewModel
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CreatePDFTask(private val passedContext: Context) {

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var document: Document
    private lateinit var pdfWriter: PdfWriter
    private lateinit var jobsViewModel: JobsViewModel
    var done = MutableLiveData<Boolean>(false)
    var filePath = ""

    fun createDocumentCoroutine(filename: String, viewModel: JobsViewModel) {
        coroutineScope = CoroutineScope(Dispatchers.IO)
        jobsViewModel = viewModel
        coroutineScope.launch {
                createDocument(filename)
                convertToPDF()
        }
    }

    private fun createDocument(filename: String) {
        var dirPath = passedContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() +
            "/pdf"
        createDir("pdf")
        Log.i("location", dirPath)
        Log.i("dirPath", dirPath)
        Log.i("filename", filename)
        filePath = "$dirPath/$filename.pdf"
        // get the pdfWriter for the document
        done.postValue(false)
        val file = File(filePath)
        document = Document()
        pdfWriter = PdfWriter.getInstance(document, FileOutputStream(file))
        document.open()
    }

    private fun convertToPDF() {
        for (i in 1..(jobsViewModel.imageBitmaps.value!!.size)) {
            val bitmap = jobsViewModel.imageBitmaps.value!![i-1]
            document.newPage()
            try {
                val pdfOutputStream = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, pdfOutputStream)
                val image = Image.getInstance(pdfOutputStream.toByteArray())
                // 0 refers to identation
                val scaler = ((document.pageSize.width - document.leftMargin()
                        - document.rightMargin() - 0) / image.width) * 100
                image.scalePercent(scaler)
                image.alignment = Image.ALIGN_CENTER
                document.add(image)
                // end of images
                if (i == (jobsViewModel.imageBitmaps.value!!.size)) {
                    pdfOutputStream.flush()
                    document.close()
                    pdfWriter.close()
                    pdfOutputStream.close()
                    Log.i("convertToPDF", "document converted")
                    done.postValue(true)
                }
            } catch (e: IOException) {
                Log.i("create pdf task", "error getting image")
            }
        }
    }

    fun createDir(dirName: String) {
        var dirPath = passedContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() +
                "/$dirName"
        val fileDir = File(dirPath)
        if (!fileDir.exists()) {
            if (!fileDir.mkdir()) {
                Log.i("create $dirName directory", "there is problem creating dir")
                // here we may notify users the directory can't be created
            } else {
                Log.i("create $dirName dir", "success")
            }
        }
    }

}