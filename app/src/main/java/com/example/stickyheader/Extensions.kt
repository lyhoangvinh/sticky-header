package com.example.stickyheader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException

fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this.context).inflate(layout, this, attachToRoot)
}

private const val SOURCE_FILE_NAME = "data.json"

fun Context.getItems(context: Context): List<Book> {

    val jsonString: String
    try {
        jsonString =
            context.assets.open(SOURCE_FILE_NAME).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return emptyList()
    }

    return GsonBuilder().create()
        .fromJson(jsonString, object : TypeToken<List<Book>>() {}.type)
}