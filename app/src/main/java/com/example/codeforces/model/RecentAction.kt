package com.example.codeforces.model

import kotlinx.serialization.Serializable

@Serializable
data class RecentActionsResponse(
    val recent_actions: List<RecentAction>
)

@Serializable
data class RecentAction(
    val user: String,
    val user_profile: String,
    val user_color: String,
    val blog_title: String,
    val blog_link: String,
    val img: ImageInfo? = null,
    val otherImg: List<ImageInfo> = emptyList(),
    val timeSeconds: Long = System.currentTimeMillis() / 1000,
    val blogEntry: BlogEntry? = null
) {
    fun toBlogEntry(): BlogEntry {
        return BlogEntry(
            id = blog_link.split("/").lastOrNull()?.toIntOrNull() ?: 0,
            originalLocale = "en",
            creationTimeSeconds = timeSeconds,
            authorHandle = user,
            title = blog_title,
            content = "",
            locale = "en",
            modificationTimeSeconds = timeSeconds,
            allowViewHistory = false,
            tags = emptyList(),
            rating = 0,
            url = blog_link
        )
    }
}

@Serializable
data class BlogEntry(
    val id: Int,
    val originalLocale: String,
    val creationTimeSeconds: Long,
    val authorHandle: String,
    val title: String,
    val content: String,
    val locale: String,
    val modificationTimeSeconds: Long,
    val allowViewHistory: Boolean,
    val tags: List<String>,
    val rating: Int,
    val url: String? = null
)

@Serializable
data class Comment(
    val id: Int,
    val creationTimeSeconds: Long,
    val commentatorHandle: String,
    val locale: String,
    val text: String,
    val rating: Int,
    val parentCommentId: Int? = null
)

@Serializable
data class ImageInfo(
    val alt: String,
    val image: String
) 