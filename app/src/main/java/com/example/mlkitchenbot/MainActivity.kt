package com.example.mlkitchenbot

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mlkitchenbot.detection.RequestCameraPermission
import com.example.mlkitchenbot.ui.screens.ObjectDetectionScreen
import com.example.mlkitchenbot.ui.theme.MLKitchenBotTheme
import com.example.mlkitchenbot.ui.views.PermissionRequestPlaceholder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MLKitchenBotTheme {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MainContent()
                    }
                }

            }
        }
    }
}


@Composable
fun MainContent() {
    var cameraPermissionGranted by remember { mutableStateOf(false) }

    RequestCameraPermission(
        onPermissionGranted = {
            cameraPermissionGranted = true
        }
    )

    if (cameraPermissionGranted) {
        ObjectDetectionScreen()
    } else {
        PermissionRequestPlaceholder(
            onRequestPermission = {
                cameraPermissionGranted = false
            }

        )
    }
}
