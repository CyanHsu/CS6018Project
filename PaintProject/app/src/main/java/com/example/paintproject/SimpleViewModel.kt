package com.example.paintproject

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.toColor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class SimpleViewModel:ViewModel() {

    //Model
    public var bitmap = Bitmap.createBitmap(1080, 2200, Bitmap.Config.ARGB_8888)
    public val bitmapCanvas = Canvas(bitmap)
    //    private val paint = Paint()
//    private val rect: Rect by lazy { Rect(0,0,width, height) }
    private val _color : MutableLiveData<Color> = MutableLiveData(Color.valueOf(0f, 0f, 0f))
    var mDefaultColor = 0
    val color  = _color as LiveData<Color>
    var shape = "circle"
    var size_ = 5

    private val _screenOrientation = MutableLiveData<Int>()
    val screenOrientation = _screenOrientation as LiveData<Int>

    private  val _offset = MutableLiveData<Offset>()
    private val _posX = MutableLiveData<Float>()
    private val _posY = MutableLiveData<Float>()
    var offset = _offset as LiveData<Offset>
    var posX  = _posX as LiveData<Float>
    var posY = _posY  as LiveData<Float>

    var position = Offset(540f, 1100f)

    var isGuest = true


    fun pickColor(){
        with(Random.Default) {
            _color.value = Color.valueOf(0F, 0F, 0F)
        }
    }
    fun setColor(r: Int, g : Int, b:Int){
        with(Random.Default) {
            _color.value = Color.valueOf(r.toFloat(), g.toFloat(), b.toFloat())
        }
    }
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

    fun setScreenOrientation(orientation: Int) {
        _screenOrientation.value = orientation
    }
    fun setPosX(posX: Float) {
        _posX.value = posX
    }
    fun setPosY(posY: Float) {
        _posY.value = posY
    }
    fun setOffset(off : Offset){
        _offset.value = off
        position =  Offset(position.x - _offset.value!!.x, position.y + _offset.value!!.y)
    }
    fun resetPosition(){
        position = Offset(540f, 1100f)
    }
}