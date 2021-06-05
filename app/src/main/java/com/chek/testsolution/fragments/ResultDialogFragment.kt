package com.chek.testsolution.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.chek.testsolution.R

class ResultDialogFragment(
    private val message: String,
    private val listener: DialogInterface.OnClickListener
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(getString(R.string.text_ok), listener)
            .create()

    companion object {
        const val TAG = "ResultDialog"
    }
}