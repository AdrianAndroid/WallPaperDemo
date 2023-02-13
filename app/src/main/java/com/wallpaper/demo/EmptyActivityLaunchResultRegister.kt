package com.wallpaper.demo

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.launchActivityForCallback(intent: Intent, onPageBack: () -> Unit): Boolean {
    kotlin.runCatching {
        val contract = EmptyActivityLaunchResultRegister.getContract(this) ?: return false
        val launcher = EmptyActivityLaunchResultRegister.getLauncher(this) ?: return false
        contract.onGetIntent = { intent }
        contract.onCallback = onPageBack
        launcher.launch(null)
        return true
    }
    return false
}


object EmptyActivityLaunchResultRegister {


    private val contractMap = HashMap<AppCompatActivity, EmptyActivityResultContract>()
    private val launcherMap = HashMap<AppCompatActivity, ActivityResultLauncher<String>>()
    private var activityStartCount = 0

    init {
        globalApp.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is AppCompatActivity && activity is ActivityResultContractAble) {
                    val contract = EmptyActivityResultContract()
                    val launcher =
                        activity.registerForActivityResult(contract) { contract.onCallback?.invoke() }
                    contractMap[activity] = contract
                    launcherMap[activity] = launcher
                }
            }

            override fun onActivityStarted(activity: Activity) {
                activityStartCount++
                if (activityStartCount == 1) {
                    //从后台切换到前台
                    onForeGround()
                }
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
                activityStartCount--
                if (activityStartCount == 0) {
                    //从前台切换到后台
                    onBackGround()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activity is AppCompatActivity && activity is ActivityResultContractAble) {
                    contractMap.remove(activity)
                    launcherMap.remove(activity)
                }
            }
        })
    }


    fun getContract(activity: AppCompatActivity): EmptyActivityResultContract? {
        return contractMap[activity]
    }


    fun getLauncher(activity: AppCompatActivity): ActivityResultLauncher<String>? {
        return launcherMap[activity]
    }

}

private var backgroundTime = 0L
fun onBackGround() {
    backgroundTime = getNowTime()
}

fun onForeGround() {
    backgroundTime = -1L
}

internal fun getNowTime(): Long {
    return System.currentTimeMillis()
}