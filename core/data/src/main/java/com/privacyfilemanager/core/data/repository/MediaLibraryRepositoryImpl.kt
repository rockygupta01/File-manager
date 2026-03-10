package com.privacyfilemanager.core.data.repository

import android.content.Context
import android.provider.MediaStore
import com.privacyfilemanager.core.common.util.FileCategory
import com.privacyfilemanager.core.domain.model.FileItem
import com.privacyfilemanager.core.domain.repository.MediaLibraryRepository
import com.privacyfilemanager.core.domain.repository.MediaSortOption
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class MediaLibraryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaLibraryRepository {

    override fun getAudioFiles(sortBy: MediaSortOption): Flow<List<FileItem>> = queryMediaContent(
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        sortOption = sortBy,
        category = FileCategory.AUDIO
    )

    override fun getVideoFiles(sortBy: MediaSortOption): Flow<List<FileItem>> = queryMediaContent(
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        sortOption = sortBy,
        category = FileCategory.VIDEO
    )

    override fun getRecentlyPlayedMedia(): Flow<List<FileItem>> = flow {
        // We will store paths in Room or DataStore. For now, we return empty structure.
        emit(emptyList<FileItem>()) // Hook up Recents Dao
    }.flowOn(Dispatchers.IO)

    override fun getLargestMediaFiles(): Flow<List<FileItem>> = flow {
        // Collect top largest media by querying both video and audio
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.MIME_TYPE
        )
        val files = mutableListOf<FileItem>()

        val uris = listOf(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        val sortOrder = "${MediaStore.MediaColumns.SIZE} DESC"

        for (uri in uris) {
            resolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
                val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

                var count = 0
                while (cursor.moveToNext() && count < 50) { // Limit top 50
                    val path = cursor.getString(dataColumn)
                    val file = File(path)
                    if (file.exists()) {
                        files.add(
                            FileItem(
                                name = cursor.getString(nameColumn) ?: file.name,
                                path = path,
                                isDirectory = false,
                                size = cursor.getLong(sizeColumn),
                                lastModified = cursor.getLong(dateColumn) * 1000,
                                mimeType = cursor.getString(mimeColumn) ?: "*/*",
                                category = if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) FileCategory.AUDIO else FileCategory.VIDEO
                            )
                        )
                    }
                    count++
                }
            }
        }
        
        files.sortByDescending { it.size }
        emit(files.take(50))
    }.flowOn(Dispatchers.IO)

    override suspend fun markMediaAsPlayed(path: String) {
        // TODO: Save to Room Dao
    }

    private fun queryMediaContent(
        uri: android.net.Uri,
        sortOption: MediaSortOption,
        category: FileCategory
    ): Flow<List<FileItem>> = flow {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DURATION
        )

        val sortOrder = when (sortOption) {
            MediaSortOption.NAME -> "${MediaStore.MediaColumns.DISPLAY_NAME} ASC"
            MediaSortOption.SIZE -> "${MediaStore.MediaColumns.SIZE} DESC"
            MediaSortOption.DATE_MODIFIED -> "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
            MediaSortOption.DURATION -> "${MediaStore.MediaColumns.DURATION} DESC"
        }

        val items = mutableListOf<FileItem>()

        resolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn)
                if (path == null) continue
                val file = File(path)
                if (file.exists()) {
                    items.add(
                        FileItem(
                            name = cursor.getString(nameColumn) ?: file.name,
                            path = path,
                            isDirectory = false,
                            size = cursor.getLong(sizeColumn),
                            lastModified = cursor.getLong(dateColumn) * 1000,
                            mimeType = cursor.getString(mimeColumn) ?: "*/*",
                            category = category
                        )
                    )
                }
            }
        }
        emit(items)
    }.flowOn(Dispatchers.IO)
}
