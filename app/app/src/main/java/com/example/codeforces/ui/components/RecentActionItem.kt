package com.example.codeforces.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeforces.R
import com.example.codeforces.model.RecentAction
import com.example.codeforces.ui.theme.getCodeforcesUserColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentActionItem(
    recentAction: RecentAction,
    onItemClick: (String) -> Unit
) {
    val userColor = getCodeforcesUserColor(recentAction.user_color)
    val blogUrl = "https://codeforces.com${recentAction.blog_link}"
    
    ElevatedCard(
        onClick = { onItemClick(blogUrl) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action icon
            recentAction.img?.let { imageInfo ->
                when (imageInfo.alt) {
                    "Necropost" -> Image(
                        painter = painterResource(id = R.drawable.necropost),
                        contentDescription = "Necropost",
                        modifier = Modifier.size(24.dp)
                    )
                    "Text created or updated" -> Image(
                        painter = painterResource(id = R.drawable.text_created_or_updated),
                        contentDescription = "Text created or updated",
                        modifier = Modifier.size(24.dp)
                    )
                    "New comment(s)" -> Image(
                        painter = painterResource(id = R.drawable.new_comments),
                        contentDescription = "New comment(s)",
                        modifier = Modifier.size(24.dp)
                    )
                    else -> Text(
                        text = imageInfo.alt,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Blog title
                Text(
                    text = recentAction.blog_title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                
                // By username
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "by ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = recentAction.user,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = userColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
} 