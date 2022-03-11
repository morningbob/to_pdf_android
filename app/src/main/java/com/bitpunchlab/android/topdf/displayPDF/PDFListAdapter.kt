package com.bitpunchlab.android.topdf.displayPDF

import androidx.databinding.ViewDataBinding
import com.bitpunchlab.android.topdf.R
import com.bitpunchlab.android.topdf.base.GenericListener
import com.bitpunchlab.android.topdf.base.GenericRecyclerAdapter
import com.bitpunchlab.android.topdf.base.GenericRecyclerBindingInterface
import com.bitpunchlab.android.topdf.databinding.PdfListItemBinding
import com.bitpunchlab.android.topdf.models.PDFItem

class PDFListAdapter(private val clickListener: PDFListListener) : GenericRecyclerAdapter<PDFItem>(
    layoutID = R.layout.pdf_list_item,
    compareContents = { old, new -> old == new },
    compareItems = { old, new -> old.fileName == new.fileName },
    onClickListener = clickListener,
    bindingInter = object : GenericRecyclerBindingInterface<PDFItem> {
        override fun bindData(
            item: PDFItem,
            binding: ViewDataBinding,
            onClickListener: GenericListener<PDFItem>?
        ) {
            (binding as PdfListItemBinding).pdfFile = item
            binding.clickListener = clickListener
        }

    }
) {
}

class PDFListListener(clickListener: (PDFItem) -> Unit) : GenericListener<PDFItem>(clickListener){
    fun onClick(item: PDFItem) = clickListener(item)
}