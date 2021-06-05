package com.chek.testsolution.parser

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import com.chek.testsolution.enums.QuestionType
import com.chek.testsolution.models.Question
import com.chek.testsolution.models.QuestionsFile
import java.io.BufferedReader
import java.io.InputStreamReader

object QuestionsParser {
    fun parseQuestions(fileAssets: AssetManager, questionsFile: QuestionsFile): List<Question> {
        val result = mutableListOf<Question>()

        val inputStream = fileAssets.open(questionsFile.path)
        val file = BufferedReader(InputStreamReader(inputStream))
        val lines = file.readLines()

        var correctAnswers = mutableListOf<String>()
        var incorrectAnswers = mutableListOf<String>()
        var questionText = ""

        lines.forEach { line ->
            when (line[0]) {
                '#' -> {
                    if (correctAnswers.size != 0) {
                        val currentQuestion = createQuestion(
                            questionText,
                            correctAnswers,
                            incorrectAnswers,
                            questionsFile.questionType
                        )

                        result.add(currentQuestion)
                        correctAnswers = mutableListOf()
                        incorrectAnswers = mutableListOf()
                    }
                    questionText = line.takeLast(line.length - 1)
                }

                '+' -> {
                    correctAnswers.add(line.takeLast(line.length - 1).lowercase())
                }

                '-' -> {
                    incorrectAnswers.add(line.takeLast(line.length - 1).lowercase())
                }
            }
        }

        if (correctAnswers.size != 0) {
            val currentQuestion = createQuestion(
                questionText,
                correctAnswers,
                incorrectAnswers,
                questionsFile.questionType
            )

            result.add(currentQuestion)
        }

        return result
    }

    fun parseImageQuestions(
        fileAssets: AssetManager,
        questionsFile: QuestionsFile
    ): List<Question> {
        val result = mutableListOf<Question>()

        val inputStream = fileAssets.open(questionsFile.path)
        val file = BufferedReader(InputStreamReader(inputStream))
        val lines = file.readLines()

        var correctAnswers = mutableListOf<String>()
        var incorrectAnswers = mutableListOf<String>()
        var questionText = ""

        lines.forEach { line ->
            when (line[0]) {
                '#' -> {
                    if (correctAnswers.size != 0) {
                        val currentQuestion = createQuestion(
                            questionText,
                            correctAnswers,
                            incorrectAnswers,
                            questionsFile.questionType,
                            fileAssets
                        )

                        result.add(currentQuestion)
                        correctAnswers = mutableListOf()
                        incorrectAnswers = mutableListOf()
                    }
                    questionText = line.takeLast(line.length - 1)
                }

                '+' -> {
                    correctAnswers.add(line.takeLast(line.length - 1).lowercase())
                }

                '-' -> {
                    incorrectAnswers.add(line.takeLast(line.length - 1).lowercase())
                }
            }
        }

        if (correctAnswers.size != 0) {
            val currentQuestion = createQuestion(
                questionText,
                correctAnswers,
                incorrectAnswers,
                questionsFile.questionType,
                fileAssets
            )

            result.add(currentQuestion)
        }

        return result
    }

    private fun createQuestion(
        questionText: String, correctAnswers: List<String>, incorrectAnswers: List<String>,
        questionType: QuestionType
    ): Question {
        return Question(
            questionText,
            correctAnswers,
            incorrectAnswers,
            questionType
        )
    }

    private fun createQuestion(
        questionText: String, correctAnswers: List<String>, incorrectAnswers: List<String>,
        questionType: QuestionType, fileAssets: AssetManager
    ): Question {
        return createQuestion(questionText, correctAnswers, incorrectAnswers, questionType).apply {
            val number = questionText.split('.').first()
            val imageStream = fileAssets.open("images/$number.png")
            image = BitmapFactory.decodeStream(imageStream)
        }
    }
}