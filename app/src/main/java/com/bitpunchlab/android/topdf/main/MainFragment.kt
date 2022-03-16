package com.bitpunchlab.android.topdf.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.database.PDFDatabase
import com.bitpunchlab.android.topdf.databinding.FragmentMainBinding
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MainFragment"

// all the main functions of the app are mostly here
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private var imageFileSaved: File? = null
    private var imageBitmap = MutableLiveData<Bitmap?>()
    private lateinit var currentJob: PDFJob
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var database: PDFDatabase
    private lateinit var jobsViewModel: JobsViewModel


    private val requestPhotoCaptureResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "photo captured")
                val uri = Uri.fromFile(imageFileSaved)
                imageBitmap.value = AppUtils.getPhotoFromUri(uri, requireContext())
                createImageItemAndInsert(imageBitmap.value, Uri.fromFile(imageFileSaved))
            } else {
                Log.i(TAG, "failed to capture photo")

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        database = PDFDatabase.getInstance(context)
        jobsViewModel = ViewModelProvider(requireActivity(), JobsViewModelFactory(database))
            .get(JobsViewModel::class.java)
        val job = requireArguments().getParcelable<PDFJob>("pdfJob") as PDFJob
        if (job != null) {
            currentJob = job
        } else {
            currentJob = jobsViewModel.currentJob!!
        }
        setHasOptionsMenu(true)
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        coroutineScope = CoroutineScope(Dispatchers.IO)

        binding.jobName.text = currentJob.jobName

        binding.captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.createJobButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_createJobFragment)
        }

        binding.displayImagesButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_imageListFragment)
        }

        binding.displayJobsButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_jobListFragment)
        }

        binding.createPdfButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("pdfJob", currentJob)
            findNavController().navigate(R.id.action_MainFragment_to_processingFragment, bundle)
        }

        binding.displayPdfsButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_PDFListFragment)
        }

        imageBitmap.observe(viewLifecycleOwner, androidx.lifecycle.Observer { image ->
            image?.let {
                binding.mainPlaceholderImage.setImageBitmap(image)
            }
        })
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    // capture photo function utils
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context?.packageManager!!)?.also {
                // Create the File where the photo should go
                imageFileSaved =
                    try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }
                // Continue only if the File was successfully created
                imageFileSaved?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.bitpunchlab.android.topdf.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    requestPhotoCaptureResult.launch(takePictureIntent)
                }
            }
        }
    }

    // capture photo function utils
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.i(
            "creating the file name",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        )
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    // capture photo function utils
    private fun createImageItemAndInsert(bitmap: Bitmap?, uri: Uri) {
        if (bitmap != null) {
            val imageItem = ImageItem(imageUri = uri.toString(), jobId = currentJob.jobId)
            // save imageItem to database
            coroutineScope.launch {
                database.imageDAO.insert(imageItem)
                Log.i(TAG, "image inserted")
            }
        } else {
            Log.i(TAG, "image is null")
        }
    }

}