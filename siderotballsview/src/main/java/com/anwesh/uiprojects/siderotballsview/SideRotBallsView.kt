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