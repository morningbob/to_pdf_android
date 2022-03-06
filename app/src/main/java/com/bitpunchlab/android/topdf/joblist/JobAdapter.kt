package com.bitpunchlab.android.topdf.joblist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bitpunchlab.android.topdf.databinding.JobListItemBinding
import com.bitpunchlab.android.topdf.models.PDFJob

private const val TAG = "JobAdapter"

class JobAdapter(private val clickListener: JobListener)  :
    ListAdapter<PDFJob, JobViewHolder>(JobDiffCallback()){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JobViewHolder {
        return JobViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        when (holder) {
            is RecyclerView.ViewHolder -> {
                val job = getItem(position)
                Log.i(TAG, "onBindViewHolder job ${job.jobName}")
                holder.bind(clickListener, job)
            }
        }
    }

}

class JobViewHolder(val binding: JobListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(clickListener: JobListener, item: PDFJob) {
        binding.job = item
        //binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): JobViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = JobListItemBinding.inflate(layoutInflater, parent, false)
            return JobViewHolder(binding)
        }
    }
}

class JobListener(val clickListener: (PDFJob) -> Unit) {
    fun onClick(job: PDFJob) = clickListener(job)
}

class JobDiffCallback : DiffUtil.ItemCallback<PDFJob>() {
    override fun areItemsTheSame(oldItem: PDFJob, newItem: PDFJob): Boolean {
        return oldItem.jobId == newItem.jobId
    }

    override fun areContentsTheSame(oldItem: PDFJob, newItem: PDFJob): Boolean {
        return oldItem == newItem
    }
}