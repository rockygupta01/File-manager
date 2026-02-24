package com.privacyfilemanager.feature.archive.domain

import com.privacyfilemanager.core.common.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.EncryptionMethod
import net.lingala.zip4j.progress.ProgressMonitor
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveManager @Inject constructor() {

    fun compressFiles(
        sourcePaths: List<String>,
        destinationZipPath: String,
        password: String? = null
    ): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val destFile = File(destinationZipPath)
            if (destFile.exists()) {
                destFile.delete()
            }
            
            val zipFile = if (password.isNullOrEmpty()) {
                ZipFile(destFile)
            } else {
                ZipFile(destFile, password.toCharArray())
            }
            
            val parameters = ZipParameters()
            if (!password.isNullOrEmpty()) {
                parameters.isEncryptFiles = true
                parameters.encryptionMethod = EncryptionMethod.AES
                parameters.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
            }
            
            zipFile.isRunInThread = true
            val progressMonitor = zipFile.progressMonitor

            val filesToAdd = sourcePaths.map { File(it) }.filter { it.isFile }
            val foldersToAdd = sourcePaths.map { File(it) }.filter { it.isDirectory }
            
            if (filesToAdd.isNotEmpty()) {
                zipFile.addFiles(filesToAdd, parameters)
            }
            foldersToAdd.forEach { folder ->
                zipFile.addFolder(folder, parameters)
            }

            // Wait for completion if running in thread
            while (progressMonitor.state == ProgressMonitor.State.BUSY) {
                // We could emit progress here: emit(Result.Loading(progressMonitor.percentDone))
                kotlinx.coroutines.delay(100)
            }
            
            if (progressMonitor.result == ProgressMonitor.Result.SUCCESS) {
                emit(Result.Success(destinationZipPath))
            } else if (progressMonitor.result == ProgressMonitor.Result.ERROR) {
                emit(Result.Error(progressMonitor.exception?.message ?: "Compression failed"))
            } else {
                emit(Result.Error("Compression canceled"))
            }

        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown compression error"))
        }
    }.flowOn(Dispatchers.IO)

    fun extractArchive(
        zipPath: String,
        destinationFolder: String,
        password: String? = null
    ): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val destDir = File(destinationFolder)
            if (!destDir.exists()) destDir.mkdirs()

            val zipFile = if (password.isNullOrEmpty()) {
                ZipFile(zipPath)
            } else {
                val zf = ZipFile(zipPath)
                if (zf.isEncrypted) zf.setPassword(password.toCharArray())
                zf
            }

            if (!zipFile.isValidZipFile) {
                emit(Result.Error("Invalid or corrupted ZIP file"))
                return@flow
            }

            zipFile.isRunInThread = true
            val progressMonitor = zipFile.progressMonitor
            
            zipFile.extractAll(destinationFolder)

            while (progressMonitor.state == ProgressMonitor.State.BUSY) {
                kotlinx.coroutines.delay(100)
            }

            if (progressMonitor.result == ProgressMonitor.Result.SUCCESS) {
                emit(Result.Success(destinationFolder))
            } else if (progressMonitor.result == ProgressMonitor.Result.ERROR) {
                emit(Result.Error(progressMonitor.exception?.message ?: "Extraction failed. Incorrect password?"))
            } else {
                emit(Result.Error("Extraction canceled"))
            }

        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown extraction error"))
        }
    }.flowOn(Dispatchers.IO)
}
