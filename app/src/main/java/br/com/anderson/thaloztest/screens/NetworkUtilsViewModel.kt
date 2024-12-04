package br.com.anderson.thaloztest.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.anderson.thaloztest.utils.SpeedTest
import br.com.anderson.thaloztest.utils.SpeedTestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NetworkUtilsViewModel(
    private val speedTest: SpeedTest = SpeedTest()
) : ViewModel() {

    private val _speedTestInfo = MutableStateFlow("")
    val speedTestInfo: StateFlow<String> = _speedTestInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun testConnectionSpeed() {
        viewModelScope.launch(Dispatchers.IO) {
            speedTest.getConnectionSpeeds(
                downloadTestUrl = SpeedTest.DOWNLOAD_URL,
                uploadTestUrl = SpeedTest.UPLOAD_URL
            ).collect {
                when (it) {
                    is SpeedTestResult.Loading -> {
                        _isLoading.value = true
                        _speedTestInfo.value = "Loading..."
                    }
                    is SpeedTestResult.Failure -> {
                        _isLoading.value = false
                        _speedTestInfo.value =
                            "Failed to fetch data: ${it.errorMessage}"
                    }

                    is SpeedTestResult.Success -> {
                        _isLoading.value = false
                        val downloadFormatted = "Download Speed: %.2f Mbps".format(it.downloadSpeed)
                        val uploadFormatted = "Upload Speed: %.2f Mbps".format(it.uploadSpeed)
                        _speedTestInfo.value = "$downloadFormatted\n$uploadFormatted"
                    }
                }
            }
        }
    }
}
