package com.bitpunchlab.android.topdf.processingPDF

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.database.PDFDatabase
import com.bitpunchlab.android.topdf.databinding.FragmentProcessingBinding
import com.bitpunchlab.android.topdf.joblist.JobsViewModel
import com.bitpunchlab.android.topdf.joblist.JobsViewModelFactory
import com.bitpunchlab.android.topdf.models.ImageItem
import com.bitpunchlab.android.topdf.models.PDFJob
import com.bitpunchlab.android.topdf.processingtasks.CreatePDFTask
import com.bitpunchlab.android.topdf.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


class ProcessingFragment : Fragment() {

    private var _binding : FragmentProcessingBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentJob: PDFJob
    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var database: PDFDatabase
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var createPDFTask: CreatePDFTask
    private lateinit var imageItemToBeProcessed: LiveData<List<ImageItem>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProcessingBinding.inflate(inflater, container, false)
        database = PDFDatabase.getInstance(requireContext())
        jobsViewModel = ViewModelProvider(requireActivity(), JobsViewModelFactory(database))
            .get(JobsViewModel::class.java)
        coroutineScope = CoroutineScope(Dispatchers.IO)
        val job = requireArguments().getParcelable<PDFJob>("pdfJob")
        if (job != null) {
            currentJob = job
        } else {
            currentJob = jobsViewModel.currentJob!!
        }
        // we first load the imageItems from the job
        imageItemToBeProcessed = database.imageDAO.getAllImagesOfJob(currentJob.jobId)
        createPDFTask = CreatePDFTask(requireContext())

        // we need to wait for the imageItems to be fetched from database
        imageItemToBeProcessed.observe(viewLifecycleOwner, Observer { imageItems ->
            imageItems?.let {
                Log.i("imageToBeProcessed", "loaded")
                if (imageItems.isNotEmpty()) {
                    // get bitmaps from imageItems uri
                    // here we check if there is image first
                    prepareImageBitmapsForPDF()
                } else {
                    noImageAlert()
                }
            }
        })

        // we need to wait for the bitmaps to be fetched from the database
        jobsViewModel.imageBitmaps.observe(viewLifecycleOwner, androidx.lifecycle.Observer { bitmaps ->
            bitmaps?.let {
                if (imageItemToBeProcessed.value!!.size == bitmaps.size) {
                    Log.i("createTask", "started")
                    // start to convert
                    createPDFTask.createDocumentCoroutine("XXX", jobsViewModel)
                }
            }
        })

        createPDFTask.done.observe(viewLifecycleOwner, Observer { value ->
            if (value) {
                val bundle = Bundle()
                bundle.putString("filename", "XXX")
                bundle.putString("location", createPDFTask.filePath)
                Log.i("createTask", "done")
                findNavController().navigate(R.id.action_processingFragment_to_displayPDFFragment, bundle)
            }
        })

        return binding.root
    }



    // need to handle if can't get the image from uri
    private fun prepareImageBitmapsForPDF() {
        jobsViewModel.imageBitmaps.value = imageItemToBeProcessed.value?.map { imageItem ->
            AppUtils.getPhotoFromUri(imageItem.imageUri.toUri(), requireContext())
        }

    }

    private fun noImageAlert() {
        val imageAlert = AlertDialog.Builder(requireContext())

        imageAlert.setTitle("Create PDF document")
        imageAlert.setMessage("There is no image to create the pdf document.")

        imageAlert.setPositiveButton("OK",
            DialogInterface.OnClickListener() { dialog, button ->
            })

        imageAlert.show()
    }

}
/*
    jobsViewModel.allImagesOfJob.observe(requireActivity(), Observer { images ->
            images?.let {
                if (images.isNotEmpty()) {
                    // get bitmaps from imageItems uri
                    // here we check if there is image first
                    if (jobsViewModel.allImagesOfJob.value != null &&
                        jobsViewModel.allImagesOfJob.value!!.isNotEmpty()) {
                        prepareImageBitmapsForPDF()
                    } else {
                        // show alert that no image to create pdf
                        noImageAlert()
                    }
                }
            }
        })

    private fun retrieveAllImagesOfJob() {
        coroutineScope.launch {
            jobsViewModel.allImagesOfJob = database.imageDAO.getAllImagesOfJob(currentJob.jobId)
            Log.i("retrieved images", jobsViewModel.allImagesOfJob.value!!.size.toString())

        }
    }


 */