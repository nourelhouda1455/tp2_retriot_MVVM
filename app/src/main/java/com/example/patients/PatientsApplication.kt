package com.example.patients

import android.app.Application
import com.example.patients.di.AppContainer

class PatientsApplication : Application() {
    val container: AppContainer by lazy {
        AppContainer()
    }
}