package com.chek.testsolution.enums

enum class QuestionPEType {
    Single,
    Multiply,
    Unsupported;

    companion object {
        fun getByValue(value: String): QuestionPEType = when (value) {
            "radio" -> Single
            "checkbox" -> Multiply
            else -> Unsupported
        }
    }
}