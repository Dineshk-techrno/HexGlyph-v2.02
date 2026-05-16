package com.hexglyph.a1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hexglyph.a1.core.ui.theme.HexGlyphTheme
import com.hexglyph.a1.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HexGlyphTheme {
                AppNavGraph()
            }
        }
    }
}
