package com.bitpunchlab.android.topdf.joblist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.database.PDFDatabase
import com.bitpunchlab.android.topdf.databinding.FragmentJobListBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi

private const val TAG = "JobListFragment"

class JobListFragment : Fragment() {

    private var _binding: FragmentJobListBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobAdapter: JobAdapter
    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var database: PDFDatabase
    private lateinit var coroutineScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobListBinding.inflate(inflater, container, false)
        database = PDFDatabase.getInstance(context)
        jobsViewModel = ViewModelProvider(requireActivity(), JobsViewModelFactory(database))
            .get(JobsViewModel::class.java)

        jobAdapter = JobAdapter(JobListener { job ->
            jobsViewModel.onJobClicked(job)
            Log.i(TAG, "job clicked: ${job.jobName}")
        })

        binding.jobsRecycler.adapter = jobAdapter

        jobsViewModel.allJobs.observe(viewLifecycleOwner, Observer { jobList ->
            jobList?.let {
                //Log.i(TAG, "job list changed observed")
                //Log.i(TAG, "job list $jobList")
                jobAdapter.submitList(jobList)
                jobAdapter.notifyDataSetChanged()
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}