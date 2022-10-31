package com.example.stickyheader

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note.NOTE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.databinding.ActivityMainBinding
import com.example.stickyheader.databinding.ItemContentBinding
import com.example.stickyheader.databinding.ItemHeaderBinding
import com.example.stickyheader.databinding.ItemStickyHeaderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = MainAdapter()
        binding.rcv.adapter = adapter
        binding.rcv.addItemDecoration(
            StickyHeaderDecoration(adapter, binding.root)
        )
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.list_divider)
            ?.let { dividerItemDecoration.setDrawable(it) }
        binding.rcv.addItemDecoration(dividerItemDecoration)
        lifecycleScope.launch(Dispatchers.IO) {
            val data = getItems(this@MainActivity).flatMap {
                it.createItems(it.year < 0)
            }
            withContext(Dispatchers.Main) {
                adapter.submitList(data)
            }
        }
    }

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
        }

        override fun getItemViewType(position: Int): Int = getItem(position).let { item ->
            when (item) {
                is HeaderItemViewModel -> HEADER
                is ContentItemViewModel -> CONTENT
                else -> throw RuntimeException("Not support item $item")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                HEADER -> HeaderViewHolder(parent)
                CONTENT -> ContentViewHolder(parent)
                else -> throw RuntimeException("Not support type=$viewType")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            getItem(position)?.let {
                when (it) {
                    is HeaderItemViewModel -> (holder as HeaderViewHolder).setItem(it)
                    is ContentItemViewModel -> (holder as ContentViewHolder).setItem(it)
                }
            }
        }

        class HeaderViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.item_header)) {
            private val binding = ItemHeaderBinding.bind(itemView)
            fun setItem(data: HeaderItemViewModel?) {
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

        override fun onBindHeaderData(binding: ItemStickyHeaderBinding, header: HeaderItemViewModel?) {
            binding.tvStickyHeader.text = header?.title
        }

        override fun getHeaderForCurrentPosition(position: Int): HeaderItemViewModel? =
            getItem(position)?.let { if (it is HeaderItemViewModel) it else null }

        override fun getHeaderForOldPosition(position: Int): HeaderItemViewModel? {
           return currentList.filterIsInstance<HeaderItemViewModel>().find { it.showHeader == true }
        }

        override fun isHeader(itemPosition: Int): Boolean = getItem(itemPosition).let {
            it is HeaderItemViewModel && it.showHeader == true
        }
    }
}