package com.example.paintproject

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.geometry.Offset
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.paintproject.databinding.FragmentDrawBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.ContentType
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.util.InternalAPI
import kotlinx.coroutines.launch

import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.json.defaultSerializer
import io.ktor.client.plugins.kotlinx.serializer.KotlinxSerializer
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.*



class DrawFragment : Fragment() {
    var drawWithSensor = false

    private val STORAGE_PERMISSION_CODE: Int = 1000

    private fun sizeDialog() {
        val dialog = activity?.let { Dialog(it) }
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.size_layout)
            dialog.window?.setLayout(800, 800)
        }
        val viewModel: SimpleViewModel by activityViewModels()
        var seekBar = dialog?.findViewById(R.id.seekBar) as SeekBar
        seekBar.max = 100
        seekBar.min = 10
        seekBar.progress = viewModel.size_
        var sizeText = dialog.findViewById(R.id.sizeText) as TextView
        sizeText.text = viewModel.size_.toString()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                sizeText.text = progress.toString();
                viewModel.setSize(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                dialog.dismiss()
            }

        })

        dialog.show()
    }

    // Dialog for changing shape
    private fun shapeDialog(title: String) {
        val dialog = activity?.let { Dialog(it) }
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.shape_layout)
            dialog.window?.setLayout(800, 800)
        }
        val viewModel: SimpleViewModel by activityViewModels()

        val body = dialog?.findViewById(R.id.body) as TextView
        body.text = title

        val circleBtn = dialog.findViewById(R.id.circleBtn) as Button
        circleBtn.setOnClickListener {
            viewModel.shape = "circle"
            dialog.dismiss()
        }

        val rectBtn = dialog.findViewById(R.id.rectBtn) as Button
        rectBtn.setOnClickListener {
            viewModel.shape = "rect"
            dialog.dismiss()
        }

        dialog.show()
    }

    // Dialogue for picking colors
    @OptIn(ExperimentalStdlibApi::class)
    fun openColorPickerDialogue() {
        val viewModel: SimpleViewModel by activityViewModels()

        // the AmbilWarnaDialog callback needs 3 parameters
        // one is the context, second is default color,
        val colorPickerDialogue = AmbilWarnaDialog(context, viewModel.mDefaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    // leave this function body as
                    // blank, as the dialog
                    // automatically closes when
                    // clicked on cancel button
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    viewModel.setColor(color)
                }
            })
        colorPickerDialogue.show()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDrawBinding.inflate(inflater)
        val sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!!

        val viewModel: SimpleViewModel by activityViewModels()
        binding.customView.setBitMap(viewModel.bitmap)

        binding.colorBtn.setOnClickListener {

            openColorPickerDialogue()
        }


        // The touch listener for drawing something
        binding.customView.setOnTouchListener { v, event ->

            binding.customView.draw(
                viewModel.color.value!!,
                event,
                viewModel.shape,
                viewModel.size_
            )

            true
        }




        binding.ballBtn.setOnClickListener {
            drawWithSensor = !drawWithSensor
            if (drawWithSensor) {
                drawWithOrientation(binding.customView, viewModel, gravitySensor, sensorManager)
            }
        }

        // Button back to home page
        binding.backBtn.setOnClickListener {
//            val homeFragment = HomeFragment()
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragmentContainerView, homeFragment, "home_tag")
//            transaction.addToBackStack(null)
//            transaction.commit()
            findNavController().navigate(R.id.homeFragment)
        }

        binding.shapeBtn.setOnClickListener {
            shapeDialog("Shape")

        }

        binding.sizeBtn.setOnClickListener {
            sizeDialog()
        }

        binding.resetBtn.setOnClickListener {
            binding.customView.drawBackGround()
            viewModel.resetPosition()
        }

        binding.shareBtn.setOnClickListener{
            openFilePicker()
        }

        binding.optionsBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Save the paining?")
                .setPositiveButton("Save") { _, _ ->
                    // Save bitmap to storage'
                    val savebitmap = binding.customView.bitmap
                    Log.d("DEBUG SAVE", "Bitmap Retrieved: $savebitmap")

                    val filePath = saveToStorage(savebitmap)
                    Toast.makeText(context, "Bitmap saved to $filePath", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("DEBUG SAVE", "Saved to $filePath")
                }

//                    // Load bitmap from storage
//                    var loadedBitmap = loadFromStorage()
//                    Log.d("DEBUG LOAD", "Bitmap Loaded: $loadedBitmap")
//
//                    if (loadedBitmap != null) {
//                        Log.d("DEBUG LOAD", "Setting bitmap to canvas")
//                        viewModel.bitmap = loadedBitmap
//                        binding.customView.setLoadBitmap(loadedBitmap)
//                        Log.d("DEBUG LOAD", "Finished bitmap to canvas")
//                    }
//
//                    Toast.makeText(context, "Bitmap loaded", Toast.LENGTH_SHORT).show()
//                }
                .show()
        }

        return binding.root
    }

    fun getScreenOrientation(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return windowManager.defaultDisplay.rotation
    }

    fun drawWithOrientation(
        customView: CustomView,
        viewModel: SimpleViewModel,
        gravitySensor: Sensor,
        sensorManager: SensorManager
    ) {

        getGravityData(gravitySensor, sensorManager, viewModel)

//        var position = Offset(540f, 1100f)
        viewModel.offset.observe(requireActivity()) {
            Log.d("start position: ", it.x.toString() + ", " + it.y.toString())
//                       position =  Offset(position.x - it.x, position.y + it.y)
            if (drawWithSensor) {
                customView.drawBySensor(
                    viewModel.color.value!!,
                    viewModel.position, viewModel.shape, viewModel.size_
                )
            }

        }

//                    position = when (o) {
//                        1 -> {
//                            Offset(
//                                position.x + value[1] / 2,
//                                position.y + value[0] / 2
//                            )
//                        }
//                        3 -> {
//                            Offset(
//                                position.x - value[1] / 2,
//                                position.y - value[0] / 2
//                            )
//                        }
//                        else -> {
//                            Offset(position.x - value[0]/2, position.y + value[1]/2)
//                        }
//                    }
//                }
//            }
    }

    private fun getGravityData(
        gravitySensor: Sensor,
        sensorManager: SensorManager,
        viewModel: SimpleViewModel
    ) {

        val listener = object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                if (p0 != null) {
//                        viewModel.setPosX(  p0.values[0])
//                        viewModel.setPosY(  p0.values[0])
                    if (drawWithSensor)
                        viewModel.setOffset(Offset(p0.values[0], p0.values[1]))
//                        val z = p0.values[2]
//
//                        emit(p0.values)
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }
        }
        if (drawWithSensor) {
            sensorManager.registerListener(
                listener,
                gravitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else {
//            awaitClose {
//                sensorManager.unregisterListener(listener)
//            }
        }

    }

    fun saveToStorage(bitmap: Bitmap): String {
        val picturesDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(picturesDirectory, "drawing_${System.currentTimeMillis()}.png")
        Log.d("saving directory", picturesDirectory.toString())
        Log.d("file name", "drawing_${System.currentTimeMillis()}.png")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("DEBUG SAVE", "Error Saving Bitmap ", e)
        }
        return file.absolutePath
    }


//    fun loadFromStorage(): Bitmap? {
//        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        if (picturesDirectory == null) {
//            Toast.makeText(context, "Failed to access storage", Toast.LENGTH_SHORT).show()
//            return null
//        }
//        val file = File(picturesDirectory, "drawing.png")
//        if (!file.exists()) {
//            Toast.makeText(context, "No saved drawing found", Toast.LENGTH_SHORT).show()
//            return null
//        }
//        val options = BitmapFactory.Options()
//        options.inMutable = true
//        return BitmapFactory.decodeFile(file.absolutePath, options)
//    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val fileName_ = extractFileName(uri)
            Log.d("DEBUG LOAD", "File name: $fileName_")
            Log.d("DEBUG LOAD", "URI: $uri")

            val user = FirebaseAuth.getInstance().currentUser
            val sendName = user?.uid.toString() + "_" + fileName_

            val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(picturesDirectory,fileName_)
            if (user != null) {
                sendFileToServer(user.uid.toString(),sendName,file)
            }


            Toast.makeText(context, "$fileName_ has been shared" , Toast.LENGTH_SHORT).show()
//            findNavController().navigate(R.id.drawFragment)
        }
    }

    private fun extractFileName(uri: Uri?): String? {
        if (uri != null) {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val displayNameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val fileName = cursor.getString(displayNameColumnIndex)
                    cursor.close()
                    return fileName
                }
                cursor.close()
            }
        }
        return null
    }



    @OptIn(InternalAPI::class)
    fun sendFileToServer(userID: String, fileName: String, file: File) {

        val client = HttpClient(CIO)

        lifecycleScope.launch {
            try {
                val response: HttpResponse = client.post("http://10.0.2.2:8080/drawings/new") {
                    method = HttpMethod.Post


                    // Set the request headers
                    headers {
                        append(HttpHeaders.ContentType, io.ktor.http.ContentType.MultiPart.FormData.withParameter("boundary", "WebAppBoundary").toString())
                    }

                    // Create the multipart/form-data content
                    val formDataContent = MultiPartFormDataContent(
                        formData {
                            append("uid", userID)
                            append(
                                "image",file.readBytes(),
                                Headers.build {
                                    append(ContentType, "image/png")
                                    append(HttpHeaders.ContentDisposition, "form-data; filename=\"$fileName.png\"")
                                }

                            )
                        }
                    )

                    // Set the request body
                    body = formDataContent
                }

                // Handle the response
                println("Response status: ${response.status}")
            } catch (e: Exception) {
                // Handle exceptions
                e.printStackTrace()
            }

        }

    }

}
@Serializable
data class FileUploadRequest(val userID: String, val fileName: String, val file: File)


