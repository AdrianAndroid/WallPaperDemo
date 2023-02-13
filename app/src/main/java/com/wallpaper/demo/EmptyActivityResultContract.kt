package com.wallpaper.demo

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract


class EmptyActivityResultContract() : ActivityResultContract<String, String>() {

    override fun parseResult(resultCode: Int, intent: Intent?): String = ""


    var onGetIntent: (() -> Intent)? = null
    var onCallback: (() -> Unit)? = null
    override fun createIntent(context: Context, input: String): Intent {
        return onGetIntent?.invoke() ?: Intent()
    }

}