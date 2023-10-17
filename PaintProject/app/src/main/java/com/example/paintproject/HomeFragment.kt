package com.example.paintproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.paintproject.databinding.FragmentHomeBinding
import java.io.File

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater,container,false)
        val viewModel : SimpleViewModel by activityViewModels()
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme{
                    AppNavi(viewModel)
                }
            }
        }
//
//        binding.clickBtn.setOnClickListener() {
//            val drawFragment = DrawFragment()
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragmentContainerView, drawFragment, "draw_tag")
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
//        return binding.root    }

}

@Composable
fun AppNavi(vm: SimpleViewModel) {
    Column {
        Text("Phase 2")

        Spacer(modifier = Modifier.padding(16.dp))

        DrawButton() {
            vm.resetBitmap()
            findNavController().navigate(R.id.drawFragment)

        }
        Spacer(modifier = Modifier.padding(16.dp))

        ResumeButton(){
            findNavController().navigate(R.id.drawFragment)
        }
        Spacer(modifier = Modifier.padding(16.dp))

        LoadButton(){
//             Load bitmap from storage
                    var loadedBitmap = loadFromStorage()
                    Log.d("DEBUG LOAD", "Bitmap Loaded: $loadedBitmap")

                    if (loadedBitmap != null) {
                        Log.d("DEBUG LOAD", "Setting bitmap to canvas")
                        vm.bitmap = loadedBitmap
//                        binding.customView.setLoadBitmap(loadedBitmap)
                        Log.d("DEBUG LOAD", "Finished bitmap to canvas")
                    }

                    Toast.makeText(context, "Bitmap loaded", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.drawFragment)
        }
    }
}

@Composable
fun DrawButton(onClick: () -> Unit) {

    Button(onClick = { onClick() }) {
        Text("New Drawing")
    }
}


@Composable
fun ResumeButton(onClick: () -> Unit) {

    Button(onClick = { onClick() }) {
        Text("Resume drawing")
    }
}


@Composable
fun LoadButton(onClick: () -> Unit) {

    Button(onClick = { onClick() }) {
        Text("Load saved drawing")
    }
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