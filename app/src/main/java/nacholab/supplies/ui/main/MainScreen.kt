package nacholab.supplies.ui.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nacholab.supplies.R
import nacholab.supplies.ui.common.FakeStatusBar
import nacholab.supplies.ui.common.MultiAnimatedSwitch
import nacholab.supplies.ui.main.shoppinglist.ShoppingListScreen
import nacholab.supplies.ui.main.supplies.SuppliesScreen
import nacholab.supplies.ui.theme.SuppliesTheme

@Composable
fun MainScreen(){

    var selectedSection by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        FakeStatusBar()

        Spacer(modifier = Modifier.size(12.dp))

        Box(
            modifier = Modifier.padding(horizontal = 48.dp)
        ){
            MultiAnimatedSwitch(
                tabs = listOf(
                    stringResource(R.string.main_screen_tabs_supplies),
                    stringResource(R.string.main_screen_tabs_shoppinglist)
                ),
                selectedOption = selectedSection,
                onTabSelected = { selectedSection = it }
            )
        }

        when (selectedSection){
            0 -> SuppliesScreen()
            1 -> ShoppingListScreen()
        }
    }

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview(){
    SuppliesTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ){
            MainScreen()
        }
    }
}