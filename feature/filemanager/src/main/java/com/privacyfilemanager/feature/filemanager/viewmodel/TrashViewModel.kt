package com.privacyfilemanager.feature.filemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyfilemanager.core.database.entity.TrashEntity
import com.privacyfilemanager.core.domain.repository.TrashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val trashRepository: TrashRepository
) : ViewModel() {

    val trashedFiles: StateFlow<List<TrashEntity>> = trashRepository.getAllTrashedFiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun restoreItem(id: Int) {
        viewModelScope.launch {
            trashRepository.restoreFromTrash(id)
        }
    }

    fun deleteItemPermanently(id: Int) {
        viewModelScope.launch {
            trashRepository.permanentlyDelete(id)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            trashRepository.emptyTrash()
        }
    }
}
