package com.wallpaper.demo

import android.os.Build

object ArrowMapHelper {


    const val arrowLeftTop = 0
    const val arrowRightTop = 1
    const val arrowBottom = 2


    private val arrowType: Int


    fun getArrowType() = arrowType


    init {
        arrowType = init()
    }


    private fun init(): Int {
        kotlin.runCatching {
            val sdkInt = Build.VERSION.SDK_INT
            val man = Build.MANUFACTURER.lowercase()
            val model = Build.MODEL.lowercase()

            if (man.contains("samsung") && model.contains("sm-j", true))
                return arrowRightTop

            if (man.contains("motorola") && sdkInt <= 24)
                return arrowLeftTop

            if (man.contains("xiaomi") && model.contains("red"))
                return arrowRightTop
        }

        return arrowBottom
    }
}