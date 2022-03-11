package com.bitpunchlab.android.topdf.displayPDF

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.databinding.FragmentDisplayPDFBinding
import com.bitpunchlab.android.topdf.processingtasks.ReadPDFTask


class DisplayPDFFragment : Fragment() {

    private var _binding : FragmentDisplayPDFBinding? = null
    private val binding get() = _binding!!
    private var filePath = ""
    private var fileName = ""
    private lateinit var readPDFTask: ReadPDFTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayPDFBinding.inflate(inflater, container, false)

        fileName = requireArguments().getString("filename").toString()
        filePath = requireArguments().getString("location").toString()

        readPDFTask = ReadPDFTask(requireContext())

        readPDFTask.openPDFFileCoroutine(fileName)

        readPDFTask.resultPDFBitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            bitmap?.let {
                Log.i("readPDFTask", "loaded bitmap")
                binding.pdfImage.setImageBitmap(bitmap)
            }
        })

        binding.previousPageButton.setOnClickListener {
            if (readPDFTask.currentPageIndex > 0) {
                Log.i("previous button", "pageCount ${readPDFTask.pageCount} currentPage ${readPDFTask.currentPageIndex}")
                readPDFTask.currentPageIndex -= 1
                readPDFTask.prepareCurrentPage()
            } else {
                Log.i("previous button", "already in front page")
            }
        }

        binding.nextPageButton.setOnClickListener {
            if (readPDFTask.currentPageIndex < readPDFTask.pageCount - 1) {
                Log.i("next button", "pageCount ${readPDFTask.pageCount} currentPage ${readPDFTask.currentPageIndex}")
                readPDFTask.currentPageIndex += 1
                readPDFTask.prepareCurrentPage()
            } else {
                Log.i("next button", "already at the end")
            }
        }

        return binding.root
    }

}