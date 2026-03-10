package com.privacyfilemanager.feature.lan.server

import android.os.Environment
import android.provider.MediaStore
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream

class LocalFileServer(
    port: Int,
    private val rootDir: File,
    private val context: android.content.Context
) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri.removePrefix("/").let {
            java.net.URLDecoder.decode(it, "UTF-8")
        }
        return when {
            session.method == Method.GET -> {
                // BUG 1 FIX (v3): Prevent path traversal via prefix siblings by mandating exact match or separator
                // For API requests
                if (uri.startsWith("api/files")) {
                    val pathParam = session.parameters["path"]?.firstOrNull() ?: "/"
                    return handleApiFiles(pathParam)
                }
                
                if (uri.startsWith("api/file")) {
                    val pathParam = session.parameters["path"]?.firstOrNull() ?: return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Missing path param")
                    val file = validatePathOrAbsolute(pathParam) ?: return newFixedLengthResponse(Response.Status.FORBIDDEN, MIME_PLAINTEXT, "Forbidden")
                    return if (file.isFile) serveFile(file) else newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not a file")
                }

                if (uri.startsWith("api/recent")) {
                    return handleApiRecent()
                }

                if (uri.startsWith("api/search")) {
                    val query = session.parameters["q"]?.firstOrNull() ?: ""
                    return handleApiSearch(query)
                }

                if (uri.startsWith("api/pictures")) return handleApiPictures()
                if (uri.startsWith("api/videos")) return handleApiVideos()
                if (uri.startsWith("api/musics")) return handleApiMusics()
                if (uri.startsWith("api/documents")) return handleApiDocuments()

                // For Static Assets (Web App)
                if (uri.isEmpty() || uri == "index.html") {
                    return serveAsset("web/index.html", "text/html")
                }
                if (uri == "style.css") {
                    return serveAsset("web/style.css", "text/css")
                }
                if (uri == "app.js") {
                    return serveAsset("web/app.js", "application/javascript")
                }

                newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
            }
            else -> newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "Method Not Allowed")
        }
    }

    private fun validatePath(rawPath: String): File? {
        val safePath = rawPath.removePrefix("/")
        val file = File(rootDir, safePath)
        val rootPath = rootDir.canonicalPath
        val targetPath = file.canonicalPath
        
        if (targetPath != rootPath && !targetPath.startsWith("$rootPath${File.separator}")) {
            return null // Path traversal detected
        }
        return file
    }

    private fun validatePathOrAbsolute(rawPath: String): File? {
        // First try relative to rootDir
        var file = validatePath(rawPath)
        if (file != null && file.exists()) return file

        // If it was an absolute path from MediaStore (e.g., /storage/emulated/0/DCIM/...)
        val absFile = File(rawPath)
        val extRoot = Environment.getExternalStorageDirectory().canonicalPath
        if (absFile.isAbsolute && absFile.exists()) {
            val absCanonical = absFile.canonicalPath
            if (absCanonical == extRoot || absCanonical.startsWith("$extRoot${File.separator}")) {
                return absFile
            }
        }
        return null
    }

    private fun handleApiFiles(path: String): Response {
        val dir = validatePath(path) ?: return newFixedLengthResponse(Response.Status.FORBIDDEN, "application/json", "{\"error\":\"Forbidden\"}")
        
        if (!dir.exists() || !dir.isDirectory) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json", "{\"error\":\"Directory not found\"}")
        }

        val items = mutableListOf<String>()
        dir.listFiles()?.forEach { file ->
            val name = org.json.JSONObject.quote(file.name)
            val isDir = file.isDirectory
            val size = if (isDir) 0 else file.length()
            val itemCount = if (isDir) file.listFiles()?.size ?: 0 else 0
            
            // For regular API files, we don't need absolutePath because it builds relative from currentPath, 
            // but we optionally provide it so the frontend can use a unified object shape
            // (We'll just omit it, frontend falls back to relative building)
            items.add("{\"name\":$name,\"isDirectory\":$isDir,\"size\":$size,\"itemCount\":$itemCount}")
        }
        
        val jsonArray = "[${items.joinToString(",")}]"
        return newFixedLengthResponse(Response.Status.OK, "application/json", jsonArray)
    }

    private fun handleApiRecent(): Response {
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        return queryMediaStore(null, null, sortOrder, 50)
    }

    private fun handleApiSearch(query: String): Response {
        if (query.isBlank()) return newFixedLengthResponse(Response.Status.OK, "application/json", "[]")
        
        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val sortOrder = "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"
        return queryMediaStore(selection, selectionArgs, sortOrder, 100)
    }

    private fun handleApiPictures(): Response {
        val selection = buildExtensionSelection(listOf("jpg", "jpeg", "png", "webp", "gif", "bmp"))
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        return queryMediaStore(selection, null, sortOrder, 200)
    }

    private fun handleApiVideos(): Response {
        val selection = buildExtensionSelection(listOf("mp4", "mkv", "avi", "mov", "3gp", "webm"))
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        return queryMediaStore(selection, null, sortOrder, 200)
    }

    private fun handleApiMusics(): Response {
        val selection = buildExtensionSelection(listOf("mp3", "wav", "aac", "m4a", "flac", "ogg"))
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        return queryMediaStore(selection, null, sortOrder, 200)
    }

    private fun handleApiDocuments(): Response {
        val selection = buildExtensionSelection(listOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"))
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        return queryMediaStore(selection, null, sortOrder, 200)
    }

    private fun buildExtensionSelection(extensions: List<String>): String {
        return extensions.joinToString(" OR ") { "${MediaStore.Files.FileColumns.DATA} LIKE '%.${it}'" }
    }

    private fun queryMediaStore(selection: String?, selectionArgs: Array<String>?, sortOrder: String?, limit: Int): Response {
        val items = mutableListOf<String>()
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE
        )

        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

                while (cursor.moveToNext()) {
                    val filePath = cursor.getString(dataCol) ?: continue
                    val fileName = cursor.getString(nameCol) ?: File(filePath).name
                    val fileSize = cursor.getLong(sizeCol)
                    
                    val nameQ = org.json.JSONObject.quote(fileName)
                    val absolutePathQ = org.json.JSONObject.quote(filePath)
                    
                    items.add("{\"name\":$nameQ,\"isDirectory\":false,\"size\":$fileSize,\"absolutePath\":$absolutePathQ}")
                    
                    if (items.size >= limit) {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"${e.message}\"}")
        }

        val jsonArray = "[${items.joinToString(",")}]"
        return newFixedLengthResponse(Response.Status.OK, "application/json", jsonArray)
    }

    private fun serveAsset(assetPath: String, mime: String): Response {
        return try {
            val stream = context.assets.open(assetPath)
            newChunkedResponse(Response.Status.OK, mime, stream)
        } catch (e: Exception) {
            newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Asset Not Found")
        }
    }

    private fun serveFile(file: File): Response {
        val mimeType = getMimeTypeForFile(file.name)
        val stream = FileInputStream(file)
        val res = newChunkedResponse(Response.Status.OK, mimeType, stream)
        val encodedName = java.net.URLEncoder.encode(file.name, "UTF-8").replace("+", "%20")

        // Previewable types: let browser display them inline
        val isPreviewable = mimeType.startsWith("image/") ||
                mimeType.startsWith("video/") ||
                mimeType.startsWith("audio/") ||
                mimeType == "application/pdf" ||
                mimeType.startsWith("text/")

        val disposition = if (isPreviewable) "inline" else "attachment"
        res.addHeader("Content-Disposition", "$disposition; filename=\"${file.name}\"; filename*=UTF-8''$encodedName")
        return res
    }

    private fun formatSize(bytes: Long): String = when {
        bytes >= 1_073_741_824 -> "%.1f GB".format(bytes / 1_073_741_824.0)
        bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
        bytes >= 1024 -> "%.1f KB".format(bytes / 1024.0)
        else -> "$bytes B"
    }
}
