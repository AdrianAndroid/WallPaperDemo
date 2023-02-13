package com.wallpaper.demo.v2

import android.animation.ValueAnimator
import android.graphics.*
import android.service.wallpaper.WallpaperService
import android.view.View
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.wallpaper.demo.ArrowMapHelper
import com.wallpaper.demo.R
import com.wallpaper.demo.dp2px
import com.wallpaper.demo.globalApp


class PreviewEngineIMPL(engine: WallpaperService.Engine) : WallpaperEngineInterface(engine) {

    private val backgroundColor = Color.parseColor("#f5f5f5")

    private val bottomLayerEndColor = Color.rgb(187, 187, 187)
    private val bottomLayerHeight = dp2px(66)
    private val bottomGradientPaint = Paint()

    private val arrowType = ArrowMapHelper.getArrowType()
    private val arrowWidth = dp2px(144) * 0.35f
    private val arrowHeight = dp2px(210) * 0.35f
    private val arrowBitmap by lazy {
        BitmapFactory.decodeResource(
            globalApp.resources,
            R.drawable.wallpaper_arrow
        )
    }
    private val arrowAtUp = arrowType != ArrowMapHelper.arrowBottom
    private var arrowMatrix: Matrix? = null

    private val drawingTopGap = dp2px(88).toFloat()

    private fun contentHeight(canvasWidth: Float) = canvasWidth / 1080f * 846f

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }
    private var textHeight = 0f
    private val text1 = "Quick Boost Wallpaper"
    private val text21 = "Set Quick Boost on wallpaper\\n to boost phone anytime".split("\n")[0]
    private val text22 = "Set Quick Boost on wallpaper\\n to boost phone anytime".split("\n")[1]
    private val textBounds = Rect()
    private val bigTextSize = dp2px(24).toFloat()
    private val smallTextSize = dp2px(16).toFloat()

    private val holderView = View(globalApp)
    private var lottie: LottieDrawable? = null

    private fun initLottie() {
        if (lottie != null)
            return
        lottie = LottieDrawable().apply {
            setSafeMode(true)
            setImagesAssetsFolder("wallpaper/images")
            repeatCount = ValueAnimator.INFINITE
            callback = holderView
            composition = LottieCompositionFactory.fromAssetSync(globalApp, "wallpaper/data.json").value
        }
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(backgroundColor)
        drawBottomGradient(canvas)
        drawContentAnimation(canvas)
        drawText(canvas)
        drawArrow(canvas)
    }


    private fun drawBottomGradient(canvas: Canvas) {
        val h = canvas.height.toFloat()
        val w = canvas.width.toFloat()
        if (bottomGradientPaint.shader == null)
            bottomGradientPaint.shader =
                LinearGradient(
                    0f,
                    h - bottomLayerHeight,
                    0f,
                    h,
                    backgroundColor,
                    bottomLayerEndColor,
                    Shader.TileMode.CLAMP
                )
        canvas.drawRect(0f, h - bottomLayerHeight, w, h, bottomGradientPaint)
    }


    private fun drawArrow(canvas: Canvas) {
        val w = canvas.width.toFloat()
        when (arrowType) {
            ArrowMapHelper.arrowRightTop -> {
                arrowMatrix = arrowMatrix ?: Matrix().apply {
                    postScale(arrowWidth / arrowBitmap.width, arrowHeight / arrowBitmap.height)
                    postRotate(180f, arrowWidth / 2f, arrowHeight / 2f)
                    postTranslate(
                        w - arrowWidth * 1.5f - globalApp.resources.displayMetrics.density * 4f,
                        drawingTopGap
                    )
                }
            }
            ArrowMapHelper.arrowLeftTop -> {
                arrowMatrix = arrowMatrix ?: Matrix().apply {
                    postScale(arrowWidth / arrowBitmap.width, arrowHeight / arrowBitmap.height)
                    postRotate(180f, arrowWidth / 2f, arrowHeight / 2f)
                    postTranslate(globalApp.resources.displayMetrics.density * 12f, drawingTopGap)
                }
            }
            else -> {
                arrowMatrix = arrowMatrix ?: Matrix().apply {
                    postScale(arrowWidth / arrowBitmap.width, arrowHeight / arrowBitmap.height)
                    postTranslate(
                        w / 2f - arrowWidth / 2,
                        drawingTopGap + arrowHeight + contentHeight(w) + textHeight
                    )
                }
            }
        }
        canvas.drawBitmap(arrowBitmap, arrowMatrix!!, null)
    }


    private fun drawContentAnimation(canvas: Canvas) {
        initLottie()
        val lottie: LottieDrawable = this.lottie ?: return
        if (!lottie.isAnimating) {
            lottie.playAnimation()
            return
        }
        val w = canvas.width.toFloat()
        if (lottie.intrinsicWidth <= 0 || lottie.intrinsicHeight <= 0)
            return
        lottie.setBounds(0, 0, w.toInt(), contentHeight(w).toInt())
        if (lottie.scale == 1f)
            lottie.scale = w / lottie.intrinsicWidth
        val ty = drawingTopGap + if (arrowAtUp) arrowHeight else 0f
        canvas.translate(0f, ty)
        lottie.draw(canvas)
        canvas.translate(0f, -ty)
    }


    private fun drawText(canvas: Canvas) {
        textHeight = 0f
        val w = canvas.width.toFloat()
        val top =
            drawingTopGap + contentHeight(w) + (if (arrowAtUp) arrowHeight else 0f) + dp2px(40)
        textPaint.textSize = bigTextSize
        textPaint.getTextBounds(text1, 0, text1.length, textBounds)
        textHeight += textBounds.height()
        textPaint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(text1, w / 2f - textBounds.width() / 2f, top, textPaint)

        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = smallTextSize
        textPaint.getTextBounds(text21, 0, text21.length, textBounds)
        canvas.drawText(text21, w / 2f - textBounds.width() / 2f, top + textHeight, textPaint)
        textHeight += textBounds.height()

        textPaint.getTextBounds(text22, 0, text22.length, textBounds)
        canvas.drawText(text22, w / 2f - textBounds.width() / 2f, top + textHeight, textPaint)
        textHeight += textBounds.height()
    }


    override fun onStateEnable() {

    }


    override fun onStateDisable() {
        lottie?.cancelAnimation()
    }


}