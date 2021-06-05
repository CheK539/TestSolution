package com.chek.testsolution.enums

enum class QuestionType(val idType: Int, val typeString: String) {
    Single(0, "Single"),
    Multi(1, "Multi"),
    PictureSingle(2, "PictureSingle"),
    PictureMulti(3, "PictureMulti");

    companion object {
        fun fromString(value: String) = values().first { it.typeString == value }
        fun fromId(value: Int) = values().first { it.idType == value }
    }
}