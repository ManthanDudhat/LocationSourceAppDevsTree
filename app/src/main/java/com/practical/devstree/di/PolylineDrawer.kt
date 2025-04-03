package com.practical.devstree.di

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PolylineDrawer @Inject constructor(val context: Context) {
    fun downloadTaskExecute(urlString: String, drawPolyLine: (jObject: JSONObject) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                withContext(Dispatchers.IO) {
                    URL(urlString).openStream()
                }.use { stream ->
                    BufferedReader(InputStreamReader(stream)).use { reader ->
                        val builder = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            builder.appendLine(line)
                        }
                        builder.toString()
                    }
                }
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
                "" // Handle exception and return empty string
            }

            withContext(Dispatchers.Main) {
                // Handle UI updates on the main thread
                if (result.isNotEmpty()) {
                    val jObject = JSONObject(result)
                    drawPolyLine.invoke(jObject)
                } else {
                    Log.e("DownloadTask", "DownloadTask: Empty result")
                }
            }
        }
    }
}