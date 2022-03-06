package com.bitpunchlab.android.topdf.images

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.database.PDFDatabase
import com.bitpunchlab.android.topdf.databinding.FragmentImageListBinding
import com.bitpunchlab.android.topdf.databinding.ImageListItemBinding
import com.bitpunchlab.android.topdf.joblist.JobsViewModel
import com.bitpunchlab.android.topdf.joblist.JobsViewModelFactory
import com.bitpunchlab.android.topdf.models.ImageItem
import kotlinx.coroutines.InternalCoroutinesApi

private const val TAG = "ImageListFragment"

class ImageListFragment : Fragment() {

    private var _binding: FragmentImageListBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var database: PDFDatabase
    private lateinit var imageAdapter: ImageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //setHasOptionsMenu(true)
        _binding = FragmentImageListBinding.inflate(inflater, container, false)
        database = PDFDatabase.getInstance(context)
        jobsViewModel = ViewModelProvider(requireActivity(), JobsViewModelFactory(database))
            .get(JobsViewModel::class.java)
        imageAdapter = ImageListAdapter()
        binding.imagesRecycler.adapter = imageAdapter

        // get all the images of the job
        jobsViewModel.allImagesOfJob =  database.imageDAO.getAllImagesOfJob(jobsViewModel.currentJobId!!)

        jobsViewModel.allImagesOfJob.observe(viewLifecycleOwner, Observer { imagelist ->
            imagelist?.let {
                Log.i(TAG,
                    "image list change observed, image list is not null, imagelist size ${imagelist.size}")
                imageAdapter.submitList(imagelist)
                imageAdapter.notifyDataSetChanged()
            }
        })

        return binding.root
    }


}