package nacholab.supplies.ui.main.shoppinglist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nacholab.supplies.R
import nacholab.supplies.domain.ShoppingListItem
import nacholab.supplies.ui.common.FakeNavBar
import nacholab.supplies.ui.common.FakeStatusBar
import nacholab.supplies.ui.main.LocationTitle
import nacholab.supplies.ui.main.ScreenTitle
import nacholab.supplies.ui.main.ShoppingItem
import nacholab.supplies.ui.main.ToolbarIconButton

@Composable
fun ShoppingList(
    shoppingList: Map<String, List<ShoppingListItem>>,
    onRebuildShoppingList: () -> Unit,
    onShoppingItemPicked: (String, Boolean) -> Unit,
    onCommitShoppingList: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle(
            text = stringResource(R.string.main_screen_tabs_shoppinglist),
            modifier = Modifier.padding(vertical = 12.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            shoppingList.keys.map {
                item { LocationTitle(it) }
                shoppingList[it]?.forEach { c ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Checkbox(
                                checked = c.picked,
                                onCheckedChange = { onShoppingItemPicked(c.consumable.id, !c.picked) }
                            )
                            Text(
                                text = "${c.consumable.requiredStock - c.consumable.currentStock} x ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            ShoppingItem(
                                name = c.consumable.name,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

            }
        }
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceDim)
                .fillMaxWidth()
                .padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            ToolbarIconButton(Icons.Default.Refresh, onRebuildShoppingList)
            ToolbarIconButton(Icons.Default.CheckCircle, onCommitShoppingList)
        }
        FakeNavBar()
    }
}