package com.wallpaper.demo

import android.app.Application
import android.content.Context

lateinit var globalApp : Application

/*
dp -> px
 */
fun dp2px(dp: Int) = (globalApp.resources.displayMetrics.density * dp + 0.5f).toInt()

fun Context.dp2px(dp: Int) = (this.resources.displayMetrics.density * dp + 0.5f).toInt()

class MyApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        globalApp  = this
    }
}