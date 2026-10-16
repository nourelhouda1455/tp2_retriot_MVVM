package com.example.patients.ui.consultations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.patients.data.model.ConsultationRequest
import com.example.patients.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConsultationViewModel(
    private val repository: PatientRepository,
    private val patientId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConsultationsUiState>(ConsultationsUiState.Loading)
    val uiState: StateFlow<ConsultationsUiState> = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    init {
        loadConsultations()
    }

    fun loadConsultations() {
        _uiState.value = ConsultationsUiState.Loading
        viewModelScope.launch {
            repository.getConsultations(patientId)
                .onSuccess { consultations ->
                    _uiState.value = ConsultationsUiState.Success(consultations)
                }
                .onFailure { e ->
                    _uiState.value = ConsultationsUiState.Error(e.message ?: "Erreur inconnue")
                }
        }
    }

    fun addConsultation(request: ConsultationRequest) {
        viewModelScope.launch {
            repository.createConsultation(request)
                .onSuccess { consultation ->
                    _uiState.update { currentState ->
                        if (currentState is ConsultationsUiState.Success) {
                            currentState.copy(consultations = listOf(consultation) + currentState.consultations)
                        } else {
                            currentState
                        }
                    }
                    _messages.emit("Consultation ajoutée avec succès")
                }
                .onFailure { e ->
                    _messages.emit(e.message ?: "Ajout impossible")
                }
        }
    }

    companion object {
        fun provideFactory(
            repository: PatientRepository,
            patientId: String
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ConsultationViewModel(repository, patientId)
            }
        }
    }
}