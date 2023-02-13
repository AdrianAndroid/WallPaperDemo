package com.wallpaper.demo

import android.os.Handler
import android.os.HandlerThread
import android.view.Choreographer


class ChoreographerDrawingThread(callback: () -> Unit) {

    @Volatile
    private var handlerThread: HandlerThread? = null


    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (handlerThread == null)
                return
            callback.invoke()
            Choreographer.getInstance().postFrameCallback(this)
        }
    }


    @Synchronized
    fun start() {
        if (handlerThread != null)
            return

        handlerThread = HandlerThread("ChoreographerDrawingThread")
        handlerThread!!.start()

        Handler(handlerThread!!.looper).post {
            if (handlerThread != null) {
                Choreographer.getInstance().removeFrameCallback(frameCallback)
                Choreographer.getInstance().postFrameCallback(frameCallback)
            }
        }
    }


    @Synchronized
    fun stop() {
        if (handlerThread == null)
            return

        handlerThread?.quit()
        handlerThread = null

        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }

}