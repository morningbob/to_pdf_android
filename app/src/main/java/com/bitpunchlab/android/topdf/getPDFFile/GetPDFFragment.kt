package com.bitpunchlab.android.topdf.getPDFFile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.databinding.FragmentGetPdfBinding
import java.io.File

private const val TAG = "GetPDFFragment"

class GetPDFFragment : Fragment() {

    private var _binding: FragmentGetPdfBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGetPdfBinding.inflate(inflater, container, false)

        openPDFDir()

        return binding.root
    }

    private fun openPDFDir() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        val fileDir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() +
                "/pdf")
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "com.bitpunchlab.android.topdf.fileprovider",
            fileDir
        )
        Log.i(TAG, "open dir: uri: ${uri.toString()}")
        intent.setDataAndType(uri, "*/*")
        startActivity(Intent.createChooser(intent, getString(R.string.opening_pdf_title)))
    }
}