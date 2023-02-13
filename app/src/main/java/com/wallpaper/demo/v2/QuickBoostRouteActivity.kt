package com.wallpaper.demo.v2

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.wallpaper.demo.R
import com.wallpaper.demo.dp2px
import com.wallpaper.demo.globalApp

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Volatile
private var unique_number = 1

@Synchronized
fun getUniqueNumber() = ++unique_number


val pendingIntentFlag by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    else
        PendingIntent.FLAG_UPDATE_CURRENT
}


class QuickBoostRouteActivity : AppCompatActivity() {


    companion object {
        fun launch() {
            val intent = Intent(globalApp, QuickBoostRouteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            kotlin.runCatching {
                PendingIntent.getActivity(
                    globalApp,
                    getUniqueNumber(),
                    intent,
                    pendingIntentFlag
                ).send()
            }
                .onFailure { kotlin.runCatching { globalApp.startActivity(intent) } }
        }
    }


    private val help = QuickBoostHelper()
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.alpha = 0f
        overridePendingTransition(0, 0)


        window.decorView.alpha = 0f
        setContentView(R.layout.activity_quick_boost_route)
        val contentLayout = findViewById<View>(R.id.contentLayout)
        val textLayout = findViewById<View>(R.id.textLayout)
        val fanImageView = findViewById<View>(R.id.fanImageView)


        kotlin.runCatching {
            contentLayout.doOnLayout {
                textLayout.doOnLayout {

                    contentLayout.translationY = help.ballDrawingTop
                    contentLayout.translationX = (contentLayout.width - help.ballDrawingLength + help.ballEdgeEmbed - dp2px(3)).toFloat()

                    val interpolator = LinearInterpolator()
                    fanImageView.animate().rotation(99 * 360f).setDuration(99L * 100L).setInterpolator(interpolator).start()
                    contentLayout.animate().translationX(0f).setDuration(666).setInterpolator(interpolator).start()

                    window.decorView.alpha = 1f

                    scope.launch {
                        delay(2000)
                        kotlin.runCatching {
//                                HomeActivityTransfer.launch(
//                                    this@QuickBoostRouteActivity,
//                                    HomeActivityTransfer.From.QuickBoost
//                                )
                            finish()
                        }
                    }
                }
            }
        }.onFailure {
            finish()
        }
    }


    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onBackPressed() = Unit

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        kotlin.runCatching {
            findViewById<View>(R.id.contentLayout).animate().cancel()
            findViewById<View>(R.id.fanImageView).animate().cancel()
        }
    }

}