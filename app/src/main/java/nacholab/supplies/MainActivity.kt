package nacholab.supplies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import nacholab.supplies.ui.main.MainScreen
import nacholab.supplies.ui.theme.SuppliesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SuppliesTheme { MainScreen() } }
    }

}