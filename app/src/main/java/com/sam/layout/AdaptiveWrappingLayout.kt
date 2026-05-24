package com.sam.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Defines the horizontal placement of the primary content within the [AdaptiveWrappingLayout].
 * The wrapping content will flow automatically around the opposite side.
 */
enum class PrimaryAlignment {
    /** Places the primary content on the left, wrapping text on the right. */
    Left,
    /** Places the primary content on the right, wrapping text on the left. */
    Right
}


/**
 * A magazine-style layout that allows a list of items to wrap around a primary obstacle (like an image or quote).
 *
 * Behaving similarly to CSS `float`, this layout reserves a vertical column on the specified side of the
 * screen for the [primaryContent]. The [wrappingContent] is placed in the remaining space until it drops
 * below the primary column, at which point it automatically expands to take up the full width of the screen.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param topContent Optional header content that sits at the very top of the layout and spans the full screen width.
 * @param primaryContent The "obstacle" content (e.g., an image, pull quote, or side-menu) that sits on one side of the screen.
 * @param wrappingContent A list of Composable (e.g., paragraphs of text) that will flow alongside the primary content, and span full width once they drop below it.
 * @param primaryAlignment Which side of the screen the [primaryContent] should "float" to.
 * @param primaryWidthRatio The percentage of the available screen width the [primaryContent] should consume (e.g., 0.4f = 40%).
 * @param horizontalSpacing The horizontal gap between the primary column and the wrapping column.
 * @param verticalSpacing The vertical gap applied between all distinct child elements.
 */
@Composable
fun AdaptiveWrappingLayout(
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit = {},
    primaryContent: @Composable () -> Unit,
    wrappingContent: List<@Composable () -> Unit>,
    primaryAlignment: PrimaryAlignment = PrimaryAlignment.Right,
    primaryWidthRatio: Float = 0.4f,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
) {
    // Combine all content blocks into a single list of lists for the MultiContentMeasurePolicy.
    // Index 0: Top, Index 1: Primary, Index 2+: Wrapping
    val totalContent = buildList {
        add(topContent)
        add(primaryContent)
        addAll(wrappingContent)
    }

    Layout(
        contents = totalContent,
        modifier = modifier,
        measurePolicy = AdaptiveWrappingMeasurePolicy(
            primaryWidthRatio = primaryWidthRatio,
            horizontalSpacing = horizontalSpacing,
            verticalSpacing = verticalSpacing,
            primaryAlignment = primaryAlignment
        )
    )
}



/**
 * Internal measurement and placement logic for [AdaptiveWrappingLayout].
 */
private class AdaptiveWrappingMeasurePolicy(
    private val primaryWidthRatio: Float,
    private val horizontalSpacing: Dp,
    private val verticalSpacing: Dp,
    private val primaryAlignment: PrimaryAlignment
) : MultiContentMeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints,
    ): MeasureResult {
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()
        val totalWidth = constraints.maxWidth

        // 1. Calculate boundaries for the columns
        val availableWidth = totalWidth - horizontalSpacingPx
        val primaryWidth = (availableWidth * primaryWidthRatio).roundToInt()
        val wrappingWidth = availableWidth - primaryWidth

        // 2. Extract Measurables by their index
        val topMeasurables: List<Measurable> = measurables.getOrNull(0) ?: emptyList()
        val primaryMeasurables: List<Measurable> = measurables.getOrNull(1) ?: emptyList()
        val wrappingMeasurables: List<Measurable> = measurables.drop(2).flatten()

        // 3. Measure Top Content (Allowed to take full width)
        val topPlaceables = topMeasurables.map { it.measure(constraints) }
        val topHeight = topPlaceables.sumOf { it.height } +
                maxOf(0, (topPlaceables.size - 1) * verticalSpacingPx)

        // Calculate where the split-columns should begin
        val startYForColumns = if (topPlaceables.isNotEmpty()) {
            topHeight + verticalSpacingPx
        } else {
            0
        }

        // 4. Measure Primary Content
        val primaryConstraints = constraints.copy(minWidth = primaryWidth, maxWidth = primaryWidth)
        val primaryPlaceables = primaryMeasurables.map { it.measure(primaryConstraints) }
        val primaryContentHeight = primaryPlaceables.sumOf { it.height } +
                maxOf(0, (primaryPlaceables.size - 1) * verticalSpacingPx)

        // 5. Measure Wrapping Content
        var relativeWrapY = 0 // Tracks Y relative to the start of the columns (ignores topContent)
        val wrappingConstraints = constraints.copy(minWidth = wrappingWidth, maxWidth = wrappingWidth)

        val wrappingPlaceables: List<Placeable> = wrappingMeasurables.map {
            val currentPlaceable = if (relativeWrapY < primaryContentHeight) {
                // If it's physically beside the primary content, measure with constrained width
                it.measure(wrappingConstraints)
            } else {
                // If it drops below the primary content, allow it to span the full width
                it.measure(constraints)
            }
            relativeWrapY += currentPlaceable.height + verticalSpacingPx
            currentPlaceable
        }

        // Remove the trailing vertical space from the final height calculation
        if (wrappingPlaceables.isNotEmpty()) {
            relativeWrapY -= verticalSpacingPx
        }

        // Total height is the top section plus whichever column extends further down
        val totalLayoutHeight = startYForColumns + maxOf(primaryContentHeight, relativeWrapY)

        // 6. Placement Phase
        return layout(totalWidth, totalLayoutHeight) {

            // A. Place Top Content
            var currentY = 0
            topPlaceables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = currentY)
                currentY += placeable.height + verticalSpacingPx
            }

            // B. Place Primary Content
            val primaryStartX = if (primaryAlignment == PrimaryAlignment.Right) wrappingWidth + horizontalSpacingPx else 0
            var primY = startYForColumns

            primaryPlaceables.forEach { placeable ->
                placeable.placeRelative(x = primaryStartX, y = primY)
                primY += placeable.height + verticalSpacingPx
            }

            // C. Place Wrapping Content
            var wrapY = startYForColumns
            var wrapRelativeY = 0 // Used exclusively to check position against primaryContentHeight

            wrappingPlaceables.forEach { placeable ->
                val isBesidePrimary = wrapRelativeY < primaryContentHeight

                val currentWrapX = if (isBesidePrimary) {
                    if (primaryAlignment == PrimaryAlignment.Right) 0 else primaryWidth + horizontalSpacingPx
                } else {
                    0 // Snap back to full-width alignment once below the primary content
                }

                placeable.placeRelative(x = currentWrapX, y = wrapY)

                wrapY += placeable.height + verticalSpacingPx
                wrapRelativeY += placeable.height + verticalSpacingPx
            }
        }
    }
}

