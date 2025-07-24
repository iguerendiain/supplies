package nacholab.supplies.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ConsumableContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
            )
            .background(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ){ content() }
}

@Composable
fun ConsumableName(name: String, modifier: Modifier = Modifier){
    Text(
        text = name,
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
fun ShoppingItem(name: String, modifier: Modifier = Modifier){
    Text(
        text = name,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
fun ConsumableLocation(modifier: Modifier = Modifier, homeLocation: String? = null, marketLocation: String? = null) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        homeLocation?.let { ConsumableLocationIconText(Icons.Default.Home, it) }
        marketLocation?.let { ConsumableLocationIconText(Icons.Default.ShoppingCart, it) }
    }
}

@Composable
fun ConsumableLocationIconText(icon: ImageVector, text: String){
    Row {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ComposableStockIndicator(current: Int, required: Int){
    Text(
        text = "$current / $required",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun LocationTitle(text: String){
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 22.dp, bottom = 8.dp)
    )
}

@Composable
fun ScreenTitle(text: String, modifier: Modifier = Modifier){
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
fun ToolbarIconButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

