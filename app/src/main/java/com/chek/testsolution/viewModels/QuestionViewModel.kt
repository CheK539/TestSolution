package com.chek.testsolution.viewModels

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chek.testsolution.models.Question
import com.chek.testsolution.models.QuestionsData
import com.chek.testsolution.models.QuestionsFile
import com.chek.testsolution.parser.QuestionsJsonParser

class QuestionViewModel : ViewModel() {
    private val mutableQuestions = MutableLiveData<List<Question>>()
    private val mutableQuestion = MutableLiveData<Question>()
    private val mutableQuestionsData = MutableLiveData<QuestionsData>()

    private var remainQuestion = ArrayDeque<Int>()
    private var count = 0

    val question: LiveData<Question> = mutableQuestion
    val questionsData: LiveData<QuestionsData> = mutableQuestionsData

    fun parseQuestions(fileAsset: AssetManager, files: List<QuestionsFile>) {
        count = 0
        val questions = files
            .map { questionsFile -> QuestionsJsonParser.parseQuestions(fileAsset, questionsFile) }
            .flatten()

        mutableQuestions.value = questions
        remainQuestion = ArrayDeque(questions.indices.shuffled())
        loadQuestion()
    }

    fun loadQuestion() {
        mutableQuestions.value?.let { questions ->
            if (remainQuestion.size <= 0)
                return

            mutableQuestion.value = questions[remainQuestion.first()]
            mutableQuestionsData.postValue(QuestionsData(++count, questions.size))
            remainQuestion.removeFirst()
        }
    }
}
