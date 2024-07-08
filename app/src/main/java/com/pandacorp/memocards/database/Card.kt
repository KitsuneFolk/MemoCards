package com.pandacorp.memocards.database

data class Card(val front: String, val back: String) {
    constructor(entity: CardEntity) : this(entity.front, entity.back)
}