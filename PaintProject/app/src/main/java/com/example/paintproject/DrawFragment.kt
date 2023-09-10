package com.example.paintproject

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.example.paintproject.databinding.FragmentDrawBinding
import yuku.ambilwarna.AmbilWarnaDialog



class DrawFragment : Fragment() {


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
            val homeFragment = HomeFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView, homeFragment, "home_tag")
            transaction.addToBackStack(null)
            transaction.commit()
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

        return binding.root
    }

}

