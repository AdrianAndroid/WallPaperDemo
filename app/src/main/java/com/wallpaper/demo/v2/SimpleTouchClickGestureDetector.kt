package com.wallpaper.demo.v2

import android.view.MotionEvent
import android.view.ViewConfiguration
import com.wallpaper.demo.globalApp
import kotlin.math.abs


/**
 * 简单的监测touch点击事件的识别工具
 */
class SimpleTouchClickGestureDetector {


    var onClickListener: ((x: Float, y: Float) -> Unit)? = null


    private var downX = -1f
    private var downY = -1f
    private var readyClicked = false

    private val touchSlop = ViewConfiguration.get(globalApp).scaledTouchSlop


    fun onTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                downY = event.rawY
                readyClicked = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (readyClicked) {
                    val mx = event.rawX
                    val my = event.rawY
                    if (abs(mx - downX) > touchSlop || abs(my - downY) > touchSlop)
                        readyClicked = false
                }
            }
            MotionEvent.ACTION_UP -> {
                if (readyClicked)
                    onClickListener?.invoke(event.rawX, event.rawY)
                downX = -1f
                downY = -1f
                readyClicked = false
            }
        }
    }


}