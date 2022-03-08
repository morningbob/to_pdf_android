package com.bitpunchlab.android.topdf.images

import android.view.View
import androidx.databinding.ViewDataBinding
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.base.GenericListener
import com.bitpunchlab.android.topdf.base.GenericRecyclerAdapter
import com.bitpunchlab.android.topdf.base.GenericRecyclerBindingInterface
import com.bitpunchlab.android.topdf.databinding.ImageListItemBinding
import com.bitpunchlab.android.topdf.models.ImageItem
import com.google.android.material.snackbar.Snackbar
import java.util.*

class ImageListAdapter : GenericRecyclerAdapter<ImageItem>(
    layoutID = R.layout.image_list_item,
    compareItems = { old, new ->  old.jobId == new.jobId },
    compareContents = { old, new ->  old == new },
    onClickListener = null,
    bindingInter = object : GenericRecyclerBindingInterface<ImageItem> {
        override fun bindData(
            item: ImageItem,
            binding: ViewDataBinding,
            onClickListener: GenericListener<ImageItem>?
        ) {
            (binding as ImageListItemBinding).imageItem = item
        }

    }
) {

    fun deleteImage(target: Int) : ImageItem {
        val imageList = currentList.toMutableList()
        val image = imageList[target]
        imageList.removeAt(target)
        notifyItemRemoved(target)
        //notifyItemRangeRemoved(target, 1)
        submitList(imageList)
        return image
    }

    fun addImage(target: Int, image: ImageItem) {
        val imageList = currentList.toMutableList()
        imageList.add(target, image)
        notifyItemInserted(target)
        //notifyItemRangeInserted(target, 1)
        submitList(imageList)
    }

    fun moveImage(from: Int, to: Int) {
        val imageList = currentList.toMutableList()
        Collections.swap(imageList, from, to)
        submitList(imageList)
        notifyDataSetChanged()
    }

}

