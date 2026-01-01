package com.example.smart_expense_tracker.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AiViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
