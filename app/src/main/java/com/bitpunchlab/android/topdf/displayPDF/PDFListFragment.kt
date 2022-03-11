package com.bitpunchlab.android.topdf.displayPDF

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.databinding.FragmentPdfListBinding
import com.bitpunchlab.android.topdf.models.PDFItem
import com.bitpunchlab.android.topdf.processingtasks.ReadDirTask

// this class hosts the list of pdf names.
// we need to read the myDocuments dir to get all the name
// and display to user
class PDFListFragment : Fragment() {

    private var _binding : FragmentPdfListBinding? = null
    private val binding get() = _binding!!
    private lateinit var pdfAdapter: PDFListAdapter
    private lateinit var pdfViewModel: PDFViewModel
    private lateinit var readDirTask: ReadDirTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPdfListBinding.inflate(inflater, container, false)
        pdfViewModel = ViewModelProvider(requireActivity()).get(PDFViewModel::class.java)
        pdfAdapter = PDFListAdapter(PDFListListener { pdf ->
            pdfViewModel.onPDFClicked(pdf)
        })

        binding.pdfRecycler.adapter = pdfAdapter
        readDirTask = ReadDirTask(requireContext())

        val pdfNameArray = readDirTask.listFiles()
        pdfViewModel.pdfList.value = pdfNameArray.map { name ->
            PDFItem(fileName = name.substring(0, name.length - 4))
        }

        pdfViewModel.pdfList.observe(viewLifecycleOwner, Observer { pdfList ->
            pdfList?.let {
                pdfAdapter.submitList(pdfList)
                pdfAdapter.notifyDataSetChanged()
            }
        })

        pdfViewModel.chosenPDF.observe(viewLifecycleOwner, Observer { pdf ->
            // navigate to display pdf fragment
            val bundle = Bundle()
            bundle.putString("filename", pdf.fileName)
            findNavController().navigate(R.id.action_PDFListFragment_to_displayPDFFragment, bundle)
        })

        return binding.root
    }


}