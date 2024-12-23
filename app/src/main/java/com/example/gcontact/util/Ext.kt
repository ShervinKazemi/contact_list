package com.example.gcontact.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import java.io.File

// Global exception handler for coroutines
val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.v("error", "Error -> ${throwable.message}")
}

/**
 * Converts a drawable resource to a URI string.
 *
 * @param context The application context for accessing resources.
 * @param drawableResId The resource ID of the drawable to be converted.
 * @return A string representing the URI of the saved image, or an empty string in case of failure.
 */
fun toUri(context: Context, drawableResId: Int): String {
    // Load the drawable resource
    val drawable = context.getDrawable(drawableResId) ?: return ""

    // Convert the drawable to a Bitmap
    val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return ""

    // Create a temporary file in the cache directory
    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.png")
    tempFile.outputStream().use { outputStream ->
        // Save the Bitmap to the file in PNG format
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }

    // Convert the file path to a URI and return it as a string
    return Uri.fromFile(tempFile).toString()
}
