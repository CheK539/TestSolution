package com.chek.testsolution.models

import com.chek.testsolution.enums.QuestionType
import java.io.InputStream

data class QuestionsFile(var inputStream: InputStream, val questionType: QuestionType)
