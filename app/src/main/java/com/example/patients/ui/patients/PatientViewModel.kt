package com.example.patients.ui.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.patients.PatientsApplication
import com.example.patients.data.model.Patient
import com.example.patients.data.model.PatientRequest
import com.example.patients.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PatientsUiState>(PatientsUiState.Loading)
    val uiState: StateFlow<PatientsUiState> = _uiState.asStateFlow()

    // Exercise 2: State for filtering by status
    private val _selectedStatut = MutableStateFlow<String?>(null)
    val selectedStatut: StateFlow<String?> = _selectedStatut.asStateFlow()

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    init {
        loadPatients()
    }

    fun loadPatients() {
        _uiState.value = PatientsUiState.Loading
        viewModelScope.launch {
            // Pass the current filter status to the repository
            repository.getPatients(_selectedStatut.value)
                .onSuccess { patients ->
                    _uiState.value = PatientsUiState.Success(patients)
                }
                .onFailure { error ->
                    _uiState.value = PatientsUiState.Error(
                        error.message ?: "Erreur inconnue"
                    )
                }
        }
    }

    // Exercise 2: Change filter and reload list
    fun setStatutFilter(statut: String?) {
        _selectedStatut.value = statut
        loadPatients()
    }

    fun addPatient(request: PatientRequest) {
        viewModelScope.launch {
            repository.createPatient(request)
                .onSuccess { patient ->
                    // Only add to the current list if it matches the active filter or if filter is null
                    if (_selectedStatut.value == null || patient.statut == _selectedStatut.value) {
                        updateList { it + patient }
                    }
                    _messages.emit("${patient.prenom} ${patient.nom} ajouté")
                }
                .onFailure { e ->
                    _messages.emit(e.message ?: "Ajout impossible")
                }
        }
    }

    // Exercise 1: Update patient function (PUT)
    fun updatePatient(id: String, request: PatientRequest) {
        viewModelScope.launch {
            repository.updatePatient(id, request)
                .onSuccess { updatedPatient ->
                    updateList { list ->
                        list.map { if (it.id == id) updatedPatient else it }
                    }
                    _messages.emit("${updatedPatient.prenom} ${updatedPatient.nom} modifié")
                }
                .onFailure { e ->
                    _messages.emit(e.message ?: "Modification impossible")
                }
        }
    }

    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            repository.deletePatient(patient.id)
                .onSuccess {
                    updateList { list -> list.filterNot { it.id == patient.id } }
                    _messages.emit("${patient.prenom} ${patient.nom} supprimé")
                }
                .onFailure { e ->
                    _messages.emit(e.message ?: "Suppression impossible")
                }
        }
    }

    private fun updateList(transform: (List<Patient>) -> List<Patient>) {
        _uiState.update { state ->
            if (state is PatientsUiState.Success) state.copy(patients = transform(state.patients)) else state
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as PatientsApplication
                PatientViewModel(app.container.patientRepository)
            }
        }
    }
}