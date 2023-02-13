package com.wallpaper.demo.v2

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import com.wallpaper.demo.ChoreographerDrawingThread


abstract class WallpaperEngineInterface(private val realEngine: WallpaperService.Engine) {


    private var stateEnable = false


    fun onCreate() {
        if (realEngine.isVisible) {
            stateEnable = true
            drawingThread.start()
            onStateEnable()
        }
    }


    fun onVisibilityChanged(visible: Boolean) {
        if (visible) {
            stateEnable = true
            drawingThread.start()
            onStateEnable()
        } else {
            stateEnable = false
            drawingThread.stop()
            onStateDisable()
        }
    }


    fun onDestroy() {
        stateEnable = false
        drawingThread.stop()
        onStateDisable()
    }


    open fun onTouch(event: MotionEvent) {

    }


    abstract fun onDraw(canvas: Canvas)


    abstract fun onStateEnable()
    abstract fun onStateDisable()


    private val drawingThread = ChoreographerDrawingThread {
        if (!stateEnable) return@ChoreographerDrawingThread
        val holder = realEngine.surfaceHolder ?: return@ChoreographerDrawingThread
        kotlin.runCatching { holder.lockCanvas() }.onSuccess { canvas ->
            kotlin.runCatching { onDraw(canvas) }
            kotlin.runCatching { holder.unlockCanvasAndPost(canvas) }
        }
    }

}