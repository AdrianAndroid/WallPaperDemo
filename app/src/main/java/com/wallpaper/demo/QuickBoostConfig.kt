package com.wallpaper.demo

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.wallpaper.demo.v2.QuickBoost


object QuickBoostConfig {

    /* 开启功能  or  关闭功能 */
    fun onSwitch(activity: Activity, callback: () -> Unit) {
        if (!QuickBoost.functionEnable) {
            //AppAnalysis.setting_quick_boost_enable_click()
            markHomeGuideShown()
            QuickBoost.launchQuickBoostPreview(activity as AppCompatActivity, true) {}
        } else {
            //AppAnalysis.setting_quick_boost_disable_click()
            QuickBoost.shutdown()
        }
        callback.invoke()
    }


    /* 记录首页功能引导弹出过了 */
    fun markHomeGuideShown() {
        //TodayPreference.saveBoolean("hom_gd_qk_bst_sn", true)
    }


    /* 是否应该弹出功能引导页面 */
    private fun shouldShowGuide(): Boolean {
        if (!QuickBoost.cloudControlEnable()) return false
        if (QuickBoost.functionEnable) return false
        return true//!TodayPreference.getBoolean("hom_gd_qk_bst_sn")
    }


    /* 弹出首页功能引导 */
    fun launchGuidePageIfNeeded(activity: Activity): Boolean {
        if (!shouldShowGuide())
            return false
        markHomeGuideShown()
        return QuickBoost.launchQuickBoostPreview(activity as AppCompatActivity, false) {}
    }


    fun launchGuidePageForResultIfNeeded(
        activity: AppCompatActivity,
        onPageBack: () -> Unit
    ): Boolean {
        if (!shouldShowGuide())
            return false
        markHomeGuideShown()
        return QuickBoost.launchQuickBoostPreview(activity, false, onPageBack)
    }

}