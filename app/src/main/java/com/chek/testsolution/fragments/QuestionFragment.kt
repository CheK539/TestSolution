package com.chek.testsolution.fragments

import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.chek.testsolution.R
import com.chek.testsolution.databinding.QuestionFragmentBinding
import com.chek.testsolution.enums.QuestionType
import com.chek.testsolution.models.Question
import com.chek.testsolution.models.QuestionsFile
import com.chek.testsolution.models.QuestionsData
import com.chek.testsolution.viewModels.QuestionViewModel

class QuestionFragment : Fragment() {

    companion object {
        private val LETTERS = listOf('A', 'B', 'C', 'D', 'E', 'F')

        const val TAG = "CorrectCount"
    }

    private val questionViewModel: QuestionViewModel by viewModels()
    private lateinit var binding: QuestionFragmentBinding
    private lateinit var question: Question
    private lateinit var questionsData: QuestionsData

    private var correctCount = 0
    private var files = listOf<QuestionsFile>()
    private var fileAsset: AssetManager? = null
    private val questionsMap = mutableMapOf<Char, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fileAsset = (activity as AppCompatActivity).assets
        files = listOf(
            QuestionsFile("1.1.json", QuestionType.Single),
            QuestionsFile("1.2.json", QuestionType.Single),
            QuestionsFile("1.3.json", QuestionType.Single),
            QuestionsFile("2.1.json", QuestionType.Single),
            QuestionsFile("2.2.json", QuestionType.Single),
            QuestionsFile("2.3.json", QuestionType.Single),
            QuestionsFile("3.1.json", QuestionType.Single),
            QuestionsFile("3.2.json", QuestionType.Single),
            QuestionsFile("3.3.json", QuestionType.Single),
            QuestionsFile("4.1.json", QuestionType.Single),
            QuestionsFile("4.2.json", QuestionType.Single),
            QuestionsFile("5.1.json", QuestionType.Single),
            QuestionsFile("5.2.json", QuestionType.Single),
            QuestionsFile("5.3.json", QuestionType.Single),
        )

        if (savedInstanceState == null)
            fileAsset?.let { questionViewModel.parseQuestions(it, files) }

        questionViewModel.questionsData.observe(this) {
            questionsData = it

            val percent = if (questionsData.complete > 1)
                correctCount.toFloat() / (questionsData.complete - 1).toFloat() * 100
            else
                0f
            val result = "${"%.2f".format(percent)}%"
            (activity as AppCompatActivity).supportActionBar?.title =
                "${it.complete}/${it.total}, $result"
        }

        questionViewModel.question.observe(this) {
            question = it
            binding.multiplyChoiceGroup.visibility = View.GONE
            binding.oneChoiceGroup.visibility = View.GONE
            binding.questionImage.visibility = View.GONE
            binding.editTextAnswer.visibility = View.GONE

            when (it.questionType) {
                QuestionType.Single -> {
                    binding.oneChoiceGroup.visibility = View.VISIBLE
                    createSingleQuestion(it)
                }
                QuestionType.Multiply -> {
                    binding.multiplyChoiceGroup.visibility = View.VISIBLE
                    createMultiplyQuestion(it)
                }

                QuestionType.PictureSingle -> {
                    binding.questionImage.visibility = View.VISIBLE
                    binding.oneChoiceGroup.visibility = View.VISIBLE
                    createSingeImageQuestion(it)
                }

                QuestionType.PictureMultiply -> {
                    binding.questionImage.visibility = View.VISIBLE
                    binding.multiplyChoiceGroup.visibility = View.VISIBLE
                    createMultiplyImageQuestion(it)
                }
                QuestionType.Input -> {
                    binding.editTextAnswer.visibility = View.VISIBLE
                    createInputQuestion(it)
                }
                QuestionType.Order -> {
                    binding.editTextAnswer.visibility = View.VISIBLE
                    createOrderQuestion(question)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.question_fragment, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.acceptButton.setOnClickListener { checkAnswer() }
    }

    private fun createInputQuestion(question: Question) {
        binding.editTextAnswer.text.clear()
        binding.editTextAnswer.requestFocus()
        binding.questionText.text = question.questionText
    }

    private fun createOrderQuestion(question: Question) {
        binding.editTextAnswer.text.clear()
        binding.editTextAnswer.requestFocus()

        val variants = question.correctAnswers.shuffled()
            .mapIndexed { index, variant ->
                questionsMap[LETTERS[index]] = variant
                "${LETTERS[index]}) $variant"
            }

        binding.questionText.text = requireContext().getString(
            R.string.question_text,
            question.questionText,
            variants.joinToString("\n")
        )
    }

    private fun createSingeImageQuestion(question: Question) {
        binding.questionImage.setImageBitmap(question.image)
        createSingleQuestion(question)
    }

    private fun createMultiplyImageQuestion(question: Question) {
        binding.questionImage.setImageBitmap(question.image)
        createMultiplyQuestion(question)
    }

    private fun createSingleQuestion(question: Question) {
        binding.oneChoiceGroup.removeAllViews()
        val radioButtonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, 24) }

        val answers = (question.correctAnswers + question.incorrectAnswers).shuffled()

        answers.forEach { answer ->
            val radioButton = RadioButton(context).apply {
                layoutParams = radioButtonParams
                text = answer
                id = ViewCompat.generateViewId()
            }

            binding.oneChoiceGroup.addView(radioButton)
        }

        binding.questionText.text = question.questionText
    }

    private fun createMultiplyQuestion(question: Question) {
        binding.multiplyChoiceGroup.removeAllViews()
        val checkboxParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, 24) }

        val answers = (question.correctAnswers + question.incorrectAnswers).shuffled()

        answers.forEach { answer ->
            val checkbox = CheckBox(context).apply {
                layoutParams = checkboxParams
                text = answer
                id = ViewCompat.generateViewId()
            }

            binding.multiplyChoiceGroup.addView(checkbox)
        }

        binding.questionText.text = question.questionText
    }

    private fun checkAnswer() {
        val actualAnswers = mutableListOf<String>()

        when (question.questionType) {
            QuestionType.Single -> view?.let {
                actualAnswers.add(
                    it.findViewById<RadioButton>(binding.oneChoiceGroup.checkedRadioButtonId)
                        ?.text
                        .toString()
                        .lowercase()
                )
            }

            QuestionType.Multiply -> binding.multiplyChoiceGroup.children.forEach { childrenView ->
                val checkbox = childrenView as CheckBox
                if (checkbox.isChecked)
                    actualAnswers.add(checkbox.text.toString().lowercase())
            }

            QuestionType.PictureSingle -> view?.let {
                actualAnswers.add(
                    it.findViewById<RadioButton>(binding.oneChoiceGroup.checkedRadioButtonId)
                        ?.text
                        .toString()
                        .lowercase()
                )
            }

            QuestionType.PictureMultiply ->
                binding.multiplyChoiceGroup.children.forEach { childrenView ->
                    val checkbox = childrenView as CheckBox
                    if (checkbox.isChecked)
                        actualAnswers.add(checkbox.text.toString().lowercase())
                }

            QuestionType.Input -> {
                actualAnswers.add(binding.editTextAnswer.text.toString().trim().lowercase())
                hideInput()
            }

            QuestionType.Order -> {
                binding.editTextAnswer.text.toString()
                    .trim()
                    .uppercase()
                    .forEach { symbol ->
                        val actualAnswer = questionsMap
                            .getOrElse(symbol) { "" }
                            .lowercase()
                        actualAnswers.add(actualAnswer)
                    }
                hideInput()
            }
        }

        var isCorrect = false

        val message = if (isCorrectAnswer(actualAnswers, question.questionType)) {
            isCorrect = true
            "${resources.getText(R.string.correct)}"
        } else
            "${resources.getText(R.string.incorrect)}\n\n${question.correctAnswers.joinToString("\n\n")}"

        ResultDialogFragment(message) { _, _ ->
            if (questionsData.complete < questionsData.total) {
                correctCount = if (isCorrect) ++correctCount else correctCount
                questionViewModel.loadQuestion()
            } else
                fileAsset?.let {
                    correctCount = if (isCorrect) ++correctCount else correctCount
                    questionViewModel.parseQuestions(it, files)
                    val percent = correctCount.toFloat() / questionsData.total.toFloat() * 100
                    val result = "${resources.getText(R.string.text_correct)}" +
                            " ${"%.2f".format(percent)}%"
                    correctCount = 0
                    ResultDialogFragment(result) { _, _ -> }
                        .show(childFragmentManager, ResultDialogFragment.TAG)
                }
        }.show(
            childFragmentManager,
            ResultDialogFragment.TAG
        )
    }

    private fun isCorrectAnswer(actualAnswers: List<String>, questionType: QuestionType): Boolean {
        return if (questionType != QuestionType.Order) {
            question.correctAnswers.all { actualAnswers.contains(it.lowercase()) }
        } else {
            var count = 0
            question.correctAnswers.all { answer ->
                val isCorrect = answer.lowercase() == actualAnswers.getOrNull(count)
                count++
                isCorrect
            }
        } && actualAnswers.size == question.correctAnswers.size
    }

    private fun hideInput() {
        val inputMethodManager =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(TAG, correctCount)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        correctCount = savedInstanceState?.getInt(TAG) ?: 0
    }
}
