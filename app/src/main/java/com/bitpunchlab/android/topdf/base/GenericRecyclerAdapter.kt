package com.bitpunchlab.android.topdf.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class GenericRecyclerAdapter<T: Any>(
    compareItems: (old: T, new: T) -> Boolean,
    compareContents: (old: T, new: T) -> Boolean,
    private val bindingInter: GenericRecyclerBindingInterface<T>,
    @LayoutRes val layoutID: Int) :
    ListAdapter<T, GenericViewHolder>(GenericDiffCallback(compareItems, compareContents)) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
        return GenericViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, bindingInter)
    }
}

class GenericViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun <T: Any> bind(
        item: T,
        bindingInterface: GenericRecyclerBindingInterface<T>)
        = bindingInterface.bindData(item, view)
}

class GenericDiffCallback<K>(
    private val compareItems: (old: K, new: K) -> Boolean,
    private val compareContents: (old: K, new: K) -> Boolean
) : DiffUtil.ItemCallback<K>() {
    override fun areItemsTheSame(old: K, new: K): Boolean
        = compareItems(old, new)

    override fun areContentsTheSame(old: K, new: K): Boolean
        = compareContents(old, new)
}

interface GenericRecyclerBindingInterface<T: Any> {
    fun bindData(item: T, view: View)
}



