package com.example.paintproject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.paintproject.databinding.FragmentDrawBinding
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class DrawFragment : Fragment() {

    private val STORAGE_PERMISSION_CODE: Int = 1000

    @RequiresApi(Build.VERSION_CODES.O)
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

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
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
            dialog.window?.setLayout(800,800)
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

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    viewModel.setColor(color)
                }
            })
        colorPickerDialogue.show()
    }





    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDrawBinding.inflate(inflater)

        val viewModel: SimpleViewModel by activityViewModels()
        binding.customView.setBitMap(viewModel.bitmap)

        binding.colorBtn.setOnClickListener {

            openColorPickerDialogue()
        }


        // The touch listener for drawing something
        binding.customView.setOnTouchListener { v, event ->

            binding.customView.draw(viewModel.color.value!!,event,viewModel.shape, viewModel.size_)

            true
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

        binding.resetBtn.setOnClickListener{
            binding.customView.drawBackGround()
        }

        binding.optionsBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Choose an option")
                .setPositiveButton("Save") { _, _ ->
                     // Save bitmap to storage'
                    val savebitmap = binding.customView.bitmap
                    Log.d("DEBUG SAVE", "Bitmap Retrieved: $savebitmap")

                    val filePath = saveToStorage(savebitmap)
                    Toast.makeText(context, "Bitmap saved to $filePath", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("DEBUG SAVE", "Saved to $filePath")
                }
                .setNegativeButton("Load") { _, _ ->
                    // Load bitmap from storage
                    var loadedBitmap = loadFromStorage()
                    Log.d("DEBUG LOAD", "Bitmap Loaded: $loadedBitmap")

                    if (loadedBitmap != null) {
                        Log.d("DEBUG LOAD", "Setting bitmap to canvas")
                        binding.customView.setLoadBitmap(loadedBitmap)
                        Log.d("DEBUG LOAD", "Finished bitmap to canvas")
                    }

                    Toast.makeText(context, "Bitmap loaded", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        return binding.root
    }

    fun saveToStorage(bitmap: Bitmap): String {
        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(picturesDirectory, "drawing.png")
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

    fun loadFromStorage(): Bitmap? {
        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (picturesDirectory == null) {
            Toast.makeText(context, "Failed to access storage", Toast.LENGTH_SHORT).show()
            return null
        }
        val file = File(picturesDirectory, "drawing.png")
        if (!file.exists()) {
            Toast.makeText(context, "No saved drawing found", Toast.LENGTH_SHORT).show()
            return null
        }
        val options = BitmapFactory.Options()
        options.inMutable = true
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

}

