package com.brdx.movpelis.core

enum class CustomError(val default: Int) {

    Unknown(-1),
    ApiKeyInvalid(1),
    UnauthorizedAction(2);

    companion object {
        private val VALUES = values()
        fun getByInt(value: Int): CustomError = VALUES.firstOrNull { it.default == value } ?: valueOf("Unknown")
    }
}