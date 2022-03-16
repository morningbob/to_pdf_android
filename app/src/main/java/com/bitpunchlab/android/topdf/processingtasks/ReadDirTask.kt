package com.bitpunchlab.android.topdf.processingtasks

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

class ReadDirTask(private val passedContext: Context) {

    fun listFiles() : ArrayList<String> {
        val pdfPattern = ".pdf"
        val path = passedContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() +
                "/pdf"
        val directory = File(path)
        val filesArray = directory.listFiles()
        val pdfFileList = ArrayList<String>()

        filesArray.map { file ->
            if (file.name.endsWith(pdfPattern)) {
                Log.i("listing file", "name ${file.name}")
                pdfFileList.add(file.name)
            }
        }
        return pdfFileList
    }
}