package pl.kolejkapiosenek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.kolejkapiosenek.ui.PlaylistTheme
import pl.kolejkapiosenek.ui.QueueScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistTheme {
                QueueScreen()
            }
        }
    }
}
