package com.chek.testsolution.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chek.testsolution.R
import com.chek.testsolution.databinding.QuestionFragmentBinding
import com.chek.testsolution.enums.QuestionType
import com.chek.testsolution.models.Question
import com.chek.testsolution.models.QuestionsFile
import com.chek.testsolution.viewModels.QuestionViewModel

class QuestionFragment : Fragment() {

    companion object {
        fun newInstance() = QuestionFragment()
    }

    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var binding: QuestionFragmentBinding
    private lateinit var question: Question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileAssets = (activity as AppCompatActivity).assets
        val files = mutableListOf(
            QuestionsFile(fileAssets.open("single_questions_en.txt"), QuestionType.Single),
            QuestionsFile(fileAssets.open("multi_questions_en.txt"), QuestionType.Multi)
        )

        questionViewModel =
            ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return QuestionViewModel() as T
                }
            }).get(QuestionViewModel::class.java)

        questionViewModel.parseQuestions(files)

        questionViewModel.question.observe(this) {
            question = it
            binding.multiplyChoiceGroup.visibility = View.GONE
            binding.oneChoiceGroup.visibility = View.GONE

            when (it.questionType) {
                QuestionType.Single -> {
                    binding.oneChoiceGroup.visibility = View.VISIBLE
                    createSingleQuestion(it)
                }
                QuestionType.Multi -> {
                    binding.multiplyChoiceGroup.visibility = View.VISIBLE
                    createMultiplyQuestion(it)
                }

                QuestionType.PictureSingle -> TODO()

                QuestionType.PictureMulti -> TODO()
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

    private fun createSingleQuestion(question: Question) {
        binding.oneChoiceGroup.removeAllViews()
        val radioButtonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, 24) }

        val answers = question.correctAnswers + question.incorrectAnswers

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

        val answers = question.correctAnswers + question.incorrectAnswers

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
                        .text
                        .toString()
                )
            }

            QuestionType.Multi -> {
                binding.multiplyChoiceGroup.children.forEach { childrenView ->
                    val checkbox = childrenView as CheckBox
                    if (checkbox.isChecked)
                        actualAnswers.add(checkbox.text.toString())
                }
            }
            QuestionType.PictureSingle -> TODO()
            QuestionType.PictureMulti -> TODO()
        }

        val message = if (question.correctAnswers.all { actualAnswers.contains(it) })
            "${resources.getText(R.string.correct)}"
        else
            "${resources.getText(R.string.incorrect)}\n${question.correctAnswers.joinToString("\n")}"

        ResultDialogFragment(message) { _, _ ->
            questionViewModel.loadQuestion()
        }.show(
            childFragmentManager,
            ResultDialogFragment.TAG
        )
    }
}