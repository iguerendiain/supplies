package nacholab.supplies.ui.main.supplies

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import nacholab.supplies.R
import nacholab.supplies.ui.EditingMode
import nacholab.supplies.ui.MainViewModel
import nacholab.supplies.ui.SortMode
import nacholab.supplies.ui.main.OptionsDialog
import nacholab.supplies.utils.copyMediaStoreUriToFile
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@Composable
fun SuppliesScreen(mainViewModel: MainViewModel = koinViewModel()){
    val app = LocalContext.current.applicationContext as Application
    val activity = LocalActivity.current

    val openFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = File(app.filesDir, "db.json")
            app.copyMediaStoreUriToFile(uri, file)
            mainViewModel.importDBFromFile(file)
        }
    }

    val state = mainViewModel.state.value
    var addEditDialogShown by remember { mutableStateOf(false) }
    var settingsShown by remember { mutableStateOf(false) }

    SupplyList(
        consumables = state.supplies,
        onReduceCurrentStock = { mainViewModel.reduceCurrentStock(it) },
        onIncreaseCurrentStock = { mainViewModel.addCurrentStock(it) },
        onAddEditConsumable = {
            mainViewModel.setEditingConsumable(it)
            addEditDialogShown = true
        },
        consumablesByMarketLocation = state.suppliesByMarketLocation,
        consumablesByHomeLocation = state.suppliesByHomeLocation,
        onReduceRequiredStock = { mainViewModel.reduceRequiredStock(it) },
        onIncreaseRequiredStock = { mainViewModel.addRequiredStock(it) },
        onSetEditMode = {
            mainViewModel.setEditingMode(
                if (state.editingMode != EditingMode.CURRENT_STOCK) EditingMode.CURRENT_STOCK
                else EditingMode.NO_EDITING
            )
        },
        editingMode = state.editingMode,
        sortMode = state.sort,
        onSettings = { settingsShown = true },
        onNameListMode = { mainViewModel.setSortMode(SortMode.NAME) },
        onHomeLocationListMode = { mainViewModel.setSortMode(SortMode.HOME_LOCATION) },
        onMarketLocationListMode = { mainViewModel.setSortMode(SortMode.MARKET_LOCATION) },
    )

    if  (settingsShown) OptionsDialog(
        options = listOf(
            stringResource(R.string.supplies_screen_import),
            stringResource(R.string.supplies_screen_export),
        ),
        onSelectedOption = {
            when (it) {
                0 -> { openFileLauncher.launch("application/json") }
                1 -> { mainViewModel.exportDBTofile() }
            }
            settingsShown = false
        },
        onDismiss = { settingsShown = false }
    )

    if (addEditDialogShown) AddEditSupplyDialog(
        onDismiss = { addEditDialogShown = false },
        consumable = state.editingConsumable,
        onSave = { name, homeLocation, marketLocation, currentStock, requiredStock ->
            val consumableId = state.editingConsumable?.id
            if (consumableId!=null) mainViewModel.updateConsumable(
                consumableId,
                name,
                homeLocation,
                marketLocation,
                currentStock,
                requiredStock
            ) else mainViewModel.addConsumable(
                name,
                homeLocation,
                marketLocation,
                currentStock,
                requiredStock
            )

            mainViewModel.setEditingConsumable(null)
            addEditDialogShown = false
        },
        onDelete = {
            state.editingConsumable?.id?.let {
                mainViewModel.deleteConsumable(it)
                addEditDialogShown = false
            }
        }
    )

    if (state.exportDBToFile!=null){
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, state.exportDBToFile)
            type = "application/json"
        }

        activity?.startActivity(Intent.createChooser(shareIntent, "Export DB"))
        mainViewModel.exportDBToFileSignalClear()
    }
}