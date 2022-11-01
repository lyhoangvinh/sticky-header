package com.example.stickyheader

data class Book(
    val author: String,
    val country: String,
    val imageLink: String,
    val language: String,
    val link: String,
    val pages: Int,
    val title: String,
    val year: Int
) {
    fun createItems(showHeader: Boolean) = listOf(
        HeaderItemViewModel(title, showHeader = showHeader),
        ContentItemViewModel(title, author, year.toString(), country),
        ContentItemViewModel(title, author, year.toString(), country),
        ContentItemViewModel(title, author, year.toString(), country),
        ContentItemViewModel(title, author, year.toString(), country),
        ContentItemViewModel(title, author, year.toString(), country)
    )

    fun createItem2(showHeader: Boolean) = HeaderContentItemViewModel(
        title,
        createItems(showHeader),
        showHeader
    )

    fun createItems3() = listOf(
        ContentItemViewModel(title, author, year.toString(), country),
        ContentItemViewModel(title, author, year.toString(), country),
        ContentItemViewModel(title, author, year.toString(), country),
        ContentItemViewModel(title, author, year.toString(), country),
        ContentItemViewModel(title, author, year.toString(), country)
    )
}