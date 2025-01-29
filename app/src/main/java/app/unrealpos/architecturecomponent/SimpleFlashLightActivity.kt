package  app.unrealpos.architecturecomponent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.hardware.camera2.CameraManager
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewModelScope

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashlightApp()
        }
    }
}

@Composable
fun FlashlightApp() {
    val context = LocalContext.current
    val resourceFlashOff= painterResource(R.drawable.flash_off)
    val resourceFlashOn= painterResource(R.drawable.flash_on)

    val viewModel: FlashlightViewModel = viewModel(
        factory = FlashlightViewModelFactory(context)

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {

            Box (

                modifier = Modifier.fillMaxSize()
            ){
                Image(
                    painter = if (flashlightState.value)  resourceFlashOn else resourceFlashOff,
                    contentDescription = null, // Background image doesn't need a content description
                     modifier = Modifier
                        .clickable {
                            // Toggle flashlight state
                            viewModel.toggleFlashlight()
                        }.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                ) 
            }
        }
    }
}

class FlashlightViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlashlightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlashlightViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class FlashlightViewModel(private val appContext: Context) : ViewModel() {
    private val _flashlightState = MutableStateFlow(false)
    val flashlightState: StateFlow<Boolean> = _flashlightState

    init {
        turnOnFlashlight()
    }

    private fun getCameraManager(): CameraManager? {
        return appContext.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    }

    private fun getCameraId(cameraManager: CameraManager?): String? {
        return try {
            cameraManager?.cameraIdList?.firstOrNull()
        } catch (e: Exception) {
            Log.e("FlashlightViewModel", "Error getting camera ID", e)
            null
        }
    }

    private fun updateFlashlightState(cameraId: String?, newState: Boolean) {
        if (cameraId == null) {
            Toast.makeText(appContext, "No camera found for flashlight", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                val cameraManager = getCameraManager()
                cameraManager?.setTorchMode(cameraId, newState)
                _flashlightState.value = newState
            } catch (e: Exception) {
                Toast.makeText(appContext, "Failed to update flashlight state", Toast.LENGTH_SHORT).show()
                Log.e("FlashlightViewModel", "Error updating flashlight state", e)
            }
        }
    }

    fun toggleFlashlight() {
        val cameraManager = getCameraManager()
        val cameraId = getCameraId(cameraManager)
        val newState = !_flashlightState.value
        updateFlashlightState(cameraId, newState)
    }

    fun turnOnFlashlight() {
        if (!_flashlightState.value) {
            val cameraManager = getCameraManager()
            val cameraId = getCameraId(cameraManager)
            updateFlashlightState(cameraId, true)
        }
    }

    fun turnOffFlashlight() {
        if (_flashlightState.value) {
            val cameraManager = getCameraManager()
            val cameraId = getCameraId(cameraManager)
            updateFlashlightState(cameraId, false)
        }
    }
}
