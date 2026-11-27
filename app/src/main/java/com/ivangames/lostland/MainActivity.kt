package com.ivangames.lostland

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val backgroundColor = Color.rgb(16, 26, 25)
    private val accentColor = Color.rgb(101, 214, 139)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 20, 32, 20)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "LOSTLAND"
            textSize = 42f
            setTextColor(accentColor)
            gravity = Gravity.CENTER
            letterSpacing = 0.15f
        }

        val subtitle = TextView(this).apply {
            text = "A PIXEL SURVIVAL ADVENTURE"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)

        addSpace(root, 28)

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
            text = "IVAN GAMES  •  VERSION 1.0"
            textSize = 10f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
        }

        root.addView(footer)

        setContentView(root)
    }

    private fun addGameButton(
        root: LinearLayout,
        text: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            this.text = text
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)
            backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    Color.rgb(39, 91, 64)
                )
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            260,
            54
        ).apply {
            setMargins(0, 6, 0, 6)
        }

        root.addView(button, params)
    }

    private fun addSpace(root: LinearLayout, height: Int) {
        root.addView(
            android.view.View(this),
            LinearLayout.LayoutParams(1, height)
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
