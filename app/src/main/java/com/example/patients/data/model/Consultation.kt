package com.example.patients.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Consultation(
    val id: String,
    val patient: PatientInfo, // Contains populated patient details or just string ID depending on response
    val date: String,
    val motif: String,
    val traitement: String? = null,
    val medecin: String,
    val notes: String? = null,
    val statut: String = "En attente"
)

// Helper to handle populated patient info or string ID safely
@Serializable
data class PatientInfo(
    val id: String,
    val nom: String,
    val prenom: String
)

@Serializable
data class ConsultationRequest(
    val patient: String, // Patient ID
    val date: String,
    val motif: String,
    val traitement: String? = null,
    val medecin: String,
    val notes: String? = null,
    val statut: String = "En attente"
)