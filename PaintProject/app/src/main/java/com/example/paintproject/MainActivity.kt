package com.example.paintproject

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView

import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.activity.ComponentActivity

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView


import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.replace

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent{
//            AppContent()
//        }
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainerView, HomeFragment()) // InitialFragment 是你的起始 Fragment
//                .commit()


        setContentView(R.layout.activity_main)
    }
}
@Composable
fun AppContent() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {

        val fragmentManager = LocalContext.current as FragmentManager


        AndroidView(
            factory = { context ->
                FragmentContainerView(context).apply {
                    id = R.id.fragmentContainerView
//                    fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, HomeFragment()).commit()
                }
            }
        )
    }
}


