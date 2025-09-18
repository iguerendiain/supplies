package nacholab.supplies.ui.main.supplies

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nacholab.supplies.R
import nacholab.supplies.domain.Supply
import nacholab.supplies.ui.EditingMode
import nacholab.supplies.ui.SortMode
import nacholab.supplies.ui.common.FakeNavBar
import nacholab.supplies.ui.main.ComposableStockIndicator
import nacholab.supplies.ui.main.ConsumableContainer
import nacholab.supplies.ui.main.ConsumableLocation
import nacholab.supplies.ui.main.ConsumableName
import nacholab.supplies.ui.main.LocationTitle
import nacholab.supplies.ui.main.ScreenTitle
import nacholab.supplies.ui.main.ToolbarIconButton
import nacholab.supplies.ui.theme.SuppliesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyList(
    consumables: List<Supply>,
    consumablesByMarketLocation: Map<String, List<Supply>>,
    consumablesByHomeLocation: Map<String, List<Supply>>,
    onAddEditConsumable: (Supply?) -> Unit,
    onReduceCurrentStock: (String) -> Unit,
    onIncreaseCurrentStock: (String) -> Unit,
    onReduceRequiredStock: (String) -> Unit,
    onIncreaseRequiredStock: (String) -> Unit,
    onSetEditMode: () -> Unit,
    onNameListMode: () -> Unit,
    onHomeLocationListMode: () -> Unit,
    onMarketLocationListMode: () -> Unit,
    onSettings: () -> Unit,
    editingMode: EditingMode,
    sortMode: SortMode,
    isReloading: Boolean,
    onReload: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ScreenTitle(
            text = stringResource(R.string.main_screen_tabs_supplies),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            isRefreshing = isReloading,
            onRefresh = onReload,
            state = rememberPullToRefreshState()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                when (sortMode) {
                    SortMode.NAME -> consumables.map { consumable ->
                        item {
                            ConsumableContainer(
                                modifier = Modifier.clickable(onClick = {
                                    onAddEditConsumable(
                                        consumable
                                    )
                                })
                            ) {
                                when (editingMode) {
                                    EditingMode.REQUIRED_STOCK, EditingMode.CURRENT_STOCK -> {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(1f)
                                        ) {
                                            ConsumableName(
                                                consumable.name
                                            )
                                            ConsumableLocation(
                                                homeLocation = consumable.locationAtHome,
                                                marketLocation = consumable.locationAtMarket
                                            )
                                            StockEditButton(
                                                consumable = consumable,
                                                onReduceCurrentStock = onReduceCurrentStock,
                                                onIncreaseCurrentStock = onIncreaseCurrentStock,
                                                onReduceRequiredStock = onReduceRequiredStock,
                                                onIncreaseRequiredStock = onIncreaseRequiredStock
                                            )
                                        }
                                    }

                                    else -> Column(
                                        modifier = Modifier.fillMaxWidth(1f)
                                    ) {
                                        ConsumableName(
                                            consumable.name
                                        )
                                        Row {
                                            ConsumableLocation(
                                                homeLocation = consumable.locationAtHome,
                                                marketLocation = consumable.locationAtMarket,
                                                modifier = Modifier.weight(1f)
                                            )
                                            ComposableStockIndicator(
                                                consumable.currentStock,
                                                consumable.requiredStock
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SortMode.MARKET_LOCATION -> consumablesByMarketLocation
                        .keys
                        .forEach {
                            item { LocationTitle(it) }
                            consumablesByMarketLocation[it]?.map { consumable ->
                                item {
                                    ConsumableContainer {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .clickable(onClick = {
                                                    onAddEditConsumable(
                                                        consumable
                                                    )
                                                })
                                        ) {
                                            ConsumableName(
                                                consumable.name
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(1f)
                                            ) {
                                                ConsumableLocation(
                                                    homeLocation = consumable.locationAtHome,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                ComposableStockIndicator(
                                                    consumable.currentStock,
                                                    consumable.requiredStock
                                                )
                                            }
                                            if (editingMode == EditingMode.REQUIRED_STOCK || editingMode == EditingMode.CURRENT_STOCK) StockEditButton(
                                                consumable = consumable,
                                                onReduceCurrentStock = onReduceCurrentStock,
                                                onIncreaseCurrentStock = onIncreaseCurrentStock,
                                                onReduceRequiredStock = onReduceRequiredStock,
                                                onIncreaseRequiredStock = onIncreaseRequiredStock
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    SortMode.HOME_LOCATION -> consumablesByHomeLocation
                        .keys
                        .forEach {
                            item { LocationTitle(it) }
                            consumablesByHomeLocation[it]?.map { consumable ->
                                item {
                                    ConsumableContainer {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .clickable(onClick = {
                                                    onAddEditConsumable(
                                                        consumable
                                                    )
                                                })
                                        ) {
                                            ConsumableName(
                                                consumable.name
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(1f)
                                            ) {
                                                ConsumableLocation(
                                                    marketLocation = consumable.locationAtMarket,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                ComposableStockIndicator(
                                                    consumable.currentStock,
                                                    consumable.requiredStock
                                                )
                                            }
                                            if (editingMode == EditingMode.REQUIRED_STOCK || editingMode == EditingMode.CURRENT_STOCK) StockEditButton(
                                                consumable = consumable,
                                                onReduceCurrentStock = onReduceCurrentStock,
                                                onIncreaseCurrentStock = onIncreaseCurrentStock,
                                                onReduceRequiredStock = onReduceRequiredStock,
                                                onIncreaseRequiredStock = onIncreaseRequiredStock
                                            )
                                        }
                                    }
                                }
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
            ToolbarIconButton(
                Icons.Default.Settings,
                onSettings
            )
            ToolbarIconButton(
                Icons.Default.Edit,
                onSetEditMode
            )
            ToolbarIconButton(
                Icons.Default.Menu,
                onNameListMode
            )
            ToolbarIconButton(
                Icons.Default.Home,
                onHomeLocationListMode
            )
            ToolbarIconButton(
                Icons.Default.ShoppingCart,
                onMarketLocationListMode
            )
            ToolbarIconButton(Icons.Default.AddCircle) {
                onAddEditConsumable(null)
            }
        }
        FakeNavBar()
    }
}

@Composable
fun StockEditButton(
    consumable: Supply,
    onReduceCurrentStock: (String) -> Unit,
    onIncreaseCurrentStock: (String) -> Unit,
    onReduceRequiredStock: (String) -> Unit,
    onIncreaseRequiredStock: (String) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(
            onClick = { onReduceCurrentStock(consumable.id) }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )
        }
        Text(
            text =consumable.currentStock.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        IconButton(
            onClick = { onIncreaseCurrentStock(consumable.id) }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )
        }
        Text(
            text = "/",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        IconButton(
            onClick = { onReduceRequiredStock(consumable.id) }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )
        }
        Text(
            text = consumable.requiredStock.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        IconButton(
            onClick = { onIncreaseRequiredStock(consumable.id) }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SupplyListPreview(){
    SuppliesTheme {
        SupplyList(
            consumables = listOf(
                Supply(
                    id = "1",
                    name = "Latas de porotos",
                    requiredStock = 2,
                    currentStock = 1,
                    locationAtHome = "Alacena",
                    locationAtMarket = "Almacén"
                ),
                Supply(
                    id = "1",
                    name = "Azucar",
                    requiredStock = 2,
                    currentStock = 1,
                    locationAtHome = "Alacena",
                    locationAtMarket = "Almacén"
                ),
                Supply(
                    id = "1",
                    name = "Latas de porotos",
                    requiredStock = 2,
                    currentStock = 1,
                    locationAtHome = "Alacena",
                    locationAtMarket = "Almacén"
                ),
                Supply(
                    id = "1",
                    name = "Sal",
                    requiredStock = 2,
                    currentStock = 1,
                    locationAtHome = "Alacena",
                    locationAtMarket = "Almacén"
                ),
                Supply(
                    id = "1",
                    name = "Aceite de oliva",
                    requiredStock = 2,
                    currentStock = 1,
                    locationAtHome = "Alacena",
                    locationAtMarket = "Almacén"
                ),
                Supply(
                    id = "1",
                    name = "Latas de champiñones",
                    requiredStock = 2,
                    currentStock = 1,
                    locationAtHome = "Alacena",
                    locationAtMarket = "Almacén"
                )
            ),
            consumablesByMarketLocation = mapOf(),
            consumablesByHomeLocation = mapOf(),
            onAddEditConsumable = { },
            onReduceCurrentStock = { },
            onIncreaseCurrentStock = { },
            onReduceRequiredStock = { },
            onIncreaseRequiredStock = { },
            onSetEditMode = { },
            editingMode = EditingMode.NAME,
            sortMode = SortMode.NAME,
            onSettings = {},
            onNameListMode = {},
            onHomeLocationListMode = {},
            onMarketLocationListMode = {},
            isReloading = false,
            onReload = { }
        )
    }
}
