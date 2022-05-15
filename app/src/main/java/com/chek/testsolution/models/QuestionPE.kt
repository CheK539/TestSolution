package com.chek.testsolution.models

data class QuestionPE(
    val type: String,
    val legend: String = "",
    val fields: List<Field> = emptyList()
) {
    data class Field(
        val text: String,
        val correct: Boolean = false
    )
}
