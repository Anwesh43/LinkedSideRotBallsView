package com.anwesh.uiprojects.siderotballsview

/**
 * Created by anweshmishra on 21/07/19.
 */

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Canvas
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity

val nodes : Int = 5
val scGap : Float = 0.05f
val scDiv : Double = 0.51
val strokeFactor : Int = 90
val sizeFactor : Float = 2f
val foreColor : Int = Color.GREEN
val backColor : Int = Color.parseColor("#BDBDBD")
val rFactor : Float = 2.2f

fun Int.inverse() : Float = 1f / this
fun Float.scaleFactor() : Float = Math.floor(this / scDiv).toFloat()
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.mirrorValue(a : Int, b : Int) : Float {
    val k : Float = scaleFactor()
    return (1 - k) * a.inverse() + k * b.inverse()
}
fun Float.updateValue(dir : Float, a : Int, b : Int) : Float = mirrorValue(a, b) * dir * scGap

fun Canvas.drawSideRotBall(i : Int, sc : Float, size : Float, r : Float, paint : Paint) {
    paint.style = Paint.Style.STROKE
    drawCircle(0f, 0f, r, paint)
    paint.style = Paint.Style.FILL
    drawCircle(0f, 0f, r * sc, paint)
    for (j in 1..(nodes - 1 - i)) {
        drawLine(size * (j - 1), 0f, size * j, 0f, paint)
        paint.style = Paint.Style.STROKE
        drawCircle(size * j, 0f, r, paint)
        paint.style = Paint.Style.FILL
        drawCircle(size * j, 0f, r * sc, paint)
    }
}
fun Canvas.drawSRBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    val r : Float = size / rFactor
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    paint.color = foreColor
    save()
    translate(gap * (i + 1), h / 10)
    paint.style = Paint.Style.STROKE
    drawCircle(0f, 0f, r, paint)
    paint.style = Paint.Style.FILL
    drawCircle(0f, 0f, r * scale, paint)
    save()
    rotate(90f * sc2)
    drawSideRotBall(i, scale.divideScale(1, 2), size, size / rFactor, paint)
    restore()
    restore()
}

class SideRotBallsView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateValue(dir, 1, 1)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SRBNode(var i : Int, val state : State = State()) {

        private var next : SRBNode? = null
        private var prev : SRBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SRBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSRBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SRBNode {
            var curr : SRBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SideRotBalls(var i : Int) {

        private val root : SRBNode = SRBNode(0)
        private var curr : SRBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SideRotBallsView) {

        private val animator : Animator = Animator(view)
        private val srb : SideRotBalls = SideRotBalls(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            srb.draw(canvas, paint)
            animator.animate {
                srb.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            srb.startUpdating {
                animator.start()
            }
        }
    }
}