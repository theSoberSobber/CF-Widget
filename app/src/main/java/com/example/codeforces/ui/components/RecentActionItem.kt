package com.example.codeforces.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.codeforces.R
import com.example.codeforces.model.ImageInfo
import com.example.codeforces.model.RecentAction
import com.example.codeforces.ui.theme.Black
import com.example.codeforces.ui.theme.Blue
import com.example.codeforces.ui.theme.Cyan
import com.example.codeforces.ui.theme.Gray
import com.example.codeforces.ui.theme.Green
import com.example.codeforces.ui.theme.Legendary
import com.example.codeforces.ui.theme.Orange
import com.example.codeforces.ui.theme.Purple
import com.example.codeforces.ui.theme.Red

@Composable
fun RecentActionItem(recentAction: RecentAction) {
    val context = LocalContext.current
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                val blogUrl = "https://codeforces.com${recentAction.blog_link}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(blogUrl))
                context.startActivity(intent)
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionImage(recentAction.img)
                
                // Display other images if available
                recentAction.otherImg.forEach { imgInfo -> 
                    Spacer(modifier = Modifier.width(4.dp))
                    ActionImage(imgInfo)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = recentAction.user,
                    color = getUserColor(recentAction.user_color),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = recentAction.blog_title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionImage(imageInfo: ImageInfo) {
    val resourceId = when(imageInfo.alt) {
        "New comment(s)" -> R.drawable.new_comments
        "Text created or updated" -> R.drawable.text_updated
        "Necropost" -> R.drawable.necropost
        else -> R.drawable.new_comments // Default fallback
    }
    
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = imageInfo.alt,
        modifier = Modifier.size(24.dp)
    )
}

fun getUserColor(colorName: String): Color {
    return when (colorName.lowercase()) {
        "red" -> Red
        "green" -> Green
        "blue" -> Blue
        "cyan" -> Cyan
        "purple" -> Purple
        "orange" -> Orange
        "black" -> Black
        "gray" -> Gray
        "legendary" -> Legendary
        else -> Black
    }
} 