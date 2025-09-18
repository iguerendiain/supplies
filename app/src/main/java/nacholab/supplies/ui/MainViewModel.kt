package nacholab.supplies.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nacholab.supplies.domain.DBRepository
import nacholab.supplies.domain.NetworkRespository
import nacholab.supplies.domain.PreferencesRepository
import nacholab.supplies.domain.RAMRepository
import nacholab.supplies.domain.ShoppingListItem
import nacholab.supplies.domain.Supply
import nacholab.supplies.network.NetworkMapper
import nacholab.supplies.storage.SupplyMapper
import java.io.File
import java.util.UUID

class MainViewModel(
    private val dbRepository: DBRepository,
    private val ramRepository: RAMRepository,
    private val preferencesRepository: PreferencesRepository,
    private val networkRespository: NetworkRespository
): ViewModel() {
    private val _state = mutableStateOf(MainState.Companion.DEFAULT)
    val state: State<MainState> = _state

    private var initialized = false

    fun initialize(){
        if (!initialized) viewModelScope.launch {
            val currentSessionId = preferencesRepository.getSessionId()
            ramRepository.storeSessionId(currentSessionId)

            if (currentSessionId.isNotBlank()){
                reloadMySupplies()

                val ramSupplies = ramRepository.getAllSupplies()

                if (ramSupplies.isEmpty()){
                    dbRepository
                        .loadCurrentDB()
                        .map { SupplyMapper.buildFrom(it) }
                        .let {
                            ramRepository.storeAllSupplies(it)
                            _state.value = state.value.copy(
                                supplies = it,
                                isAuthenticated = true
                            )
                        }
                }else {
                    _state.value = state.value.copy(
                        supplies = ramSupplies,
                        isAuthenticated = true
                    )
                }
            }else{
                dbRepository.clearDB()
                ramRepository.storeAllSupplies(listOf())
                _state.value = state.value.copy(
                    supplies = listOf(),
                    isAuthenticated = false
                )
            }
        }
        initialized = true
    }

    fun setEditingMode(editingMode: EditingMode){
        when (editingMode){
            EditingMode.MARKET_LOCATION -> updateConsumablesByMarketLocation()
            EditingMode.HOME_LOCATION -> updateConsumablesByHomeLocation()
            else -> {}
        }
        _state.value = state.value.copy(editingMode = editingMode)
    }

    fun setSortMode(sortMode: SortMode){
        when (sortMode){
            SortMode.MARKET_LOCATION -> updateConsumablesByMarketLocation()
            SortMode.HOME_LOCATION -> updateConsumablesByHomeLocation()
            SortMode.NAME -> sortConsumables()
        }
        _state.value = state.value.copy(sort = sortMode)
    }

    private fun sortConsumables(){
        _state.value = state.value.copy(
            supplies = state.value.supplies.sortedBy { it.name }
        )
    }

    private fun updateConsumablesByMarketLocation(){
        val consumables = state.value.supplies
        val locations = consumables
            .map { it.locationAtMarket }
            .distinct()
            .sorted()

        val consumablesByMarketLocation = HashMap<String, List<Supply>>()
        locations.forEach { location ->
            consumables
                .filter { it.locationAtMarket == location }
                .let { consumablesByMarketLocation[location] = it}
        }

        _state.value = state.value.copy(suppliesByMarketLocation = consumablesByMarketLocation)
    }

    private fun updateConsumablesByHomeLocation(){
        val consumables = state.value.supplies
        val locations = consumables
            .map { it.locationAtHome }
            .distinct()
            .sorted()

        val consumablesByHomeLocation = HashMap<String, List<Supply>>()
        locations.forEach { location ->
            consumables
                .filter { it.locationAtHome == location }
                .let { consumablesByHomeLocation[location] = it}
        }

        _state.value = state.value.copy(suppliesByHomeLocation = consumablesByHomeLocation)
    }

    fun setEditingConsumable(consumable: Supply?){
        _state.value = state.value.copy(editingConsumable = consumable)
    }

    fun addRequiredStock(id: String){
        viewModelScope.launch {
            val updatedConsumables = state
                .value
                .supplies
                .map {
                    if (it.id == id)
                        it.copy(requiredStock = it.requiredStock + 1)
                    else it
                }

            _state.value = state.value.copy(supplies = updatedConsumables)
            dbRepository.updateDB(updatedConsumables.map { SupplyMapper.buildFrom(it) })
            updateConsumablesByHomeLocation()
            updateConsumablesByMarketLocation()
            uploadSupplies()
        }
    }

    fun reduceRequiredStock(id: String){
        viewModelScope.launch {
            val updatedConsumables = state
                .value
                .supplies
                .map {
                    if (it.id == id)
                        it.copy(requiredStock = it.requiredStock - 1)
                    else it
                }

            _state.value = state.value.copy(supplies = updatedConsumables)
            dbRepository.updateDB(updatedConsumables.map { SupplyMapper.buildFrom(it) })
            updateConsumablesByHomeLocation()
            updateConsumablesByMarketLocation()
            uploadSupplies()
        }
    }

    fun addCurrentStock(id: String){
        viewModelScope.launch {
            val updatedConsumables = state
                .value
                .supplies
                .map {
                    if (it.id == id)
                        it.copy(currentStock = it.currentStock + 1)
                    else it
                }

            _state.value = state.value.copy(supplies = updatedConsumables)
            dbRepository.updateDB(updatedConsumables.map { SupplyMapper.buildFrom(it) })
            updateConsumablesByHomeLocation()
            updateConsumablesByMarketLocation()
            uploadSupplies()
        }
    }

    fun reduceCurrentStock(id: String){
        viewModelScope.launch {
            val updatedConsumables = state
                .value
                .supplies
                .map {
                    if (it.id == id)
                        it.copy(currentStock = it.currentStock - 1)
                    else it
                }

            _state.value = state.value.copy(supplies = updatedConsumables)
            dbRepository.updateDB(updatedConsumables.map { SupplyMapper.buildFrom(it) })
            updateConsumablesByHomeLocation()
            updateConsumablesByMarketLocation()
            uploadSupplies()
        }
    }

    fun markItemAsPicked(id: String, picked: Boolean){
        val currentShoppingList = state.value.shoppingList
        val consumableLocation = state.value.supplies.find { it.id == id }?.locationAtMarket

        val updatedShoppingList = HashMap<String, List<ShoppingListItem>>()

        currentShoppingList.keys.forEach { location ->
            if (consumableLocation == location){
                currentShoppingList[location]?.map {
                    if (it.consumable.id == id) it.copy(picked = picked)
                    else it
                }?.let { upd -> updatedShoppingList[location] = upd}
            }else currentShoppingList[location]?.let { updatedShoppingList[location] = it }
        }

        _state.value = state.value.copy(shoppingList = updatedShoppingList)
    }

    fun updateDBFromShoppinglist(){
        val flatShoppingList = arrayListOf<ShoppingListItem>()

        state.value.shoppingList.forEach {
            flatShoppingList.addAll(it.value)
        }

        updateDBFromShoppinglist(flatShoppingList)
        uploadSupplies()
    }

    private fun updateDBFromShoppinglist(shoppingList: List<ShoppingListItem>){
        viewModelScope.launch {
            val updatedConsumables = state
                .value
                .supplies
                .map {
                    if (shoppingList.find { s -> s.consumable.id == it.id }?.picked == true)
                        it.copy(currentStock = it.requiredStock)
                    else it
                }

            _state.value = state.value.copy(supplies = updatedConsumables)
            dbRepository.updateDB(updatedConsumables.map { SupplyMapper.buildFrom(it) })
        }
    }

    fun buildShoppingList(){
        _state.value = state.value.copy(
            shoppingList = buildShoppingList(_state.value.supplies)
        )
    }
    fun buildShoppingList(consumables: List<Supply>): HashMap<String, List<ShoppingListItem>> {
        val missingConsumables = consumables
            .filter {
                (it.requiredStock > 0 && it.currentStock < it.requiredStock) ||
                        (it.requiredStock == -1 && it.currentStock != -1)
            }

        val marketLocations = missingConsumables
            .map { it.locationAtMarket }
            .distinct()
            .sorted()

        val shoppingList = HashMap<String, List<ShoppingListItem>>()
        marketLocations.forEach { location ->
            shoppingList[location] = missingConsumables
                .filter { it.locationAtMarket == location }
                .map { ShoppingListItem(it, false) }
        }

        return shoppingList
    }

    fun updateConsumable(
        id: String,
        name: String,
        homeLocation: String,
        marketLocation: String,
        currentStock: Int,
        requiredStock: Int
    ) {
        viewModelScope.launch {
            val updatedConsumables = state.value.supplies.map {
                if (it.id == id) it.copy(
                    name = name,
                    locationAtHome = homeLocation,
                    locationAtMarket = marketLocation,
                    currentStock = currentStock,
                    requiredStock = requiredStock
                ) else it
            }


            _state.value = state.value.copy(supplies = updatedConsumables)
            dbRepository.updateDB(updatedConsumables.map { SupplyMapper.buildFrom(it) })
            updateConsumablesByHomeLocation()
            updateConsumablesByMarketLocation()
            uploadSupplies()
        }
    }

    fun addConsumable(
        name: String,
        homeLocation: String,
        marketLocation: String,
        currentStock: Int,
        requiredStock: Int
    ) {
        viewModelScope.launch {
            val mutableConsumables = state.value.supplies.toMutableList()
            mutableConsumables.add(
                Supply(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    locationAtHome = homeLocation,
                    locationAtMarket = marketLocation,
                    currentStock = currentStock,
                    requiredStock = requiredStock
                )
            )
            _state.value = state.value.copy(supplies = mutableConsumables)
            dbRepository.updateDB(mutableConsumables.map { SupplyMapper.buildFrom(it) })
            updateConsumablesByHomeLocation()
            updateConsumablesByMarketLocation()
            uploadSupplies()
        }
    }

    fun importDBFromFile(file: File) {
        viewModelScope.launch {
            dbRepository.importFromFile(file)
            val newSupplies = dbRepository.loadCurrentDB()
            _state.value = state.value.copy(supplies = newSupplies.map { SupplyMapper.buildFrom(it) })
            updateConsumablesByHomeLocation()
            updateConsumablesByMarketLocation()
            uploadSupplies()
        }
    }

    fun exportDBTofile() {
        viewModelScope.launch {
            val db = dbRepository.exportToJSON()
            _state.value = state.value.copy(exportDBToFile = db)
        }
    }

    fun exportDBToFileSignalClear(){
        _state.value = state.value.copy(exportDBToFile = null)
    }

    fun deleteConsumable(id: String) {
        viewModelScope.launch {
            val updatedConsumables = state.value.supplies.filter { it.id != id }
            _state.value = state.value.copy(supplies = updatedConsumables)
            dbRepository.updateDB(updatedConsumables.map { SupplyMapper.buildFrom(it) })
            updateConsumablesByHomeLocation()
            updateConsumablesByMarketLocation()
            uploadSupplies()
        }
    }

    fun authenticateUser(email: String) {
        if (!email.contains("@") || !email.contains(".") || email.length < 8 ){
            _state.value = state.value.copy(
                waitingForCodeVerification = false,
                isAuthenticationLoading = false,
                authenticationError = "Invalid email address"
            )
        }else {
            if (!state.value.isAuthenticationLoading) {
                _state.value = state.value.copy(
                    waitingForCodeVerification = false,
                    isAuthenticationLoading = true,
                    authenticationError = null
                )
                viewModelScope.launch {
                    try {
                        if (networkRespository.authenticateUser(email)) {
                            _state.value = state.value.copy(
                                waitingForCodeVerification = true,
                                isAuthenticationLoading = false,
                                authenticationError = null
                            )
                        } else {
                            _state.value = state.value.copy(
                                waitingForCodeVerification = false,
                                isAuthenticationLoading = false,
                                authenticationError = "Network error..."
                            )
                        }
                    }catch (e: Exception){
                        _state.value = state.value.copy(
                            waitingForCodeVerification = false,
                            isAuthenticationLoading = false,
                            authenticationError = e.message + e.stackTraceToString()
                        )
                    }
                }
            }
        }
    }

    fun verifyCode(email: String, code: String){
        if (code.length != 6){
            _state.value = state.value.copy(
                waitingForCodeVerification = true,
                isAuthenticationLoading = false,
                authenticationError = "Invalid verification code"
            )
        }else {
            if (!state.value.isAuthenticationLoading) {
                _state.value = state.value.copy(
                    waitingForCodeVerification = true,
                    isAuthenticationLoading = true,
                    authenticationError = null
                )
                viewModelScope.launch {
                    try {
                        val createdSession = networkRespository.createSession(email, code)

                        if (createdSession!=null){
                            preferencesRepository.storeSessionId(createdSession.id)
                            reloadMySupplies()
                            _state.value = state.value.copy(
                                isAuthenticated = true,
                                waitingForCodeVerification = false,
                                isAuthenticationLoading = false,
                                authenticationError = null,
                                suppliesLoading = true
                            )
                        }else{
                            _state.value = state.value.copy(
                                waitingForCodeVerification = true,
                                isAuthenticationLoading = false,
                                authenticationError = "Invalid code!"
                            )
                        }
                    }catch (e: Exception){
                        _state.value = state.value.copy(
                            waitingForCodeVerification = true,
                            isAuthenticationLoading = false,
                            authenticationError = e.message + e.stackTraceToString()
                        )
                    }
                }
            }
        }
    }

    fun clearAuthenticationError(){
        _state.value = state.value.copy(authenticationError = null)
    }

    fun reloadMySupplies(){
        _state.value = state.value.copy(suppliesLoading = true, suppliesLoadingError = null)

        viewModelScope.launch {
            try {
                val apiSupplies = networkRespository.getMySupplies()
                val supplies = apiSupplies.map { SupplyMapper.buildFrom(it) }
                val dbSupplies = supplies.map { SupplyMapper.buildFrom(it) }

                dbRepository.updateDB(dbSupplies)
                ramRepository.storeAllSupplies(supplies)

                _state.value = state.value.copy(
                    supplies = supplies,
                    suppliesLoading = false,
                    suppliesLoadingError = null
                )
            }catch (e: Exception){
                _state.value = state.value.copy(
                    suppliesLoading = false,
                    suppliesLoadingError = e.message + e.stackTraceToString()
                )
            }
        }
    }

    private fun uploadSupplies(){
        _state.value = state.value.copy(suppliesLoading = true, suppliesLoadingError = null)

        viewModelScope.launch {
            try {
                networkRespository.saveMySupplies(state.value.supplies.map {
                    NetworkMapper.buildFrom(
                        it
                    )
                })
                _state.value = state.value.copy(suppliesLoading = false, suppliesLoadingError = null)
            }catch (e: Exception){
                _state.value = state.value.copy(suppliesLoading = false, suppliesLoadingError = e.message + e.stackTraceToString())
            }
        }
    }

    fun clearSuppliesLoadingError(){
        _state.value = state.value.copy(suppliesLoadingError = null)
    }

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.storeSessionId("")
            ramRepository.storeAllSupplies(listOf())
            dbRepository.clearDB()
            _state.value = MainState.DEFAULT
        }
    }
}