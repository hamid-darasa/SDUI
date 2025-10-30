package com.sample.sdui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.sample.sdui.ui.sdui.property.readJsonObjectFromAssets
import com.sample.sdui.ui.sdui.uiBuilder.DynamicJsonUI
import com.sample.sdui.ui.theme.SDUITheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private val viewModel = MainViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SDUITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val jsonFile: String = readJsonObjectFromAssets("test.json")
                    val root = remember { JSONObject(jsonFile) }

                    LaunchedEffect(Unit) {
                        viewModel.registerJsonTreeAsync(root)
                    }

                    if (!viewModel.isJsonLoaded) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Log.d("viewmoddslfldfld:", "${viewModel.jsonState["component20"]}")
                        DynamicJsonUI(jsonFile, viewModel) { logId, actionId, url ->
                            when {
                                url == "div-screen://close" -> {
                                    println("closessssssss")
                                }

                                url.startsWith("navigate://home") -> {
                                    val uri = url.toUri()
                                    // Some param for test
                                    val isAlive =
                                        uri.getQueryParameter("isAlive")?.toBoolean() ?: true

                                    println("Navigating to home. isAlive = $isAlive")
                                }

                                else -> {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SDUITheme {
        Greeting("Android")
    }
}