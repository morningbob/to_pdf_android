package com.bitpunchlab.android.topdf.intro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.topdf.R

private const val TAG = "PermissionFragment"

// we check the camera permission here, if the permission is not granted,
// we request it
class PermissionFragment : Fragment() {

    private var cameraEnabled = MutableLiveData<Boolean>(false)

    private val requestCameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                Log.i(TAG, "camera permission granted")
                getWritePermission()
                //findNavController().navigate(R.id.action_permissionFragment_to_IntroFragment)
            } else {
                Log.i(TAG, "camera permission not granted")
            }
        }

    private val requestWritePermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                Log.i(TAG, "write permission granted")
                findNavController().navigate(R.id.action_permissionFragment_to_IntroFragment)
            } else {
                Log.i(TAG, "write permission not granted")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!checkCameraPermission()) {
            requestCameraPermissionResult.launch(Manifest.permission.CAMERA)
        } else {
            cameraEnabled.value = true
            getWritePermission()
        }

        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    private fun checkCameraPermission() : Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkWritePermission() : Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun getWritePermission() {
        if (!checkWritePermission()) {
            requestWritePermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            findNavController().navigate(R.id.action_permissionFragment_to_IntroFragment)
        }
    }
}