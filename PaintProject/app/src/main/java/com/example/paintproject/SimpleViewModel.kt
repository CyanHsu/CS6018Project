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
    public val bitmap = Bitmap.createBitmap(1080, 2200, Bitmap.Config.ARGB_8888)
    private val bitmapCanvas = Canvas(bitmap)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveBitmap(context: Context) {
        Log.d("DEBUG", "Entered saveBitmap function")
        val publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(publicDirectory, "painting.png")

        if (file.exists()) {
            file.delete()
        }

        try {
            Log.d("DEBUG", "Attempting to save bitmap at: ${file.absolutePath}")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Log.d("DEBUG", "Bitmap successfully saved at: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ERROR", "Error while saving bitmap: ${e.message}")
        }

        val file2 = File("/storage/emulated/0/Pictures/painting.png")
        if(file2.exists()) {
            Log.d("DEBUG", "File exists at ${file.absolutePath}")
        } else {
            Log.e("ERROR", "File not found at ${file.absolutePath}")
        }
    }
}