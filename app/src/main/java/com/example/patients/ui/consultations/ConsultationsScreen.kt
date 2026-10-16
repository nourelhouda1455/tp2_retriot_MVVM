package com.example.patients.ui.consultations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.patients.PatientsApplication
import com.example.patients.data.model.Consultation
import com.example.patients.ui.patients.ErrorContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationsScreen(
    patientId: String,
    patientNom: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PatientsApplication
    val repository = app.container.patientRepository

    val viewModel: ConsultationViewModel = viewModel(
        factory = ConsultationViewModel.provideFactory(repository, patientId)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messages.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consultations : $patientNom") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Fixed deprecation warning by using AutoMirrored
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadConsultations() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ConsultationsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ConsultationsUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadConsultations() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ConsultationsUiState.Success -> {
                    ConsultationList(consultations = state.consultations)
                }
            }
        }
    }
}

@Composable
fun ConsultationList(consultations: List<Consultation>) {
    if (consultations.isEmpty()) {
        Text("Aucune consultation pour ce patient", modifier = Modifier.padding(24.dp))
    } else {
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(consultations, key = { it.id }) { consultation ->
                ConsultationCard(consultation = consultation)
            }
        }
    }
}

@Composable
fun ConsultationCard(consultation: Consultation) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Date : ${consultation.date}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Motif : ${consultation.motif}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Médecin : ${consultation.medecin}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Statut : ${consultation.statut}", style = MaterialTheme.typography.bodySmall)
        }
    }
}