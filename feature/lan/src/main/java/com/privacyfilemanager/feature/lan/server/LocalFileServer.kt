package com.privacyfilemanager.feature.lan.server

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream

class LocalFileServer(
    port: Int,
    private val rootDir: File
) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri.removePrefix("/").let {
            java.net.URLDecoder.decode(it, "UTF-8")
        }

        return when {
            session.method == Method.GET && uri.isEmpty() -> serveDirectory(rootDir, "/")
            session.method == Method.GET -> {
                val file = File(rootDir, uri)
                when {
                    file.isDirectory -> serveDirectory(file, "/$uri")
                    file.isFile -> serveFile(file)
                    else -> newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
                }
            }
            else -> newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "Method Not Allowed")
        }
    }

    private fun serveDirectory(dir: File, path: String): Response {
        val sb = StringBuilder()
        sb.append("<!DOCTYPE html><html><head>")
        sb.append("<meta charset='UTF-8'>")
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1'>")
        sb.append("<title>File Manager - $path</title>")
        sb.append("<style>body{font-family:sans-serif;padding:16px;background:#121212;color:#fff}")
        sb.append("a{color:#BB86FC;text-decoration:none}a:hover{text-decoration:underline}")
        sb.append("li{padding:6px 0;border-bottom:1px solid #333}</style></head><body>")
        sb.append("<h2>📁 $path</h2><ul>")

        if (path != "/") {
            val parent = path.trimEnd('/').substringBeforeLast('/')
            sb.append("<li><a href='/${parent.removePrefix("/")}'>⬆ Parent</a></li>")
        }

        dir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))?.forEach { file ->
            val displayName = if (file.isDirectory) "📁 ${file.name}/" else "📄 ${file.name}"
            val link = if (path == "/") "/${file.name}" else "$path/${file.name}"
            sb.append("<li><a href='${java.net.URLEncoder.encode(link, "UTF-8").replace("%2F", "/").replace("+", "%20")}'>$displayName</a>")
            if (file.isFile) sb.append(" <span style='color:#aaa;font-size:12px'>(${formatSize(file.length())})</span>")
            sb.append("</li>")
        }
        sb.append("</ul></body></html>")
        return newFixedLengthResponse(Response.Status.OK, "text/html; charset=UTF-8", sb.toString())
    }

    private fun serveFile(file: File): Response {
        val mimeType = getMimeTypeForFile(file.name)
        val stream = FileInputStream(file)
        return newChunkedResponse(Response.Status.OK, mimeType, stream)
    }

    private fun formatSize(bytes: Long): String = when {
        bytes >= 1_073_741_824 -> "%.1f GB".format(bytes / 1_073_741_824.0)
        bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
        bytes >= 1024 -> "%.1f KB".format(bytes / 1024.0)
        else -> "$bytes B"
    }
}
