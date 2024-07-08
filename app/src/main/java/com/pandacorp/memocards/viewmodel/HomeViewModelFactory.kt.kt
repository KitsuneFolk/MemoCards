package com.pandacorp.memocards.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.pandacorp.memocards.database.CardRepository

class HomeViewModelFactory(private val repository: CardRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}