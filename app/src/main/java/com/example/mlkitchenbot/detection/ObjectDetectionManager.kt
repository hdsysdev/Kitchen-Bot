package com.example.mlkitchenbot.detection

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ObjectDetectionManager {
    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .build()

    private val objectDetector = ObjectDetection.getClient(options)

    suspend fun detectObjects(bitmap: Bitmap): List<DetectedObject> = suspendCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                val mappedObjects = detectedObjects.map { mlKitObject ->
                    DetectedObject(
                        boundingBox = mlKitObject.boundingBox,
                        trackingId = mlKitObject.trackingId,
                        labels = mlKitObject.labels.map { label ->
                            Label(text = label.text, confidence = label.confidence)
                        }
                    )
                }
                continuation.resume(mappedObjects)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}

data class DetectedObject(
    val boundingBox: android.graphics.Rect,
    val trackingId: Int?,
    val labels: List<Label>
)

data class Label(
    val text: String,
    val confidence: Float
)