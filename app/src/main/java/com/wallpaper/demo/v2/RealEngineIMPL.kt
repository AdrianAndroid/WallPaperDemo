package com.wallpaper.demo.v2

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.wallpaper.demo.R
import com.wallpaper.demo.globalApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class RealEngineIMPL(engine: WallpaperService.Engine) : WallpaperEngineInterface(engine) {


    private val helper = QuickBoostHelper()
    private val scope = MainScope()

    @Volatile
    private var inDrawingFan = false
    private val fanBitmap by lazy {
        BitmapFactory.decodeResource(
            globalApp.resources,
            R.drawable.ic_wallpaper_fan
        )
    }
    private val rocketBitmap by lazy {
        BitmapFactory.decodeResource(
            globalApp.resources,
            R.drawable.ic_wallpaper_rocket
        )
    }
    private val backgroundBitmap by lazy { helper.getDrawingBackground() }
    private val backgroundImageView = ImageView(globalApp)
    private val normalStateRocketImageView = ImageView(globalApp)
    private val fanImageView = ImageView(globalApp).apply {
        setBackgroundColor(Color.parseColor("#0039e8"))
    }
    private var backToNormalStateJob: Job? = null
    private val clickListener = SimpleTouchClickGestureDetector().apply {
        onClickListener = { x, y ->
            if (helper.inBallTouchArea(x, y))
                onBallClick()
        }
    }

    private var fanRotateAnimator: ValueAnimator? = null


    private fun onBallClick() {
        if (inDrawingFan)
            return
        inDrawingFan = true
        backToNormalStateJob?.cancel()
        backToNormalStateJob = scope.launch {
            delay(5500)
            inDrawingFan = false
        }
        fanRotateAnimator.cancel2()
        fanRotateAnimator = ValueAnimator.ofFloat(360f, 0f).setDuration(100).apply {
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
//        AppAnalysis.quick_boost_function_icon_click()
        scope.launch { QuickBoostRouteActivity.launch() }
    }


    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        if (inDrawingFan)
            drawFanState(canvas)
        else
            drawNormalState(canvas)
    }


    private fun drawBackground(canvas: Canvas) {
        initHolderImageView(
            backgroundImageView,
            canvas.width,
            canvas.height,
            ImageView.ScaleType.CENTER_CROP,
            backgroundBitmap
        )
        backgroundImageView.draw(canvas)
    }


    private fun drawFanState(canvas: Canvas) {
        val drawingLength = helper.ballDrawingLength
        initHolderImageView(
            fanImageView,
            drawingLength,
            drawingLength,
            ImageView.ScaleType.FIT_XY,
            fanBitmap
        )
        canvas.translate(helper.ballDrawingLeft.toFloat(), helper.ballDrawingTop)
        val degree = fanRotateAnimator?.animatedValue as? Float ?: 0f
        val rotateCenter = drawingLength / 2f
        if (degree != 0f) canvas.rotate(degree, rotateCenter, rotateCenter)
        fanImageView.draw(canvas)
        if (degree != 0f) canvas.rotate(-degree, rotateCenter, rotateCenter)
        val rocketLength = (drawingLength * 0.64f).toInt()
        initHolderImageView(
            normalStateRocketImageView,
            rocketLength,
            rocketLength,
            ImageView.ScaleType.FIT_XY,
            rocketBitmap
        )
        val trans = (drawingLength - rocketLength) / 2f
        canvas.translate(trans, trans)
        normalStateRocketImageView.draw(canvas)
    }


    private fun drawNormalState(canvas: Canvas) {
        val length = helper.ballDrawingLength
        initHolderImageView(
            normalStateRocketImageView,
            length,
            length,
            ImageView.ScaleType.FIT_XY,
            rocketBitmap
        )
        canvas.translate(helper.ballDrawingLeft.toFloat(), helper.ballDrawingTop)
        normalStateRocketImageView.draw(canvas)
    }


    private fun initHolderImageView(
        iv: ImageView,
        width: Int,
        height: Int,
        scaleType: ImageView.ScaleType,
        bitmap: Bitmap
    ) {
        if (iv.width != width || iv.height != height || iv.layoutParams == null) {
            iv.layoutParams = ViewGroup.LayoutParams(width, height)
            iv.scaleType = scaleType
            iv.setImageBitmap(bitmap)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            iv.measure(widthSpec, heightSpec)
            iv.layout(0, 0, width, height)
        }
    }


    override fun onStateEnable() {
        resetNormalState()
    }


    override fun onStateDisable() {
        resetNormalState()
    }


    fun resetNormalState() {
        inDrawingFan = false
        backToNormalStateJob?.cancel()
        backToNormalStateJob = null
        fanRotateAnimator.cancel2()
        fanRotateAnimator = null
    }


    override fun onTouch(event: MotionEvent) {
        clickListener.onTouch(event)
    }

}
fun Animator?.cancel2() {
    this ?: return
    removeAllListeners()
    if (this is ValueAnimator) removeAllUpdateListeners()
    cancel()
}