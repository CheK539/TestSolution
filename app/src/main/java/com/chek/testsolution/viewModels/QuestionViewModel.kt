package com.chek.testsolution.viewModels

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chek.testsolution.enums.QuestionType
import com.chek.testsolution.models.Question
import com.chek.testsolution.models.QuestionsFile
import com.chek.testsolution.parser.QuestionsParser

class QuestionViewModel : ViewModel() {
    private val mutableQuestions = MutableLiveData<List<Question>>()
    private val mutableQuestion = MutableLiveData<Question>()
    private var remainQuestion = ArrayDeque<Int>()

    val question: LiveData<Question> = mutableQuestion

    fun parseQuestions(fileAsset: AssetManager, files: List<QuestionsFile>) {
        val questions = mutableListOf<Question>()
        files.forEach { questionsFile ->
            when (questionsFile.questionType) {
                QuestionType.Single ->
                    questions.addAll(QuestionsParser.parseQuestions(fileAsset, questionsFile))

                QuestionType.Multiply ->
                    questions.addAll(QuestionsParser.parseQuestions(fileAsset, questionsFile))

                QuestionType.PictureSingle ->
                    questions.addAll(QuestionsParser.parseImageQuestions(fileAsset, questionsFile))

                QuestionType.PictureMultiply ->
                    questions.addAll(QuestionsParser.parseImageQuestions(fileAsset, questionsFile))

                QuestionType.Input ->
                    questions.addAll(QuestionsParser.parseQuestions(fileAsset, questionsFile))
            }
        }

        mutableQuestions.value = questions
        remainQuestion = ArrayDeque(questions.indices.shuffled())
        loadQuestion()
    }

    fun loadQuestion() {
        mutableQuestions.value?.let { questions ->
            mutableQuestion.value = questions[remainQuestion.first()]
            remainQuestion.removeFirst()
        }
    }
}