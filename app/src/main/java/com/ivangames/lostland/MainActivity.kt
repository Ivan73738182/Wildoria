package com.ivangames.lostland

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val bg = Color.rgb(13, 23, 21)
    private val green = Color.rgb(101, 214, 139)
    private val white = Color.rgb(240, 245, 240)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(20), dp(12), dp(20), dp(12))
            setBackgroundColor(bg)
        }

        val title = TextView(this).apply {
            text = "LOSTLAND"
            textSize = 42f
            typeface = Typeface.create("sans-serif-black", Typeface.NORMAL)
            setTextColor(green)
            gravity = Gravity.CENTER
            letterSpacing = 0.16f
            includeFontPadding = false
        }

        val subtitle = TextView(this).apply {
            text = "A PIXEL SURVIVAL ADVENTURE"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
            includeFontPadding = false
        }

        root.addView(title)
        root.addView(subtitle)

        addSpace(root, 24)

        addGameButton(root, "НОВАЯ ИГРА") {
            showMessage("Создание мира скоро появится!")
        }

        addGameButton(root, "ПОДКЛЮЧИТЬСЯ К LAN") {
            showMessage("LAN-мультиплеер скоро появится!")
        }

        addGameButton(root, "НАСТРОЙКИ") {
            showMessage("Настройки скоро появятся!")
        }

        addSpace(root, 18)

        val footer = TextView(this).apply {
            text = "IVAN GAMES  •  VERSION 1.1"
            textSize = 11f
            setTextColor(Color.rgb(135, 150, 140))
            gravity = Gravity.CENTER
            includeFontPadding = false
        }

        root.addView(footer)

        setContentView(root)
    }

    private fun addGameButton(
        root: LinearLayout,
        text: String,
        action: () -> Unit
    ) {
        val button = TextView(this).apply {
            this.text = text
            textSize = 15f
            typeface = Typeface.create(
                "sans-serif-medium",
                Typeface.BOLD
            )
            setTextColor(white)
            gravity = Gravity.CENTER
            includeFontPadding = false
            isClickable = true
            isFocusable = true

            background = GradientDrawable().apply {
                setColor(Color.rgb(39, 91, 64))
                cornerRadius = dp(10).toFloat()
                setStroke(dp(1), Color.rgb(65, 130, 88))
            }

            setPadding(dp(12), 0, dp(12), 0)

            setOnClickListener {
                action()
            }

            setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        view.alpha = 0.7f
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        view.alpha = 1f
                    }
                }
                false
            }
        }

        val params = LinearLayout.LayoutParams(
            dp(290),
            dp(58)
        ).apply {
            setMargins(0, dp(5), 0, dp(5))
        }

        root.addView(button, params)
    }

    private fun addSpace(root: LinearLayout, height: Int) {
        root.addView(
            View(this),
            LinearLayout.LayoutParams(1, dp(height))
        )
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
