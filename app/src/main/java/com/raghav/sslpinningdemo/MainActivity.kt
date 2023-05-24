package com.raghav.sslpinningdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raghav.sslpinningdemo.ui.DemoApi
import com.raghav.sslpinningdemo.ui.theme.SSLPinningDemoTheme
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var builder: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        builder = OkHttpClient()
            .newBuilder()
            .addInterceptor(interceptor)
            .build()

        setContent {
            SSLPinningDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    App()
                }
            }
        }
    }

    @Composable
    fun App(modifier: Modifier = Modifier) {
        var apiRequestInProgress by remember { mutableStateOf(false) }
        var response by remember { mutableStateOf("") }
        var httpsEnabled by remember { mutableStateOf(true) }

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = response, modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { apiRequestInProgress = true }) {
                if (!apiRequestInProgress) {
                    Text(text = "Make Api Call")
                } else {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Switch(
                checked = httpsEnabled,
                onCheckedChange = { httpsEnabled = it },
                enabled = !apiRequestInProgress
            )
            Text(
                text = "HTTPS enabled: ${if (httpsEnabled) "true" else "false"}",
                modifier = Modifier.padding(8.dp)
            )
        }

        if (apiRequestInProgress) {
            LaunchedEffect(key1 = true) {
                delay(2_000)
                response = try {
                    createApi(httpsEnabled).getDemoResponse().body()?.title.toString()
                } catch (e: Exception) {
                    e.message.toString()
                }
                apiRequestInProgress = false
            }
        }
    }

    private fun getBaseUrl(isHttpsEnabled: Boolean): String {
        return if (isHttpsEnabled) "https://192.168.1.23:8443" else "http://192.168.1.23:8080"
    }

    private fun createApi(isHttpsEnabled: Boolean): DemoApi {
        return Retrofit.Builder()
            .baseUrl(getBaseUrl(isHttpsEnabled))
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder)
            .build()
            .create(DemoApi::class.java)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        SSLPinningDemoTheme {
            App()
        }
    }
}
