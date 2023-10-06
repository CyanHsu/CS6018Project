package com.example.paintproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

class SimpleViewModel:ViewModel() {

    //Model
    public var bitmap = Bitmap.createBitmap(1080, 2200, Bitmap.Config.ARGB_8888)
    public val bitmapCanvas = Canvas(bitmap)
    //    private val paint = Paint()
//    private val rect: Rect by lazy { Rect(0,0,width, height) }
    @RequiresApi(Build.VERSION_CODES.O)
    private val _color : MutableLiveData<Color> = MutableLiveData(Color.valueOf(0f, 0f, 0f))
    var mDefaultColor = 0
    @RequiresApi(Build.VERSION_CODES.O)
    val color  = _color as LiveData<Color>
    var shape = "circle"
    var size_ = 10



    @RequiresApi(Build.VERSION_CODES.O)
    fun pickColor(){
        with(Random.Default) {
            _color.value = Color.valueOf(0F, 0F, 0F)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setColor(r: Int, g : Int, b:Int){
        with(Random.Default) {
            _color.value = Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setColor(color: Int){
        with(Random.Default) {
            _color.value = color.toColor()
            mDefaultColor = color
        }
    }
    fun setSize(size: Int){
        with(Random.Default) {
            size_ = size
        }
    }

    fun resetBitmap() {
        bitmap = Bitmap.createBitmap(1080, 2200, Bitmap.Config.ARGB_8888)
    }
}