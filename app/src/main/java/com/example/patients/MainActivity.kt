package com.example.patients

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.patients.data.model.Patient
import com.example.patients.ui.consultations.ConsultationsScreen
import com.example.patients.ui.patients.PatientsScreen
import com.example.patients.ui.theme.PatientsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PatientsTheme {
                var selectedPatientForConsultations by remember { mutableStateOf<Patient?>(null) }

                if (selectedPatientForConsultations == null) {
                    PatientsScreen(
                        onNavigateToConsultations = { patient ->
                            selectedPatientForConsultations = patient
                        }
                    )
                } else {
                    ConsultationsScreen(
                        patientId = selectedPatientForConsultations!!.id,
                        patientNom = "${selectedPatientForConsultations!!.prenom} ${selectedPatientForConsultations!!.nom}",
                        onBack = { selectedPatientForConsultations = null }
                    )
                }
            }
        }
    }
}