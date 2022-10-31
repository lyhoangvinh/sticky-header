package com.example.stickyheader

interface ItemViewModel {
    val idViewModel: String?
}

data class HeaderItemViewModel(val title: String, var showHeader: Boolean?) : ItemViewModel {
    override val idViewModel: String
        get() = title
}

data class ContentItemViewModel(val name: String, val author: String, val year: String, val country: String) : ItemViewModel {
    override val idViewModel: String
        get() = name
}

data class HeaderContentItemViewModel(val name: String, val list: List<ItemViewModel>, var showHeader: Boolean?) : ItemViewModel {
    override val idViewModel: String
        get() = name
}



