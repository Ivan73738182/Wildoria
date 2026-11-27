package com.ivangames.lostland

import android.app.Activity
import android.os.Bundle
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.*
import android.widget.*
import kotlin.random.Random
import kotlin.math.*

class MainActivity : Activity() {

    private val bg = Color.rgb(13, 23, 21)
    private val green = Color.rgb(101, 214, 139)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullscreen()
        showMenu()
    }

    private fun fullscreen() {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) fullscreen()
    }

    private fun showMenu() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(20, 12, 20, 12)
            setBackgroundColor(bg)
        }

        val title = TextView(this).apply {
            text = "LOSTLAND"
            textSize = 42f
            typeface = Typeface.create("sans-serif-black", 1)
            setTextColor(green)
            gravity = Gravity.CENTER
            letterSpacing = 0.16f
        }

        val subtitle = TextView(this).apply {
            text = "A PIXEL SURVIVAL ADVENTURE"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)
        space(root, 24)

        menuButton(root, "НОВАЯ ИГРА") {
            setContentView(GameView())
        }

        menuButton(root, "ПОДКЛЮЧИТЬСЯ К LAN") {
            message("LAN-мультиплеер готовится!")
        }

        menuButton(root, "НАСТРОЙКИ") {
            message("Настройки появятся позже!")
        }

        space(root, 15)

        root.addView(TextView(this).apply {
            text = "IVAN GAMES • VERSION 1.2"
            textSize = 11f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
        })

        setContentView(root)
        fullscreen()
    }

    private fun menuButton(
        root: LinearLayout,
        text: String,
        action: () -> Unit
    ) {
        val button = TextView(this).apply {
            this.text = text
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(Color.rgb(39, 91, 64))
                cornerRadius = 12f
            }
            setOnClickListener { action() }
        }

        root.addView(button, LinearLayout.LayoutParams(
            290, 58
        ).apply {
            setMargins(0, 5, 0, 5)
        })
    }

    private fun space(root: LinearLayout, height: Int) {
        root.addView(View(this),
            LinearLayout.LayoutParams(1, height))
    }

    private fun message(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    inner class GameView : View(this) {

        private val paint = Paint().apply {
            isAntiAlias = false
        }

        private val tile = 48f
        private val mapSize = 80
        private val world = Array(mapSize) { IntArray(mapSize) }

        private var playerX = mapSize * tile / 2f
        private var playerY = mapSize * tile / 2f

        private var moveX = 0f
        private var moveY = 0f
        private var joystickActive = false
        private var joyX = 0f
        private var joyY = 0f

        private var lastTime = System.currentTimeMillis()

        private val grass1 = Color.rgb(74, 139, 75)
        private val grass2 = Color.rgb(80, 148, 79)
        private val grass3 = Color.rgb(68, 128, 69)
        private val treeColor = Color.rgb(34, 91, 47)
        private val treeLight = Color.rgb(53, 121, 57)
        private val rockColor = Color.rgb(116, 121, 108)

        private val loop = object : Runnable {
            override fun run() {
                val now = System.currentTimeMillis()
                val dt = ((now - lastTime) / 16f).coerceIn(0f, 2f)
                lastTime = now

                val speed = 3.1f * dt

                playerX += moveX * speed
                playerY += moveY * speed

                playerX = playerX.coerceIn(tile, (mapSize - 1) * tile)
                playerY = playerY.coerceIn(tile, (mapSize - 1) * tile)

                invalidate()
                postOnAnimation(this)
            }
        }

        init {
            isFocusable = true

            val random = Random(73738182)

            for (y in 0 until mapSize) {
                for (x in 0 until mapSize) {
                    val edge = x < 2 || y < 2 ||
                        x >= mapSize - 2 || y >= mapSize - 2

                    world[y][x] = if (edge) {
                        4
                    } else {
                        val distance = hypot(
                            x - mapSize / 2f,
                            y - mapSize / 2f
                        )

                        val chance = random.nextInt(100)

                        when {
                            distance < 3 -> 0
                            chance < 13 -> 1
                            chance < 19 -> 2
                            else -> 0
                        }
                    }
                }
            }

            post(loop)
        }

        override fun onDetachedFromWindow() {
            removeCallbacks(loop)
            super.onDetachedFromWindow()
        }

        private fun rect(
            canvas: Canvas,
            color: Int,
            l: Float,
            t: Float,
            r: Float,
            b: Float
        ) {
            paint.color = color
            canvas.drawRect(l, t, r, b, paint)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            canvas.drawColor(Color.rgb(38, 91, 49))

            val cameraX = playerX - w / 2f
            val cameraY = playerY - h / 2f

            val startX = floor(cameraX / tile).toInt().coerceAtLeast(0)
            val startY = floor(cameraY / tile).toInt().coerceAtLeast(0)

            val endX = (ceil((cameraX + w) / tile).toInt() + 1)
                .coerceAtMost(mapSize)

            val endY = (ceil((cameraY + h) / tile).toInt() + 1)
                .coerceAtMost(mapSize)

            for (y in startY until endY) {
                for (x in startX until endX) {

                    val sx = x * tile - cameraX
                    val sy = y * tile - cameraY

                    val base = when ((x * 7 + y * 13) % 3) {
                        0 -> grass1
                        1 -> grass2
                        else -> grass3
                    }

                    rect(canvas, base, sx, sy, sx + tile + 1, sy + tile + 1)

                    when (world[y][x]) {
                        1 -> drawTree(canvas, sx, sy)
                        2 -> drawRock(canvas, sx, sy)
                        4 -> rect(
                            canvas,
                            Color.rgb(38, 83, 106),
                            sx, sy, sx + tile, sy + tile
                        )
                    }

                    if (world[y][x] == 0 &&
                        (x * 17 + y * 23) % 19 == 0
                    ) {
                        rect(
                            canvas,
                            Color.rgb(160, 194, 94),
                            sx + 18, sy + 18,
                            sx + 23, sy + 23
                        )
                    }
                }
            }

            drawPlayer(canvas, w / 2f, h / 2f)

            drawHud(canvas)
            drawJoystick(canvas, w, h)
        }

        private fun drawTree(canvas: Canvas, x: Float, y: Float) {
            rect(canvas, Color.rgb(92, 60, 35),
                x + 20, y + 22, x + 29, y + 45)

            rect(canvas, treeColor,
                x + 5, y + 5, x + 43, y + 31)

            rect(canvas, treeLight,
                x + 11, y + 8, x + 35, y + 23)

            rect(canvas, Color.rgb(43, 105, 49),
                x + 8, y + 24, x + 39, y + 35)
        }

        private fun drawRock(canvas: Canvas, x: Float, y: Float) {
            rect(canvas, Color.rgb(70, 77, 70),
                x + 9, y + 25, x + 40, y + 40)

            rect(canvas, rockColor,
                x + 13, y + 17, x + 36, y + 35)

            rect(canvas, Color.rgb(158, 163, 145),
                x + 18, y + 18, x + 29, y + 23)
        }

        private fun drawPlayer(canvas: Canvas, x: Float, y: Float) {
            // Shadow
            rect(canvas, Color.rgb(35, 69, 37),
                x - 12, y + 12, x + 12, y + 17)

            // Legs
            rect(canvas, Color.rgb(53, 64, 86),
                x - 9, y + 5, x - 2, y + 15)

            rect(canvas, Color.rgb(53, 64, 86),
                x + 2, y + 5, x + 9, y + 15)

            // Body
            rect(canvas, Color.rgb(46, 105, 160),
                x - 10, y - 9, x + 10, y + 8)

            rect(canvas, Color.rgb(87, 155, 190),
                x - 8, y - 8, x + 7, y - 3)

            // Head
            rect(canvas, Color.rgb(222, 175, 125),
                x - 8, y - 22, x + 8, y - 9)

            // Hair
            rect(canvas, Color.rgb(71, 48, 34),
                x - 9, y - 24, x + 9, y - 19)

            rect(canvas, Color.rgb(71, 48, 34),
                x - 8, y - 20, x - 4, y - 15)
        }

        private fun drawHud(canvas: Canvas) {
            paint.color = Color.argb(175, 12, 24, 19)
            canvas.drawRoundRect(
                15f, 15f, 190f, 79f,
                12f, 12f, paint
            )

            paint.color = Color.WHITE
            paint.textSize = 17f
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("LOSTLAND", 27f, 39f, paint)

            paint.textSize = 12f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("DAY 1  •  FOREST", 27f, 61f, paint)

            paint.color = Color.argb(180, 15, 25, 20)
            canvas.drawRoundRect(
                width - 112f, 15f,
                width - 15f, 55f,
                10f, 10f, paint
            )

            paint.color = Color.WHITE
            paint.textSize = 14f
            canvas.drawText(
                "MENU",
                width - 89f, 41f, paint
            )
        }

        private fun drawJoystick(
            canvas: Canvas,
            w: Float,
            h: Float
        ) {
            joyX = if (joystickActive) joyX else 86f
            joyY = if (joystickActive) joyY else h - 88f

            paint.color = Color.argb(85, 10, 20, 15)
            canvas.drawCircle(86f, h - 88f, 59f, paint)

            paint.color = Color.argb(100, 220, 240, 220)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawCircle(86f, h - 88f, 59f, paint)
            paint.style = Paint.Style.FILL

            paint.color = Color.argb(170, 101, 214, 139)
            canvas.drawCircle(joyX, joyY, 25f, paint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val x = event.x
            val y = event.y
            val h = height.toFloat()
            val w = width.toFloat()

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_POINTER_DOWN -> {

                    if (x > w - 125 && y < 75) {
                        showMenu()
                        return true
                    }

                    if (x < w * 0.4f && y > h * 0.5f) {
                        joystickActive = true
                        updateJoystick(x, y, h)
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (joystickActive) {
                        updateJoystick(x, y, h)
                    }
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_POINTER_UP,
                MotionEvent.ACTION_CANCEL -> {
                    joystickActive = false
                    moveX = 0f
                    moveY = 0f
                    joyX = 86f
                    joyY = h - 88f
                }
            }

            return true
        }

        private fun updateJoystick(x: Float, y: Float, h: Float) {
            val centerX = 86f
            val centerY = h - 88f

            val dx = x - centerX
            val dy = y - centerY

            val distance = hypot(dx, dy)
            val maxDistance = 48f
            val factor = if (distance > maxDistance) {
                maxDistance / distance
            } else {
                1f
            }

            joyX = centerX + dx * factor
            joyY = centerY + dy * factor

            moveX = (dx / maxDistance).coerceIn(-1f, 1f)
            moveY = (dy / maxDistance).coerceIn(-1f, 1f)

            if (hypot(moveX, moveY) > 1f) {
                val length = hypot(moveX, moveY)
                moveX /= length
                moveY /= length
            }
        }
    }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        showMenu()
    }
}
