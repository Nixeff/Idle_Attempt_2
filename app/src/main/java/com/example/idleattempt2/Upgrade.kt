package com.example.idleattempt2

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import java.math.BigDecimal

class Upgrade (var cost: BigDecimal,
               var target: Int,
               var type: String,
               var amount: BigDecimal, // Pos
               var title: String,
               var desc: String,
){
    var btn = Rect(0,0,0,0)

    fun render(canvas: Canvas, paint:Paint, x: Float, y: Float){
        btn = Rect(x.toInt(),y.toInt()+70,x.toInt()+240,y.toInt()+140)
        canvas.drawText(title,x,y,paint)
        canvas.drawText(desc,x,y+60f,paint)
        canvas.drawText("$ ${cost.toString()}",x+260f,y+120f,paint)

        var upPaint = Paint()
        upPaint.color = Color.argb(255,255,193,177)

        canvas.drawRect(btn,upPaint)
    }

    fun checkClick(xTouch: Int, yTouch: Int): Boolean{
        return btn.contains(xTouch, yTouch)
    }





}