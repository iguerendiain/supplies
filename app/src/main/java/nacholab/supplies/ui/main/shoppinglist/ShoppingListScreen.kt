package nacholab.supplies.ui.main.shoppinglist

import androidx.compose.runtime.Composable
import nacholab.supplies.ui.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShoppingListScreen(mainViewModel: MainViewModel = koinViewModel()){
    val state = mainViewModel.state.value

    ShoppingList(
        shoppingList = state.shoppingList,
        onRebuildShoppingList = { mainViewModel.buildShoppingList() },
        onShoppingItemPicked = { id, p -> mainViewModel.markItemAsPicked(id, p) },
        onCommitShoppingList = {
            mainViewModel.updateDBFromShoppinglist()
            mainViewModel.buildShoppingList()
        }
    )
}