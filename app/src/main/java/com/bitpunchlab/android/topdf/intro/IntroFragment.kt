package com.bitpunchlab.android.topdf.intro

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.databinding.FragmentIntroBinding

private const val TAG = "IntroFragment"

// we check the camera permission here, if the permission is not granted,
// we request it
class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null

    private val binding get() = _binding!!

    private var cameraEnabled = MutableLiveData<Boolean>(false)
/*
    private val requestCameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "camera permission granted")
                cameraEnabled.value = true
            } else {
                Log.i(TAG, "camera permission not granted")
                // show an alert
            }
        }
*/

    private val requestCameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                Log.i(TAG, "camera permission granted")
                findNavController().navigate(R.id.action_IntroFragment_to_createJobFragment)
            } else {
                Log.i(TAG, "camera permission not granted")
            }
        }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentIntroBinding.inflate(inflater, container, false)

        binding.createJobButton.setOnClickListener {
            if (!checkCameraPermission()) {
                //val enableCameraAccessIntent = Intent(Manifest.permission.CAMERA)
                requestCameraPermissionResult.launch(Manifest.permission.CAMERA)
            } else {
                // navigate to create a job fragment
                findNavController().navigate(R.id.action_IntroFragment_to_createJobFragment)
            }
        }

        binding.displayJobButton.setOnClickListener {
            // navigate to display job list fragment if there is a job
        }

        binding.displayPDFButton.setOnClickListener {
            // navigate to pdf list fragment if there is pdf created
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkCameraPermission() : Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
}