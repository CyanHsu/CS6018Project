package com.example.paintproject

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColor
import androidx.fragment.app.activityViewModels
import com.example.paintproject.databinding.FragmentDrawBinding
import yuku.ambilwarna.AmbilWarnaDialog


class DrawFragment : Fragment() {



    private fun showDialog(title: String) {
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
                    // change the mDefaultColor to
                    // change the GFG text color as
                    // it is returned when the OK
                    // button is clicked from the
                    // color picker dialog

                    viewModel.setColor(color)

                }
            })
        colorPickerDialogue.show()
    }





    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDrawBinding.inflate(inflater)

        val viewModel: SimpleViewModel by activityViewModels()
//        mDefaultColor = viewModel.color.value!!
        binding.customView.setBitMap(viewModel.bitmap)
//        binding.customView.drawBackGround()
//        binding.customView.drawCircle(viewModel.color.value!!)

//        viewModel.color.observe(viewLifecycleOwner){
//
//        }

        binding.colorBtn.setOnClickListener {

            openColorPickerDialogue()
        }

        binding.customView.setOnTouchListener { v, event ->

            binding.customView.draw(viewModel.color.value!!,event,viewModel.shape)

            true
        }


        binding.backBtn.setOnClickListener {
            val homeFragment = HomeFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView, homeFragment, "home_tag")
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.shapeBtn.setOnClickListener {
                showDialog("Shape")

        }





        return binding.root
    }

}

