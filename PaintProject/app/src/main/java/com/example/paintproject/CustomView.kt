package com.example.paintproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.fragment.app.activityViewModels

class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs)  {
    public lateinit var bitmap : Bitmap;
    private lateinit var bitmapCanvas : Canvas
    private val paint = Paint()
    private val rect: Rect by lazy { Rect(0,0,width, height) }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmap, null, rect, paint)
        if (canvas != null) {
            Log.e("canvas", "Canvas height = " + canvas.width)
        }
    }

    public fun drawBackGround(){
        paint.color = Color.WHITE
        bitmapCanvas.drawRect(0f,0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun drawCircle(color: Color){
//        paint.color = Color.WHITE
//        bitmapCanvas.drawRect(0f,0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        paint.color = color.toArgb()
        bitmapCanvas.drawCircle(0.5f*bitmap.width, 0.5f*bitmap.height,
            0.1f*bitmap.width, paint)
        invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun draw(color: Color, e : MotionEvent, shape : String , size : Int){
        paint.color = color.toArgb()
        if(shape == "circle" ) {
            bitmapCanvas.drawCircle(
                e.x * bitmap.width / width, e.y * bitmap.height / height,
                5f * size, paint
            )
        }
        else if(shape == "rect"){
            bitmapCanvas.drawRect(e.x  - 5f * size,e.y  - 10f * size, e.x  + 5f * size, e.y , paint)

        }

        invalidate()
    }

    public fun drawBySensor(color: Color, o:Offset, shape : String , size : Int){
        paint.color = color.toArgb()
        if(shape == "circle" ) {
            bitmapCanvas.drawCircle(
                o.x * bitmap.width / width, o.y * bitmap.height / height,
                5f * size, paint
            )
        }
        else if(shape == "rect"){
            bitmapCanvas.drawRect(o.x  - 5f * size,o.y  - 10f * size, o.x  + 5f * size, o.y , paint)

        }

        invalidate()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    public fun drawLine(color: Color, starX : Float, startY : Float, stopX : Float, stopY :Float){
//        paint.color = Color.WHITE
//        bitmapCanvas.drawRect(0f,0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        paint.color = color.toArgb()
        bitmapCanvas.drawLine(starX*1F, startY*1F, stopX*1F, stopY*1F,paint )
//        bitmapCanvas.drawCircle(e.x * bitmap.width/width , e.y * bitmap.height/ height ,
//            100f, paint)
//        Log.e("Pos", "bitmap.height = " + bitmap.height + ", bitmapCanvas = " + bitmapCanvas.height + ", mousePos = " + e.y )
        invalidate()
    }

    public fun setBitMap(b: Bitmap){
        bitmap = b
        bitmapCanvas = Canvas(bitmap)
    }

    fun setLoadBitmap(newBitmap: Bitmap) {
        setBitMap(newBitmap)
        invalidate()
    }

}