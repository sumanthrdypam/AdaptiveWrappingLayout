package com.sam.layout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sam.layout.ui.theme.LayoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // A scrollable showcase of all layout capabilities
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        DemoHeader(title = "1. Magazine Article (Right Aligned)")
                        PreviewWithHeader()

                        Divider(Modifier.padding(vertical = 24.dp))

                        DemoHeader(title = "2. Editorial Pull-Quote (Left Aligned)")
                        PreviewLeftAligned()

                        Divider(Modifier.padding(vertical = 24.dp))

                        DemoHeader(title = "3. Wikipedia-Style Infobox")
                        PreviewWikiInfobox()

                        Divider(Modifier.padding(vertical = 24.dp))

                        DemoHeader(title = "4. E-Commerce Product Callout")
                        PreviewEcommerceCard()

                        Divider(Modifier.padding(vertical = 24.dp))

                        DemoHeader(title = "5. Tall Obstacle, Short Text")
                        PreviewShortWrappingText()

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DemoHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// ============================================================================
// PREVIEWS & USE CASES (Visible in Android Studio's Design Pane)
// ============================================================================

@Preview(showBackground = true, name = "1. Full-Width Header + Article", fontScale = 1.1f)
@Composable
fun PreviewWithHeader() {
    LayoutTheme {
        AdaptiveWrappingLayout(
            modifier = Modifier.padding(16.dp),
            primaryAlignment = PrimaryAlignment.Right,
            primaryWidthRatio = 0.4f,
            horizontalSpacing = 16.dp,
            verticalSpacing = 12.dp,
            topContent = {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Mastering Compose Layouts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "By Jane Doe • May 23, 2026",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                    Divider(color = Color.LightGray)
                }
            },
            primaryContent = {
                PlaceholderImage(text = "Author\nPortrait", aspectRatio = 1.0f)
            },
            wrappingContent = listOf(
                { Text("The top content block renders completely independently of the split columns. It spans the full 100% width.") },
                { Text("After the top content finishes measuring, it passes its Y-offset down to the primary and wrapping columns so they know exactly where to start drawing.") },
                { Text("This prevents us from needing to wrap our Custom Layout inside an external Column, keeping our view hierarchy flat and highly performant. Notice how this text beautifully spans the full width once it clears the portrait.") }
            )
        )
    }
}

@Preview(showBackground = true, name = "2. Left-Aligned Pull Quote", fontScale = 1.1f)
@Composable
fun PreviewLeftAligned() {
    LayoutTheme {
        AdaptiveWrappingLayout(
            modifier = Modifier.padding(16.dp),
            primaryAlignment = PrimaryAlignment.Left,
            primaryWidthRatio = 0.45f,
            horizontalSpacing = 20.dp,
            verticalSpacing = 16.dp,
            primaryContent = {
                PullQuote(text = "\"Custom layouts give you absolute control over the pixels.\"")
            },
            wrappingContent = listOf(
                { Text("Here we see the primary alignment swapped to the left side. This is highly useful for editorial pull-quotes.") },
                { Text("Instead of an image, the primary content is a styled box. The layout engine doesn't care what the composable is, as long as it has measurable bounds.") },
                { Text("Once again, this bottom paragraph drops completely below the quote and takes over the full width of the parent container.") }
            )
        )
    }
}

@Preview(showBackground = true, name = "3. Wikipedia-Style Infobox")
@Composable
fun PreviewWikiInfobox() {
    LayoutTheme {
        AdaptiveWrappingLayout(
            modifier = Modifier.padding(16.dp),
            primaryAlignment = PrimaryAlignment.Right,
            primaryWidthRatio = 0.45f,
            horizontalSpacing = 16.dp,
            verticalSpacing = 8.dp,
            primaryContent = {
                WikiInfoBox()
            },
            wrappingContent = listOf(
                { Text("The AdaptiveWrappingLayout is perfect for creating dense data views like Wikipedia pages.") },
                { Text("The InfoBox on the right is a complex composable containing its own Columns, Rows, and Dividers. The layout engine simply measures its final height and flows this text right past it.") },
                { Text("Because the logic is dynamic, if the device rotates or the screen size changes, the math instantly recalculates exactly which paragraphs should be squished and which should span the full width of the screen, ensuring a perfect responsive design every time.") }
            )
        )
    }
}

@Preview(showBackground = true, name = "4. E-Commerce Product Card")
@Composable
fun PreviewEcommerceCard() {
    LayoutTheme {
        AdaptiveWrappingLayout(
            modifier = Modifier.padding(16.dp),
            primaryAlignment = PrimaryAlignment.Left,
            primaryWidthRatio = 0.4f,
            horizontalSpacing = 16.dp,
            verticalSpacing = 12.dp,
            primaryContent = {
                ProductCard()
            },
            wrappingContent = listOf(
                { Text("Primary content can be fully interactive. The product card on the left contains clickable buttons and dynamic state.", fontWeight = FontWeight.Bold) },
                { Text("This layout pattern is often seen in long-form product reviews or blog posts where the author wants to embed an affiliate link or product purchase card directly inline with the text.") },
                { Text("Once the review text drops past the height of the interactive card, it resumes standard full-width rendering.") }
            )
        )
    }
}

@Preview(showBackground = true, name = "5. Short Wrapping Text (Tall Primary)")
@Composable
fun PreviewShortWrappingText() {
    LayoutTheme {
        AdaptiveWrappingLayout(
            modifier = Modifier.padding(16.dp),
            primaryAlignment = PrimaryAlignment.Right,
            primaryWidthRatio = 0.3f,
            horizontalSpacing = 16.dp,
            verticalSpacing = 8.dp,
            primaryContent = {
                PlaceholderImage(text = "Very Tall\nAd Banner\n\n|\n|\n|\n|\n|\nV", aspectRatio = 0.4f)
            },
            wrappingContent = listOf(
                { Text("Sometimes the text is shorter than the image.") },
                { Text("The layout still accurately wraps its own height to the tallest column, preventing the tall image from clipping into the content below it.") }
            )
        )
    }
}

// ============================================================================
// MOCK UI COMPONENTS (For making the demos look good)
// ============================================================================

@Composable
fun PlaceholderImage(text: String, aspectRatio: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PullQuote(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WikiInfoBox() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDEE2E6))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Quick Facts", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Divider(Modifier.padding(vertical = 8.dp))
            WikiRow("Developer", "Sam")
            WikiRow("Platform", "Android")
            WikiRow("Release", "2026")
        }
    }
}

@Composable
private fun WikiRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(key, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProductCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlaceholderImage(text = "Widget", aspectRatio = 1.0f)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Pro Widget 2.0", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("$99.99", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* Demo only */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Buy", fontSize = 12.sp)
            }
        }
    }
}