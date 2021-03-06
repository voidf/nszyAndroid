package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ImageReader
import android.net.sip.SipErrorCode
import android.net.sip.SipErrorCode.TIME_OUT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.runBlocking
import java.util.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


val ktorClient = HttpClient(Android){
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })

        engine {
            connectTimeout = SipErrorCode.TIME_OUT
            socketTimeout = SipErrorCode.TIME_OUT
        }
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
//                Log.v("Logger Ktor =>", message)
            }

        }
        level = LogLevel.ALL
    }

    install(ResponseObserver) {
        onResponse { response ->
//            Log.d("HTTP status:", "${response.status.value}")
        }
    }

    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

val coroutineScope = CoroutineScope(ktorClient.coroutineContext)