package com.example.stickyheader

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.databinding.ItemContentBinding
import com.example.stickyheader.databinding.ItemHeaderAndContentBinding
import com.example.stickyheader.databinding.ItemHeaderBinding
import com.example.stickyheader.databinding.ItemStickyHeaderBinding

class MainAdapter : ListAdapter<ItemViewModel, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<ItemViewModel>() {
    override fun areItemsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean =
        oldItem.idViewModel == newItem.idViewModel

    override fun areContentsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean =
        oldItem.idViewModel == newItem.idViewModel
}), StickyHeaderDecoration.StickyHeaderCallBack {
    companion object {
        const val HEADER = 0
        const val CONTENT = 1
        const val HEADER_AND_CONTENT = 2
    }

    private var headerListener: ((String?) -> Unit)? = null

    private var positionListener: ((Int) -> Unit)? = null

    fun addHeaderListener(headerListener: ((String?) -> Unit)? = null, positionListener: ((Int) -> Unit)? = null) {
        this.headerListener = headerListener
        this.positionListener = positionListener
    }

    override fun getItemViewType(position: Int): Int = getItem(position).let { item ->
        positionListener?.invoke(position)
        when (item) {
            is HeaderItemViewModel -> {
                if (item.showHeader == true) {
                    headerListener?.invoke(item.title)
                }
                HEADER
            }
            is ContentItemViewModel -> CONTENT
            is HeaderContentItemViewModel -> HEADER_AND_CONTENT
            else -> throw RuntimeException("Not support item $item")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> HeaderViewHolder(parent)
            CONTENT -> ContentViewHolder(parent)
            HEADER_AND_CONTENT -> HeaderAndContentViewHolder(parent)
            else -> throw RuntimeException("Not support type=$viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is HeaderItemViewModel -> (holder as HeaderViewHolder).setItem(it)
                is ContentItemViewModel -> (holder as ContentViewHolder).setItem(it)
                is HeaderContentItemViewModel -> (holder as HeaderAndContentViewHolder).setItem(it)
            }
        }
    }

    inner class HeaderViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_header)) {
        private val binding = ItemHeaderBinding.bind(itemView)
        private var data: HeaderItemViewModel?=null

        fun setItem(data: HeaderItemViewModel?) {
            this.data = data
            binding.tvStickyHeader.text = data?.title
        }
    }

    class ContentViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_content)) {
        private val binding = ItemContentBinding.bind(itemView)
        fun setItem(data: ContentItemViewModel?) {
            binding.tvTitle.text = data?.name
            binding.tvAuthor.text = data?.author
            binding.tvCountry.text = data?.country
            binding.tvYear.text = data?.year
        }
    }

    class HeaderAndContentViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.item_header_and_content)) {
        private val binding = ItemHeaderAndContentBinding.bind(itemView)
        fun setItem(data: HeaderContentItemViewModel?) {
            binding.rcv.adapter = MainAdapter().apply {
                submitList(data?.list)
            }
        }
    }

    override fun onBindHeaderData(
        binding: ItemStickyHeaderBinding,
        header: HeaderItemViewModel?
    ) {
        binding.tvStickyHeader.text = header?.title
    }

    override fun getHeaderForCurrentPosition(position: Int): HeaderItemViewModel? =
        getItem(position)?.let {
            if (it is HeaderContentItemViewModel) HeaderItemViewModel(
                it.name,
                it.showHeader
            ) else null
        }

    override fun getHeaderForOldPosition(position: Int): HeaderItemViewModel? {
        if (position < 0) {
            return null
        }
        return getItem(position)?.let {
            if (it is HeaderContentItemViewModel) HeaderItemViewModel(
                it.name,
                it.showHeader
            ) else null
        }
    }

    override fun isHeader(itemPosition: Int): Boolean =
        if (itemPosition < 0) false else getItem(itemPosition)?.let {
            (it is HeaderContentItemViewModel && it.showHeader == true)
        } ?: false
}