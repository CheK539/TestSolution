package com.chek.testsolution

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chek.testsolution.enums.QuestionType
import com.chek.testsolution.models.Question
import com.chek.testsolution.parser.QuestionsParser


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}