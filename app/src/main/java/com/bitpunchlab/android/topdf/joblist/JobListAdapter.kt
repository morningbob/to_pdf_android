package com.bitpunchlab.android.topdf.joblist

import androidx.databinding.ViewDataBinding
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.base.GenericListener
import com.bitpunchlab.android.topdf.base.GenericRecyclerAdapter
import com.bitpunchlab.android.topdf.base.GenericRecyclerBindingInterface
import com.bitpunchlab.android.topdf.databinding.JobListItemBinding
import com.bitpunchlab.android.topdf.models.PDFJob

class JobListAdapter(clickListener: JobListListener) : GenericRecyclerAdapter<PDFJob>(
        layoutID = R.layout.job_list_item,
        compareItems = { old, new ->  old.jobId == new.jobId },
        compareContents = { old, new ->  old == new },
        onClickListener = clickListener,
        bindingInter = object : GenericRecyclerBindingInterface<PDFJob> {
            override fun bindData(
                item: PDFJob,
                binding: ViewDataBinding,
                onClickListener: GenericListener<PDFJob>
            ) {
                (binding as JobListItemBinding).clickListener = clickListener
                binding.job = item
            }
        },
     ) {

}

class JobListListener(clickListener: (PDFJob) -> Unit) : GenericListener<PDFJob>(clickListener){
    fun onClick(job: PDFJob) = clickListener(job)
}




