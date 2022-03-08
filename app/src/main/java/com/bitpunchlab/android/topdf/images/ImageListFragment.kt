package com.bitpunchlab.android.topdf.images

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.database.PDFDatabase
import com.bitpunchlab.android.topdf.databinding.FragmentImageListBinding
import com.bitpunchlab.android.topdf.databinding.ImageListItemBinding
import com.bitpunchlab.android.topdf.joblist.JobsViewModel
import com.bitpunchlab.android.topdf.joblist.JobsViewModelFactory
import com.bitpunchlab.android.topdf.models.ImageItem
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException

private const val TAG = "ImageListFragment"

class ImageListFragment : Fragment() {

    private var _binding: FragmentImageListBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var database: PDFDatabase
    private lateinit var imageAdapter: ImageListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var itemTouchHelperCallback: ImageItemTouchHelperCallback
    private lateinit var coroutineScope: CoroutineScope
    //private var imageDeletedPosition: Int? = null
    //private var imageDeleted = MutableLiveData<ImageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentImageListBinding.inflate(inflater, container, false)
        coroutineScope = CoroutineScope(Dispatchers.IO)
        database = PDFDatabase.getInstance(context)
        jobsViewModel = ViewModelProvider(requireActivity(), JobsViewModelFactory(database))
            .get(JobsViewModel::class.java)
        imageAdapter = ImageListAdapter()
        binding.imagesRecycler.layoutManager = WrapContentLinearLayoutManager()
        binding.imagesRecycler.adapter = imageAdapter

        setupItemTouchHelper()

        // show snackbar when there is deletion detected by itemTouchHelper
        itemTouchHelperCallback.imageDeleted.observe(viewLifecycleOwner, Observer { image ->
            image?.let {
                showUndoSnackbar()
            }
        })

        // get all the images of the job
        jobsViewModel.allImagesOfJob =  database.imageDAO.getAllImagesOfJob(jobsViewModel.currentJob!!.jobId)

        jobsViewModel.allImagesOfJob.observe(viewLifecycleOwner, Observer { imagelist ->
            imagelist?.let {
                //Log.i(TAG,
                //    "image list change observed, image list is not null, imagelist size ${imagelist.size}")
                imageAdapter.submitList(imagelist)
                imageAdapter.notifyDataSetChanged()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_display_images, menu)
        //menu.findItem(R.id.MainFragment).isVisible = false
        //menu.findItem(R.id.displayImagesFragment).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        // update the images rank in the recyclerview in database
        // check if the rank change, only update the one changed
        val imagesList = imageAdapter.currentList.toMutableList()
        val imagesToBeUpdated = imagesList.mapIndexed { index, image ->
            if (index != image.rank) {
                // update the rank
                image.rank = index
                image
            }
        }
        // update the images
        // this deals with the change in rank only
        coroutineScope.launch {
            database.imageDAO.insertAll(*imagesList.toTypedArray())
            Log.i("onPause", "inserted all")
        }
        Log.i("onPause", imagesToBeUpdated.toString())
    }

    private fun setupItemTouchHelper() {
        itemTouchHelperCallback = ImageItemTouchHelperCallback(imageAdapter, requireContext())
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.imagesRecycler)
    }

    private fun showUndoSnackbar() {
        // here, we delete the image, we'll save it again if undo is pressed
        coroutineScope.launch {
            database.imageDAO.delete(itemTouchHelperCallback.imageDeleted.value!!)
            Log.i("snackbar", "deleted")
        }
        val imageItemView = binding.imageListLayout
        val snack = Snackbar.make(imageItemView, "Undo Delete?", Snackbar.LENGTH_LONG)
        snack.setAction("UNDO", SnackUndoListener())
        snack.show()
    }

    inner class SnackUndoListener : View.OnClickListener {
        override fun onClick(p0: View?) {
            // undo the deletion
            imageAdapter.addImage(itemTouchHelperCallback.imagePosition!!,
                itemTouchHelperCallback.imageDeleted.value!!)
            // save it to database
            coroutineScope.launch {
                database.imageDAO.insert(itemTouchHelperCallback.imageDeleted.value!!)
                Log.i("snackbar", "reinserted image")
            }
        }
    }

    // This class wrapped linear layout manager to skip the exception we got
    // when added or removed item but recyclerview operate in different thread
    // and doesn't know the change in list size.
    inner class WrapContentLinearLayoutManager : LinearLayoutManager(context) {
        override fun onLayoutChildren(
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
        ) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.i(TAG, "recyclerview executed in different threads")
            }
        }
    }
}



