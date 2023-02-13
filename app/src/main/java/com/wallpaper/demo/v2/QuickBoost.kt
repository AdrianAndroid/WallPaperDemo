package com.wallpaper.demo.v2

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import com.wallpaper.demo.DynamicWallpaper
import com.wallpaper.demo.globalApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object QuickBoost {

    private val previewIntent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
        putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(globalApp, DynamicWallpaper::class.java)
        )
    }


    val functionEnable get() = DynamicWallpaper.realServiceEnable


    fun launchQuickBoostPreview(
        activity: AppCompatActivity,
        toastIfFailed: Boolean,
        onPageBack: () -> Unit
    ): Boolean {
        return false
    }


    fun shutdown() {
        DynamicWallpaper.realServiceEnable = false
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val wm = WallpaperManager.getInstance(globalApp)
                if (QuickBoostHelper.originWallpaperBackgroundFile.exists()) {
                    val bmp =
                        BitmapFactory.decodeFile(QuickBoostHelper.originWallpaperBackgroundFile.absolutePath)
                    if (bmp != null) wm.setBitmap(bmp) else wm.clear()
                } else {
                    wm.clear()
                }
            } catch (e: Exception) {
                WallpaperManager.getInstance(globalApp).clear()
            }
        }
    }


    fun cloudControlEnable(): Boolean {
        return true
    }

}
