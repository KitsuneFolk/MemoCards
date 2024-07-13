package com.pandacorp.memocards.database

data class CardItem(
    val id: Int = -1,
    val front: String,
    val back: String,
    val details: String = "",
    val status: CardStatus = CardStatus.TO_LEARN
) {
    fun toEntity(): CardEntity {
        return CardEntity(id, front, back, details, status)
    }
}