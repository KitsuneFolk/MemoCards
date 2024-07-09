package com.pandacorp.memocards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandacorp.memocards.database.Card
import com.pandacorp.memocards.database.CardRepository
import com.pandacorp.memocards.database.CardStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CardRepository) : ViewModel() {
    private val _cardCounts = MutableStateFlow(CardCounts(0, 0, 0))
    val cardCounts: StateFlow<CardCounts> = _cardCounts

    private val allCards = repository.allCards.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    fun getFilteredCards(query: String): StateFlow<List<Card>> {
        return allCards.map { cards ->
            cards.filter { card ->
                card.front.contains(query, ignoreCase = true) ||
                        card.back.contains(query, ignoreCase = true) ||
                        card.details.contains(query, ignoreCase = true)
            }.sortedBy { it.status }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
}

data class CardCounts(val toLearn: Int, val known: Int, val learned: Int)