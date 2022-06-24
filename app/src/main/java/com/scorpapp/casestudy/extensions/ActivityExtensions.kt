package com.scorpapp.casestudy.extensions

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scorpapp.casestudy.R

fun Activity.showCommonPopup(message: String) {
    MaterialAlertDialogBuilder(this).apply {
        setCancelable(false)
        setMessage(message)
        setPositiveButton(getString(R.string.okay)) { dialog, _ ->
            dialog.dismiss()
        }
        show()
    }
}

fun Activity.showPopupWithAction(message: String): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(this).apply {
        setCancelable(false)
        setMessage(message)
    }
}

fun Activity.showLoading() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )

    val loadingView = this.findViewById<RelativeLayout>(R.id.loadingView)
    loadingView.visibility = View.VISIBLE
}

fun Activity.hideLoading() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    val loadingView = this.findViewById<RelativeLayout>(R.id.loadingView)
    loadingView.visibility = View.GONE
}

fun Activity.isLoading(): Boolean {
    val loadingView = this.findViewById<RelativeLayout>(R.id.loadingView)
    return loadingView.visibility == View.VISIBLE
}

