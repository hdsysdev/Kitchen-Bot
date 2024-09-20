package com.example.mlkitchenbot.ui.screens

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mlkitchenbot.camera.toBitmap
import com.example.mlkitchenbot.detection.DetectedObject
import com.example.mlkitchenbot.detection.ObjectDetectionManager
import com.example.mlkitchenbot.ui.views.DetectedObjectsOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalGetImage::class)
@Composable
fun ObjectDetectionScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val objectDetectionManager = remember { ObjectDetectionManager() }

    val screenWidth = remember { mutableIntStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableIntStateOf(context.resources.displayMetrics.heightPixels) }

    var detectedObjects by remember { mutableStateOf<List<DetectedObject>>(emptyList()) }
    val imageWidth = remember { mutableIntStateOf(480) }
    val imageHeight = remember { mutableIntStateOf(640) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { imageAnalysis ->
                            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                                imageWidth.intValue = imageProxy.width
                                imageHeight.intValue = imageProxy.height
                                Log.d("ObjectDetectionScreen", "image width: ${imageWidth.intValue}, height: ${imageHeight.intValue}")
                                val image = imageProxy.image
                                if (image != null) {
                                    val bitmap = image.toBitmap()
                                    lifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                                        detectedObjects = objectDetectionManager.detectObjects(bitmap)
                                    }
                                }
                                imageProxy.close()
                            }
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalyzer
                        )
                    } catch (exc: Exception) {
                        // TODO: Handle errors
                        throw exc
                    }
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        DetectedObjectsOverlay(detectedObjects, imageWidth.intValue, imageHeight.intValue, screenWidth.intValue, screenHeight.intValue)
    }
}