package com.pandacorp.memocards.database

import kotlinx.coroutines.flow.Flow

class CardRepository(private val cardDao: CardDao) {
    val allCards: Flow<List<CardEntity>> = cardDao.getAllCards()

    suspend fun insertCard(card: CardEntity) {
        cardDao.insertCard(card)
    }

    suspend fun deleteCard(card: CardEntity) {
        cardDao.deleteCard(card)
    }

    suspend fun deleteAllCards() {
        cardDao.deleteAllCards()
    }
}