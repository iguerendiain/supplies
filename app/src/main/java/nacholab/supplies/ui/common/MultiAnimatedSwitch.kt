package nacholab.supplies.ui.common

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nacholab.supplies.ui.theme.SuppliesTheme

@Composable
fun MultiAnimatedSwitch(
    tabs: List<String>,
    counters: List<Int> = tabs.map { 0 },
    selectedOption: Int = 0,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    tabColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    containerCornerRadius: Dp = 8.dp,
    tabCornerRadius: Dp = 6.dp,
    selectorHeight: Dp = 32.dp,
    tabHeight: Dp = 28.dp,
    spacing: Dp = 2.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(
        color = MaterialTheme.colorScheme.primary
    ),
    onTabSelected: (selectedIndex: Int) -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier
            .clip(RoundedCornerShape(containerCornerRadius))
            .height(selectorHeight)
            .fillMaxSize()
            .background(containerColor)
    ) {
        val segmentWidth = maxWidth / tabs.size

        val boxWidth = segmentWidth - spacing * 2
        val positions = tabs.indices.map { index ->
            segmentWidth * index + (segmentWidth - boxWidth) / 2
        }
        val animatedOffsetX by animateDpAsState(targetValue = positions[selectedOption], label = "")
        val containerHeight = maxHeight
        val verticalOffset = (containerHeight - tabHeight) / 2

        Box(
            modifier = Modifier
                .offset(x = animatedOffsetX, y = verticalOffset)
                .clip(RoundedCornerShape(tabCornerRadius))
                .width(boxWidth)
                .height(tabHeight)
                .background(tabColor)
        )

        if (tabs.size > 3) Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            tabs.subList(0, tabs.size-1).forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                        .width(1.dp)
                        .background(
                            if (selectedOption == index || selectedOption-1 == index) Color.Transparent
                            else MaterialTheme.colorScheme.background
                        )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            counters.forEach { count ->
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier
                        .width(segmentWidth)
                        .fillMaxHeight()
                        .padding(top = 2.dp, end = 2.dp)
                ){
                    if (count > 0) Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                            )
                    ){
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, text ->
                Text(
                    text = text,
                    style = textStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(segmentWidth)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onTabSelected(index) }
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun DynamicTabSelectorPreview() {
    SuppliesTheme {
        val selectedOption = remember { mutableIntStateOf(0) }
        val optionTexts = listOf("Tab 1", "Tab 2", "Tab 3")
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            MultiAnimatedSwitch(tabs = optionTexts, selectedOption = selectedOption.intValue) {
                Toast.makeText(context, "Selected tab: ${it + 1}", Toast.LENGTH_SHORT).show()
                selectedOption.intValue = it
            }
        }
    }
}