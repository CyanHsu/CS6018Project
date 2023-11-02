package com.example.paintproject

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.paintproject.databinding.FragmentHomeBinding
import java.io.File
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executors


class HomeFragment : Fragment() {

    private val viewModeller: SimpleViewModel by activityViewModels()
    private var fileName_: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater,container,false)
        val viewModel: SimpleViewModel by activityViewModels()

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme{
                    AppNavi(viewModel)
                }
            }
        }
    }



    @Composable
    fun AppNavi(vm: SimpleViewModel) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Start with",
                fontSize = 18.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 200.dp)
            )
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
                openFilePicker()
    //          Load bitmap from storage
                //val loadedBitmap = loadFromStorage("drawing.png")
//                val loadedBitmap = fileName_?.let { loadFromStorage(it) }
//                Log.d("DEBUG LOAD", "Bitmap Loaded: $loadedBitmap")
//
//                if (loadedBitmap != null) {
//                    Log.d("DEBUG LOAD", "Setting bitmap to canvas")
//                    vm.bitmap = loadedBitmap
////                        binding.customView.setLoadBitmap(loadedBitmap)
//                    Log.d("DEBUG LOAD", "Finished bitmap to canvas")
//                }
//
//                Toast.makeText(context, "Bitmap loaded", Toast.LENGTH_SHORT).show()
//                findNavController().navigate(R.id.drawFragment)
            }
            Spacer(modifier = Modifier.padding(16.dp))

            ShareButton(){
                findNavController().navigate(R.id.sharedDrawingFragment)

            }
            Spacer(modifier = Modifier.padding(80.dp))

            if(vm.isGuest) {
                LogInButton {
                    findNavController().navigate(R.id.authFragment)
                }
            }else {
                LogOutButton {
                    FirebaseAuth.getInstance().signOut()
                    vm.isGuest = true
                    findNavController().navigate(R.id.authFragment)
                }
            }

        }
    }
    @Preview
    @Composable
    fun AppNaviPre(){
        AppNavi(SimpleViewModel())
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
            Text("Resume Drawing")
        }
    }
    @Composable
    fun LoadButton(onClick: () -> Unit) {
        Button(onClick = { onClick() }) {
            Text("Load Drawing")
        }
    }
    @Composable
    fun ShareButton(onClick: () -> Unit) {
        FilledTonalButton(onClick = { onClick() }, shape = RoundedCornerShape(10)) {
            Text("Explore Gallery")
        }
    }

    @Composable
    fun LogOutButton(onClick: () -> Unit) {
        TextButton(onClick = { onClick() }) {
            Text("Log out & Back to Log in Page")
        }
    }
    @Composable
    fun LogInButton(onClick: () -> Unit) {
        TextButton(onClick = { onClick() }) {
            Text("Back to Log in Page")
        }
    }

    @Composable
    fun SignUpButton(onClick: () -> Unit) {
        Button(onClick = { onClick() }) {
            Text("Sign up")
        }
    }




    private fun loadFromStorage(fileName: String): Bitmap? {
        Log.d("DEBUG LOAD", "Loading from storage: $fileName")
        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        Log.d("DEBUG LOAD", picturesDirectory.toString())
        if (picturesDirectory == null) {
            Toast.makeText(context, "Failed to access storage", Toast.LENGTH_SHORT).show()
            Log.d("DEBUG LOAD", "No such directory")
            return null
        }
        val file = File(picturesDirectory, fileName)
        if (!file.exists()) {
            Toast.makeText(context, "No saved drawing found", Toast.LENGTH_SHORT).show()
            Log.d("DEBUG LOAD", "No such file")
            return null
        }
        val options = BitmapFactory.Options()
        options.inMutable = true
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

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
            fileName_ = extractFileName(uri)
            Log.d("DEBUG LOAD", "File name: $fileName_")
            Log.d("DEBUG LOAD", "URI: $uri")
            val loadedBitmap = loadFromStorage(fileName_!!)
            if (loadedBitmap != null) {
                Log.d("DEBUG LOAD", "Setting bitmap to canvas")
                viewModeller.bitmap = loadedBitmap
                Log.d("DEBUG LOAD", "Finished bitmap to canvas")
            }
            Toast.makeText(context, "Bitmap loaded", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.drawFragment)
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
}