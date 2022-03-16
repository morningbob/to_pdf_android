package com.bitpunchlab.android.topdf.processingPDF

import android.content.DialogInterface
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.lifecycle.*
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
            Log.i("job", "is not null")
        } else {
            currentJob = jobsViewModel.currentJob!!
            Log.i("job", "is null")
        }

        Log.i("creating pdf", "yes")
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
        jobsViewModel.imageBitmaps.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { bitmaps ->
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
                // reset current job to null, there is no job anymore, processing fragment won't
                    // be triggered again.
                jobsViewModel.currentJob = null
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
                // info user the app is processing the document.
                binding.processingMessage.text = "Creating the ${name}.pdf document...  Please wait."
                binding.progressBar.visibility = View.VISIBLE
                binding.jobNameEditText.visibility = View.GONE
                binding.submitButton.visibility = View.GONE
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

        imageAlert.setTitle(getString(R.string.create_pdf_alert_title))
        imageAlert.setMessage(getString(R.string.create_pdf_alert_no_image))

        imageAlert.setPositiveButton("OK",
            DialogInterface.OnClickListener() { dialog, button ->
            })

        imageAlert.show()
    }

    private fun doneAlert() {
        val doneAlert = AlertDialog.Builder(requireContext())

        doneAlert.setTitle(getString(R.string.create_pdf_done_alert_title))
        doneAlert.setCancelable(false)
        doneAlert.setMessage("The PDF ${pdfName.value} was created.\nIt is located at /storage/emulated/0/Android/data/com.bitpunchlab.android.topdf/files/Documents/pdf/")

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

    override fun onResume() {
        super.onResume()

    }
}

/*
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}
*/