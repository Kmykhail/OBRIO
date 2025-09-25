package com.kote.obrio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kote.obrio.data.cache.ImageMemoryCache
import com.kote.obrio.data.navigation.Navigraph
import com.kote.obrio.ui.theme.ObrioTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        enableEdgeToEdge()

        setContent {
            ObrioTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigraph(
                        navController = rememberNavController(),
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImageMemoryCache.clear()
    }
}