package com.bitpunchlab.android.topdf.intro

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.databinding.FragmentIntroBinding

private const val TAG = "IntroFragment"


class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentIntroBinding.inflate(inflater, container, false)

        binding.createJobButton.setOnClickListener {
            findNavController().navigate(R.id.action_IntroFragment_to_createJobFragment)
        }

        binding.displayJobButton.setOnClickListener {
            // navigate to display job list fragment if there is a job
            findNavController().navigate(R.id.action_IntroFragment_to_jobListFragment)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_intro, menu)
        //menu.findItem(R.id.MainFragment).isVisible = false
        //menu.findItem(R.id.displayImagesFragment).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}