package com.unasp.studantaddress.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unasp.studantaddress.models.Student
import com.unasp.studantaddress.repository.StudentRepository
import com.unasp.studantaddress.worker.SyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentListUiState(
    val students: List<Student> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class StudentListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepository(application)

    private val _uiState = MutableStateFlow(StudentListUiState())
    val uiState: StateFlow<StudentListUiState> = _uiState.asStateFlow()

    init {
        observeStudents()
        viewModelScope.launch {
            SyncWorker.syncNow(application)
            // aguarda um momento e recarrega do banco
            kotlinx.coroutines.delay(1500)
            repository.refreshFromApi()
        }
    }

    // Observa o Flow do Room reativamente
    private fun observeStudents() {
        viewModelScope.launch {
            repository.findAll().collect { students ->
                _uiState.value = _uiState.value.copy(
                    students = students,
                    isLoading = false
                )
            }
        }
    }

    fun removeStudent(localId: Long) {
        viewModelScope.launch {
            try {
                repository.delete(localId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao remover estudante"
                )
            }
        }
    }
}