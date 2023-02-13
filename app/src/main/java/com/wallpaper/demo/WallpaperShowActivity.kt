package com.wallpaper.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WallpaperShowActivity : AppCompatActivity() {

    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, WallpaperShowActivity::class.java))
        }
    }

//    lateinit var binding: ActivityWallpaperShowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityWallpaperShowBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.wallpaperShowTitle.compoundDrawablePadding = dp2px(4)
//
//        binding.backBtn.setOnClickListener { view ->
//            finish()
//        }
    }

}