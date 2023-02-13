package com.wallpaper.demo

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btnSetWall).setOnClickListener {
            val previewIntent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this@MainActivity.application, DynamicWallpaper::class.java)
                )
            }
            val contract = EmptyActivityLaunchResultRegister.getContract(this)
            val launcher = EmptyActivityLaunchResultRegister.getLauncher(this)
            contract?.onGetIntent = { previewIntent }
            contract?.onCallback = {}
            launcher?.launch(null)
        }
    }
}