package nacholab.supplies.ui

import nacholab.supplies.domain.Supply
import nacholab.supplies.domain.ShoppingListItem

data class MainState(
    val supplies: List<Supply>,
    val suppliesByMarketLocation: Map<String, List<Supply>>,
    val suppliesByHomeLocation: Map<String, List<Supply>>,
    val shoppingList: Map<String, List<ShoppingListItem>>,
    val editingConsumable: Supply?,
    val marketLocations: List<String>,
    val homeLocations: List<String>,
    val editingMode: EditingMode,
    val sort: SortMode,
    val exportDBToFile: String?,
    val isAuthenticated: Boolean,
    val isAuthenticationLoading: Boolean,
    val waitingForCodeVerification: Boolean,
    val authenticationError: String?,
    val suppliesLoading: Boolean,
    val suppliesLoadingError: String?
){
    companion object{
        val DEFAULT = MainState(
            supplies = listOf(),
            suppliesByMarketLocation= mapOf(),
            suppliesByHomeLocation = mapOf(),
            shoppingList = mapOf(),
            editingConsumable = null,
            marketLocations = listOf(),
            homeLocations = listOf(),
            editingMode = EditingMode.NO_EDITING,
            sort = SortMode.NAME,
            exportDBToFile = null,
            isAuthenticated = false,
            isAuthenticationLoading = false,
            waitingForCodeVerification = false,
            authenticationError = null,
            suppliesLoading = false,
            suppliesLoadingError = null,
        )
    }
}

enum class EditingMode {
    NO_EDITING, CURRENT_STOCK, REQUIRED_STOCK, MARKET_LOCATION, HOME_LOCATION, NAME
}

enum class SortMode {
    NAME, MARKET_LOCATION, HOME_LOCATION
}