package br.com.anderson.thaloztest.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.system.measureTimeMillis


sealed class SpeedTestResult {
    data object Loading : SpeedTestResult()
    data class Success(val downloadSpeed: Double, val uploadSpeed: Double) : SpeedTestResult()
    data class Failure(val errorMessage: String) : SpeedTestResult()
}

class SpeedTest(private val client: OkHttpClient = OkHttpClient()) {

    companion object {
        const val UPLOAD_URL = "http://postman-echo.com/post"
        const val DOWNLOAD_URL = "http://speedtest.tele2.net/1MB.zip"
    }

    private var cachedJson: String? = null
    private val jsonMaxSize = 2 * 1024 * 1024


    private fun generate2MBJson(): String {
        cachedJson?.let {
            return it
        }

        val sampleJson = JSONObject().apply {
            put("id", 1)
            put("name", "Sample Name")
            put("description", "This is a sample description for the JSON object.")
        }

        val stringBuilder = StringBuilder()

        while (stringBuilder.length < jsonMaxSize) {
            stringBuilder.append(sampleJson.toString())
        }

        return stringBuilder.toString().also {
            cachedJson = it
        }
    }

    private suspend fun testDownloadSpeed(testUrl: String): Double = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(testUrl).build()
            val speed: Double
            var dataSize: Int

            val elapsedTime = measureTimeMillis {
                client.newCall(request).execute().use { response ->

                    if (!response.isSuccessful) {
                        throw Exception("Download test failed with status code: ${response.code}")
                    }

                    dataSize = response.body?.bytes()?.size ?: 0
                }
            }

            speed = if (elapsedTime > 0) {
                (dataSize * 8 / 1_000_000.0) / (elapsedTime / 1000.0)
            } else {
                0.0
            }

            speed
        } catch (e: Exception) {
            throw Exception("Download Speed Test Failed: ${e.message}")
        }
    }

    private suspend fun testUploadSpeed(testUrl: String): Double = withContext(Dispatchers.IO) {
        try {
            val generate2MBJson = generate2MBJson()
            val requestBody = generate2MBJson.toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(testUrl)
                .post(requestBody)
                .build()

            val elapsedTime: Long = measureTimeMillis {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw Exception("Upload test failed with status code: ${response.code}")
                    }
                }
            }

            val sizeInBits = generate2MBJson.toByteArray().size * 8
            val timeInSeconds = elapsedTime / 1000.0

            val speed = if (timeInSeconds > 0) sizeInBits / (1_000_000 * timeInSeconds) else 0.0

            speed
        } catch (e: Exception) {
            throw Exception("Upload failed with status code: ${e.message}")
        }
    }

    fun getConnectionSpeeds(
        downloadTestUrl: String,
        uploadTestUrl: String
    ): Flow<SpeedTestResult> = callbackFlow {
        try {
            send(SpeedTestResult.Loading)

            val downloadSpeedDeferred = async { testDownloadSpeed(downloadTestUrl) }
            val uploadSpeedDeferred = async { testUploadSpeed(uploadTestUrl) }

            val results = awaitAll(downloadSpeedDeferred, uploadSpeedDeferred)

            send(SpeedTestResult.Success(results[0], results[1]))

        } catch (e: Exception) {
            send(SpeedTestResult.Failure("Connection speed test failed: ${e.message}"))
        } finally {
            close()
        }
    }
}
