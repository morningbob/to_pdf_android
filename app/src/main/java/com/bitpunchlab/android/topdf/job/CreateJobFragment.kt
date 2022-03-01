package com.bitpunchlab.android.topdf.job

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.databinding.FragmentCreateJobBinding

class CreateJobFragment : Fragment() {

    private var _binding : FragmentCreateJobBinding? = null
    private val binding  get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCreateJobBinding.inflate(inflater, container, false)



        return binding!!.root
    }


}