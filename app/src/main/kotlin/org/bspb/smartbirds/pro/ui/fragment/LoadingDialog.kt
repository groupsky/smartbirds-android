package org.bspb.smartbirds.pro.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.bspb.smartbirds.pro.R

class LoadingDialog : DialogFragment() {

    private var loadingMessage: TextView? = null
    private var message: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_MESSAGE)) {
            message = savedInstanceState.getString(EXTRA_MESSAGE, "")
        }
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        loadingMessage = view.findViewById<TextView>(R.id.text_loading_message)
        loadingMessage?.text = message
        return AlertDialog.Builder(context!!)
                .setView(view)
                .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (message != null) {
            outState.putString(EXTRA_MESSAGE, message)
        }
        super.onSaveInstanceState(outState)
    }

    public fun updateTexts(message: String) {
        loadingMessage?.text = message
    }

    companion object {
        private const val EXTRA_MESSAGE = "message"

        public fun newInstance(message: String?): LoadingDialog {
            val fragment = LoadingDialog()
            fragment.message = message
            return fragment
        }
    }
}