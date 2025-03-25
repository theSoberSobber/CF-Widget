package com.example.codeforces.model

data class RecentActionsResponse(
    val recent_actions: List<RecentAction>
)

data class RecentAction(
    val user: String,
    val user_profile: String,
    val user_color: String,
    val blog_title: String,
    val blog_link: String,
    val img: ImageInfo,
    val otherImg: List<ImageInfo>
)

data class ImageInfo(
    val alt: String,
    val image: String
) 