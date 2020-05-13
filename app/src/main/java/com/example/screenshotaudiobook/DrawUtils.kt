package com.example.screenshotaudiobook

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

fun drawRects(bitmap: Bitmap?, rects : List<Rect>) {
    if (bitmap != null){
        val canvas = Canvas(bitmap)
        var paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F
        rects.forEach {
            canvas.drawRect(it, paint)
        }
    }
}