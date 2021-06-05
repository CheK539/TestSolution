package com.chek.testsolution.parser

import com.chek.testsolution.enums.QuestionType
import com.chek.testsolution.models.Question
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object QuestionsParser {
    fun parseQuestions(inputStream: InputStream, questionType: QuestionType): List<Question> {
        val result = mutableListOf<Question>()

        val file = BufferedReader(InputStreamReader(inputStream))
        val lines = file.readLines()

        var correctAnswers = mutableListOf<String>()
        var incorrectAnswers = mutableListOf<String>()
        var questionText = ""

        lines.forEach { line ->
            when (line[0]) {
                '#' -> {
                    if (correctAnswers.size != 0 && incorrectAnswers.size != 0) {
                        val currentQuestion = Question(
                            questionText, correctAnswers, incorrectAnswers, questionType
                        )
                        result.add(currentQuestion)
                        correctAnswers = mutableListOf()
                        incorrectAnswers = mutableListOf()
                    }
                    questionText = line.takeLast(line.length - 1)
                }

                '+' -> {
                    correctAnswers.add(line.takeLast(line.length - 1))
                }

                '-' -> {
                    incorrectAnswers.add(line.takeLast(line.length - 1))
                }
            }
        }

        if (correctAnswers.size != 0 && incorrectAnswers.size != 0) {
            val currentQuestion = Question(
                questionText, correctAnswers, incorrectAnswers, questionType
            )
            result.add(currentQuestion)
        }

        return result
    }
}