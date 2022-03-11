package com.bitpunchlab.android.topdf.job

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.database.PDFDatabase
import com.bitpunchlab.android.topdf.databinding.FragmentCreateJobBinding
import com.bitpunchlab.android.topdf.models.PDFJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.regex.Pattern

private const val TAG = "CreateJobFragment"

class CreateJobFragment : Fragment() {

    private var _binding : FragmentCreateJobBinding? = null
    private val binding  get() = _binding!!
    private var jobName: String = ""
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

        setHasOptionsMenu(true)
        _binding = FragmentCreateJobBinding.inflate(inflater, container, false)
        database = PDFDatabase.getInstance(context)
        coroutineScope = CoroutineScope(Dispatchers.IO)

        binding.submitButton.setOnClickListener {
            // get the job name
            jobName = binding.jobNameInput.text.toString()
            if (jobName != "" && isValidName(jobName)) {
                createJobAndSaveToDatabase()
            }
        }

        return binding.root
    }

    private fun isValidName(word: String): Boolean {

        val regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]")

        return !regex.matcher(word).find()
    }

    private fun createJobAndSaveToDatabase() {

        // create the job
        val job = PDFJob(jobName = jobName)
        Log.i(TAG, "creating job ${job.jobName}")
        // save to database
        coroutineScope.launch {
            database.jobDAO.insert(job)
            // notify user the job is created
            // navigate to job list fragment

        }
        findNavController().navigate(R.id.action_createJobFragment_to_jobListFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_job, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}