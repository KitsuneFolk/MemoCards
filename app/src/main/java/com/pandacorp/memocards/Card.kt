package com.pandacorp.memocards

import com.pandacorp.memocards.database.CardEntity

data class Card(val front: String, val back: String) {
    constructor(entity: CardEntity) : this(entity.front, entity.back)
}