package com.example.stickyheader

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.stickyheader.databinding.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StickyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = MainAdapter()
        with(ActivityStickyBinding.inflate(layoutInflater)) {
            setContentView(root)
            rcv.adapter = adapter
            rcv.addItemDecoration(StickyHeaderDecoration(adapter, root))
            val dividerItemDecoration = DividerItemDecoration(this@StickyActivity, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(this@StickyActivity, R.drawable.list_divider)
                ?.let { dividerItemDecoration.setDrawable(it) }
            rcv.addItemDecoration(dividerItemDecoration)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val data = getItems().map {
                it.createItem2(it.year < 0)
            }
            withContext(Dispatchers.Main) {
                adapter.submitList(data)
            }
        }
    }
}