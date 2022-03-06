package com.bitpunchlab.android.topdf.joblist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.base.GenericListener
import com.bitpunchlab.android.topdf.database.PDFDatabase
import com.bitpunchlab.android.topdf.databinding.FragmentJobListBinding
import com.bitpunchlab.android.topdf.models.PDFJob

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi

private const val TAG = "JobListFragment"

class JobListFragment : Fragment() {

    private var _binding: FragmentJobListBinding? = null
    private val binding get() = _binding!!
    //private lateinit var jobAdapter: JobAdapter
    private lateinit var jobAdapter: JobListAdapter
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

        jobAdapter = JobListAdapter(JobListListener { job ->
            jobsViewModel.onJobClicked(job)
            Log.i(TAG, "job clicked: ${job.jobName}")
        })

        binding.jobsRecycler.adapter = jobAdapter

        jobsViewModel.allJobs.observe(viewLifecycleOwner, Observer { jobList ->
            jobList?.let {
                jobAdapter.submitList(jobList)
                jobAdapter.notifyDataSetChanged()
            }
        })

        jobsViewModel.chosenJob.observe(viewLifecycleOwner, Observer { job ->
            job?.let {
                val bundle = Bundle()
                bundle.putParcelable("pdfJob", job)
                Log.i("navigate observer", job.toString())
                findNavController().navigate(
                    R.id.action_jobListFragment_to_MainFragment,
                    bundle
                )
                jobsViewModel.doneNavigating()
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}