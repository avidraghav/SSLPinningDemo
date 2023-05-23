package com.raghav.sslpinningdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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

    private lateinit var demoApi: DemoApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        builder = OkHttpClient()
            .newBuilder()
            .addInterceptor(interceptor)
            .build()

        demoApi =
            Retrofit.Builder()
                .baseUrl("https://192.168.1.23:8443")
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder)
                .build()
                .create(DemoApi::class.java)

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
        var requestInProgress by remember { mutableStateOf(false) }
        var response by remember { mutableStateOf("") }

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = response)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { requestInProgress = true }) {
                if (!requestInProgress) {
                    Text(text = "Make Api Call")
                } else {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }

        if (requestInProgress) {
            LaunchedEffect(key1 = true) {
                delay(2_000)
                response = demoApi.getDemoResponse().body()?.title.toString()
                requestInProgress = false
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        SSLPinningDemoTheme {
            App()
        }
    }
}
