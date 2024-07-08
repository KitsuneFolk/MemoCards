package com.pandacorp.memocards.database

import kotlinx.coroutines.flow.Flow

class CardRepository(private val cardDao: CardDao) {
    val allCards: Flow<List<CardEntity>> = cardDao.getAllCards()

    fun getCardCountByStatus(status: CardStatus): Flow<Int> =
        cardDao.getCardCountByStatus(status)

    suspend fun insertCard(card: CardEntity) {
        cardDao.insertCard(card)
    }

    suspend fun updateCard(card: CardEntity) {
        cardDao.updateCard(card)
    }

    suspend fun deleteCard(card: CardEntity) {
        cardDao.deleteCard(card)
    }

    suspend fun deleteAllCards() {
        cardDao.deleteAllCards()
    }
}