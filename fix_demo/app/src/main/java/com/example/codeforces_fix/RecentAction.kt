package com.example.codeforces_fix

// Previous model structure
data class OldRecentActionsResponse(
    val status: String,
    val result: List<OldRecentAction>
)

data class OldRecentAction(
    val timeSeconds: Long,
    val blogEntry: OldBlogEntry? = null,
    val comment: OldComment? = null
)

data class OldBlogEntry(
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

data class OldComment(
    val id: Int,
    val creationTimeSeconds: Long,
    val commentatorHandle: String,
    val locale: String,
    val text: String,
    val rating: Int,
    val parentCommentId: Int? = null
)

// New model structure
data class NewRecentActionsResponse(
    val recent_actions: List<NewRecentAction>
)

data class NewRecentAction(
    val user: String,
    val user_profile: String,
    val user_color: String,
    val blog_title: String,
    val blog_link: String,
    val img: ImageInfo? = null,
    val otherImg: List<ImageInfo> = emptyList(),
    // For backward compatibility
    val timeSeconds: Long = System.currentTimeMillis() / 1000,
    val blogEntry: OldBlogEntry? = null
) {
    // Method to create a backward-compatible BlogEntry from this new model
    fun toBlogEntry(): OldBlogEntry {
        return OldBlogEntry(
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

data class ImageInfo(
    val alt: String,
    val image: String
) 