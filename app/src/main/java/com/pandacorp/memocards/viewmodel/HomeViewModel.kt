package com.pandacorp.memocards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.memocards.database.CardRepository
import com.pandacorp.memocards.database.CardStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CardRepository) : ViewModel() {
    private val _cardCounts = MutableStateFlow(CardCounts(0, 0, 0))
    val cardCounts: StateFlow<CardCounts> = _cardCounts

    init {
        viewModelScope.launch {
            combine(
                repository.getCardCountByStatus(CardStatus.TO_LEARN),
                repository.getCardCountByStatus(CardStatus.KNOWN),
                repository.getCardCountByStatus(CardStatus.LEARNED)
            ) { toLearn, known, learned ->
                CardCounts(toLearn, known, learned)
            }.collect { counts ->
                _cardCounts.value = counts
            }
        }
    }
}

data class CardCounts(val toLearn: Int, val known: Int, val learned: Int)