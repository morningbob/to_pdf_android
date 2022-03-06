package com.bitpunchlab.android.topdf.images

import androidx.databinding.ViewDataBinding
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.base.GenericListener
import com.bitpunchlab.android.topdf.base.GenericRecyclerAdapter
import com.bitpunchlab.android.topdf.base.GenericRecyclerBindingInterface
import com.bitpunchlab.android.topdf.databinding.ImageListItemBinding
import com.bitpunchlab.android.topdf.models.ImageItem

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
}