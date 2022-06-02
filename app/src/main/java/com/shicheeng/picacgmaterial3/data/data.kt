package com.shicheeng.picacgmaterial3.data

data class CategoryData(val title: String, val url: String)

data class RankData(
    val _id: String,
    val title: String,
    val author: String,
    val url: String,
    val categories: MutableList<String>,
    val leaderboardCount: Int,
    val decType: String = "绅士指名数：",
)

data class ComicItemCommonData(
    val _id: String,
    val title: String,
    val author: String,
    val url: String,
)

data class FavoriteItemData(
    val _id: String,
    val title: String,
    val author: String,
    val url: String,
    val categories: MutableList<String>,
)

data class ComicChpaterItemData(
    val _id: String,
    val title: String,
    val order: Int,
    val time: String,
)