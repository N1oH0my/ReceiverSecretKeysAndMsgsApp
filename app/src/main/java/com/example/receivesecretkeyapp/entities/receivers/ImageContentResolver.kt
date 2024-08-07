package com.example.receivesecretkeyapp.entities.receivers

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class ImageContentResolver(private val context: Context) {

    private val contentResolver: ContentResolver = context.contentResolver

    companion object {
        private const val AUTHORITY = "dev.surf.android.images.provider"
        private const val PATH_STRING = "image"
        private const val TEXT_ID = 1
    }

    @SuppressLint("Range")
    fun queryImage(): Uri? {
        val uri = Uri.parse("content://$AUTHORITY/$PATH_STRING")

        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        var imageUri: Uri? = null

        cursor?.use {
            if (it.moveToFirst()) {
                val uriString = it.getString(it.getColumnIndex(PATH_STRING))
                imageUri = Uri.parse(uriString)
            }
        }
        return imageUri
    }

}