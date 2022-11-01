package com.example.stickyheader

import android.content.Intent
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
import com.example.stickyheader.databinding.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(ActivityMainBinding.inflate(layoutInflater)) {
            setContentView(root)
            btnCollapsingToolbarLayout.setOnClickListener {
                startActivity(Intent(this@MainActivity, CollapsingActivity::class.java))
            }
            btnStickyHeader.setOnClickListener {
                startActivity(Intent(this@MainActivity, StickyActivity::class.java))
            }
        }
    }
}