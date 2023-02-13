package com.wallpaper.demo


import android.app.Activity
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.wallpaper.demo.v2.PreviewEngineIMPL
import com.wallpaper.demo.v2.QuickBoostRouteActivity
import com.wallpaper.demo.v2.RealEngineIMPL
import com.wallpaper.demo.v2.WallpaperEngineInterface


/**
 * 壁纸加速球service
 *
 * 注意如果要优化该功能, 不要修改服务的路径和类名, 否则更新用户无法自动启动该服务
 */
class DynamicWallpaper : WallpaperService() {

    companion object {
        var realServiceEnable = false
    }


    override fun onCreateEngine(): Engine = WallpaperEngine()


    /* ######################################################################################################################################## */
    /* ######################################################################################################################################## */
    /* ######################################################################################################################################## */


    /**
     * 注意引擎的这个类名也不能修改, 否则更新app后会无法重启动态壁纸服务
     */
    inner class WallpaperEngine : Engine() {


        private val impl: WallpaperEngineInterface by lazy {
            if (isPreview) PreviewEngineIMPL(this) else RealEngineIMPL(this)
        }


        private val activityListener = object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityStopped(activity: Activity) {
                if (activity is QuickBoostRouteActivity)
                    kotlin.runCatching {
                        if (impl is RealEngineIMPL)
                            (impl as RealEngineIMPL).resetNormalState()
                    }
            }
        }


        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(!isPreview)
            if (!isPreview) {
                //toast(R.string.quick_boost_toast_success)
                realServiceEnable = true
                globalApp.unregisterActivityLifecycleCallbacks(activityListener)
                globalApp.registerActivityLifecycleCallbacks(activityListener)
            } else {
                //AppAnalysis.quick_boost_guide_show()
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            holder ?: return
            impl.onCreate()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            impl.onVisibilityChanged(visible)
        }

        override fun onDestroy() {
            super.onDestroy()
            impl.onDestroy()
            if (!isPreview) {
                realServiceEnable = false
                globalApp.unregisterActivityLifecycleCallbacks(activityListener)
            }
            if (isPreview && !realServiceEnable) {
                //AppAnalysis.quick_boost_guide_exit_click()
            }
        }

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
            event ?: return
            impl.onTouch(event)
        }
    }

}