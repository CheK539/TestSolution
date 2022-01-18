package com.chek.testsolution.enums

enum class QuestionType(val idType: Int, val typeString: String) {
    Single(0, "Single"),
    Multiply(1, "Multiply"),
    PictureSingle(2, "PictureSingle"),
    PictureMultiply(3, "PictureMultiply"),
    Input(4, "Input"),
    Order(5, "Order");

    companion object {
        fun fromString(value: String) = values().first { it.typeString == value }
        fun fromId(value: Int) = values().first { it.idType == value }
    }
}
