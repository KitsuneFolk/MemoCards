package com.pandacorp.memocards.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val front: String,
    val back: String,
    val status: CardStatus = CardStatus.TO_LEARN
)

enum class CardStatus {
    TO_LEARN, KNOWN, LEARNED
}