package com.example.paintproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                    AppNavi()
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
fun AppNavi(){
    Column {
        Text("Phase 2")

        Spacer(modifier = Modifier.padding(16.dp))

        DrawButton() {
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
}