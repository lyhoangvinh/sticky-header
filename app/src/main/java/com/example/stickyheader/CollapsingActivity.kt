package com.example.stickyheader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.stickyheader.databinding.ActivityCollapsingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollapsingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = MainAdapter()
        with(ActivityCollapsingBinding.inflate(layoutInflater)) {
            setContentView(root)
            rcv.adapter = adapter
            val dividerItemDecoration =
                DividerItemDecoration(this@CollapsingActivity, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(this@CollapsingActivity, R.drawable.list_divider)
                ?.let { dividerItemDecoration.setDrawable(it) }
            rcv.addItemDecoration(dividerItemDecoration)
            lifecycleScope.launch(Dispatchers.IO) {
                val items = getItems()
                val header = items.find { it.year < 0 }?.title
                val data = items.flatMapIndexed { index, book ->
                    if (index == 0) {
                        book.createItems3()
                    } else {
                        book.createItems(book.year < 0)
                    }
                }
                withContext(Dispatchers.Main) {
                    tvStickyHeader.text = header
                    adapter.submitList(data)
                    adapter.addHeaderListener(
                        positionListener = { position ->
                            if (position == 0) {
                                tvStickyHeader.text = header
                            }
                        },
                        headerListener = {
                            tvStickyHeader.text = it
                        }
                    )
                }
            }
        }
    }
}