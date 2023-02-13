package com.wallpaper.demo.v2

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.WorkerThread
import com.wallpaper.demo.R
import com.wallpaper.demo.dp2px
import com.wallpaper.demo.globalApp
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File


class QuickBoostHelper {


    @SuppressLint("MissingPermission")
    @WorkerThread
    fun getDrawingBackground(): Bitmap {
        kotlin.runCatching { originWallpaperBackgroundFile.delete() }
        if (Build.VERSION.SDK_INT >= 24)
            kotlin.runCatching {
                val file = WallpaperManager.getInstance(globalApp)
                    .getWallpaperFile(WallpaperManager.FLAG_SYSTEM)
                val resultBmp = BitmapFactory.decodeFileDescriptor(file.fileDescriptor)
                if (resultBmp != null) {
                    newWorkerThreadCoroutineJob { resultBmp.toFile(originWallpaperBackgroundFile) }
                    return resultBmp
                }
            }
        kotlin.runCatching {
            val drawable = WallpaperManager.getInstance(globalApp).drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                if (bitmap != null) {
                    newWorkerThreadCoroutineJob { bitmap.toFile(originWallpaperBackgroundFile) }
                    return bitmap
                }
            }
        }
        return BitmapFactory.decodeResource(globalApp.resources, R.drawable.wallpaper_default)
    }


    companion object {
        val originWallpaperBackgroundFile by lazy {
            File(
                globalApp.filesDir,
                "walOriBck.image.back"
            )
        }
    }


    val ballDrawingVerticalPercent = 0.673f
    val ballEdgeEmbed = dp2px(6)
    val ballTouchLength = dp2px(50)
    val ballDrawingLength = dp2px(40)
    val ballTouchAndDrawingLengthGap = (ballTouchLength - ballDrawingLength) / 2f


    val ballDrawingLeft: Int
        get() = globalApp.resources.displayMetrics.widthPixels - ballDrawingLength + ballEdgeEmbed
    val ballDrawingTop: Float
        get() = globalApp.resources.displayMetrics.heightPixels * ballDrawingVerticalPercent


    val ballTouchingLeft: Float
        get() = ballDrawingLeft - ballTouchAndDrawingLengthGap
    val ballTouchingTop: Float
        get() = ballDrawingTop - ballTouchAndDrawingLengthGap


    /* 是否在加速球的可点击区域 */
    fun inBallTouchArea(x: Float, y: Float): Boolean {
        return x >= ballTouchingLeft && y >= ballTouchingTop && y <= ballTouchingTop + ballTouchLength
    }

}

fun newWorkerThreadCoroutineJob(block: suspend CoroutineScope.() -> Unit): Job {
    return GlobalScope.launch(context = Dispatchers.IO, block = block)
}

fun Bitmap.toFile(file: File) = kotlin.runCatching {
    val out = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, out)
    file.writeBytes(out.toByteArray())
}
