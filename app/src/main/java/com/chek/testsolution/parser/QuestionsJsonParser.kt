package com.chek.testsolution.parser

import android.content.res.AssetManager
import android.util.Log
import com.chek.testsolution.enums.QuestionPEType
import com.chek.testsolution.enums.QuestionType
import com.chek.testsolution.models.Question
import com.chek.testsolution.models.QuestionPE
import com.chek.testsolution.models.QuestionsFile
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


object QuestionsJsonParser {

    private val gson = GsonBuilder().create()

    fun parseQuestions(fileAssets: AssetManager, questionsFile: QuestionsFile): List<Question> {
        Log.d("MYTEST", questionsFile.path)

        val jsonString = getJsonAsString(fileAssets, questionsFile.path)

        val token = object : TypeToken<List<QuestionPE>>() {}.type

        return gson.fromJson<List<QuestionPE>>(jsonString, token)
            .filter { question ->
                QuestionPEType.getByValue(question.type) != QuestionPEType.Unsupported
            }
            .map { question ->
                Question(
                    questionText = question.legend,
                    correctAnswers = question.fields
                        .filter { field -> field.correct }
                        .map { field -> field.text },
                    incorrectAnswers = question.fields
                        .filter { field -> !field.correct }
                        .map { field -> field.text },
                    questionType = when(QuestionPEType.getByValue(question.type)) {
                        QuestionPEType.Single -> QuestionType.Single
                        QuestionPEType.Multiply -> QuestionType.Multiply
                        else -> QuestionType.Single
                    }
                )
            }
    }

    private fun getJsonAsString(fileAssets: AssetManager, path: String): String {
        val inputStream = fileAssets.open(path)
        val size = inputStream.available()
        val buffer = ByteArray(size)

        inputStream.read(buffer)
        inputStream.close()

        return String(buffer)
    }
}

