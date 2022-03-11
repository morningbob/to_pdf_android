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
import androidx.lifecycle.MutableLiveData
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
import java.util.regex.Pattern


class ProcessingFragment : Fragment() {

    private var _binding : FragmentProcessingBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentJob: PDFJob
    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var database: PDFDatabase
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var createPDFTask: CreatePDFTask
    private lateinit var imageItemToBeProcessed: LiveData<List<ImageItem>>
    private var pdfName = MutableLiveData<String>()


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
                if (pdfName.value != null) {
                    Log.i("createTask", "started")
                    // start to convert
                    createPDFTask.createDocumentCoroutine(pdfName.value!!, jobsViewModel)
                }
            }
        })

        createPDFTask.done.observe(viewLifecycleOwner, Observer { value ->
            if (value) {
                // alert user, the process completed
                doneAlert()
            }
        })

        binding.submitButton.setOnClickListener {
            pdfName.value = getPDFName()
        }

        // here I decided to do the processing before user enter the pdf name.
        // until all the image bitmaps are loaded in JobsViewModel
        // then, I see if I got a valid pdfName
        // if so, and the bitmaps are loaded, we start the processing
        // if not, we wait until the bitmaps are loaded,
        // then start processing in the imageBitmap observable
        // so, I got 2 places to start to process
        pdfName.observe(viewLifecycleOwner, Observer { name ->
            name?.let {
                if (jobsViewModel.imageBitmaps.value != null) {
                    createPDFTask.createDocumentCoroutine(pdfName.value!!, jobsViewModel)
                }
            }
        })


        return binding.root
    }

    private fun getPDFName() : String? {
        val name = binding.jobNameEditText.text.toString()
        val nameValidity = checkIfNameValid(name)
        if (nameValidity) {
            return name
        }
        return null
    }

    private fun checkIfNameValid(pdfName: String) : Boolean {
        val regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]")
        return !regex.matcher(pdfName).find()
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

    private fun doneAlert() {
        val doneAlert = AlertDialog.Builder(requireContext())

        doneAlert.setTitle("PDF Document Created")
        doneAlert.setCancelable(false)
        doneAlert.setMessage("The PDF ${pdfName.value} was created.")

        doneAlert.setPositiveButton("OK",
            DialogInterface.OnClickListener() { dialog, button ->
                val bundle = Bundle()
                bundle.putString("filename", pdfName.value)
                bundle.putString("location", createPDFTask.filePath)
                Log.i("createTask", "done")
                findNavController().navigate(R.id.action_processingFragment_to_displayPDFFragment, bundle)
            })

        doneAlert.show()
    }

}
