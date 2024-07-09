package com.pandacorp.memocards.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "front") val front: String,
    @ColumnInfo(name = "back") val back: String,
    @ColumnInfo(name = "details") val details: String,
    @ColumnInfo(name = "status") val status: CardStatus
) {
    fun toCard(): Card {
        return Card(id, front, back, details, status)
    }
}

enum class CardStatus {
    TO_LEARN, KNOWN, LEARNED
}