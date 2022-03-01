package com.bitpunchlab.android.topdf.job

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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
        //val regex = Pattern.compile("[a-zA-Z0-9]*")
        val regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]")
        //return .matches("[a-zA-Z0-9.? ]*")
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
}