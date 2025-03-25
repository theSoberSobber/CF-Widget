package com.example.codeforces.model

import kotlinx.serialization.Serializable

@Serializable
data class RecentActionsResponse(
    val status: String,
    val result: List<RecentAction>
)

@Serializable
data class RecentAction(
    val timeSeconds: Long,
    val blogEntry: BlogEntry? = null,
    val comment: Comment? = null
)

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

data class ImageInfo(
    val alt: String,
    val image: String
) 