package com.chek.testsolution.models

import android.graphics.Bitmap
import com.chek.testsolution.enums.QuestionType

data class Question(
    val questionText: String,
    val correctAnswers: List<String>,
    val incorrectAnswers: List<String>,
    val questionType: QuestionType
)
{
    var image: Bitmap? = null
}