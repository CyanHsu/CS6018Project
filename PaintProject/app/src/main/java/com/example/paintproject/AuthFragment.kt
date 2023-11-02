package com.example.paintproject

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast


import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.paintproject.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth


class AuthFragment : Fragment() {

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
                    Auth(viewModel)
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
    fun Auth(vm: SimpleViewModel) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text("Little Painter",
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 250.dp)
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Row {
                LoginButton() {
                   logInDialog("Log In", vm)
                }
                Spacer(modifier = Modifier.padding(16.dp))
                SignUpButton() {

                    signUpDialog("Sign Up")
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))

            GuestButton(){
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }
    @Preview
    @Composable
    fun authPre(){
        Auth(SimpleViewModel())
    }

    @Composable
    fun GuestButton(onClick: () -> Unit) {
        FilledTonalButton(onClick = { onClick() }, shape = RoundedCornerShape(10)
            ) {
            Text("as Guest")
        }
    }

    @Composable
    fun LoginButton(onClick: () -> Unit) {
        Button(onClick = { onClick() }) {
            Text("Log in")
        }
    }

    @Composable
    fun SignUpButton(onClick: () -> Unit) {
        Button(onClick = { onClick() }) {
            Text("Sign up")
        }
    }


    private fun signUpDialog(title: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Create your account")

        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL

        val usernameEditText = EditText(activity)
        usernameEditText.hint = "Email"
        layout.addView(usernameEditText)

        val passwordEditText = EditText(activity)
        passwordEditText.hint = "Password"
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(passwordEditText)

//        val messageTextView = TextView(activity)
//        layout.addView(messageTextView)

        builder.setView(layout)

        builder.setPositiveButton("Sign Up") { dialog, which ->
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            activity?.let {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) {
//                            val user = FirebaseAuth.getInstance().currentUser
                            Toast.makeText(it, "Account created", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()

                        } else {
//                            messageTextView.text = "Invalid user name or password"
                            Toast.makeText(it, "Invalid user name or password", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun logInDialog(title: String, vm: SimpleViewModel) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Log in to continue")

        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL

        val usernameEditText = EditText(activity)
        usernameEditText.hint = "Email"
        layout.addView(usernameEditText)
        val passwordEditText = EditText(activity)
        passwordEditText.hint = "Password"
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(passwordEditText)

        builder.setView(layout)

        builder.setPositiveButton("Log In") { dialog, which ->
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            activity?.let {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) {
//                            val user = FirebaseAuth.getInstance().currentUser
                            vm.isGuest = false
                            findNavController().navigate(R.id.homeFragment)
                            dialog.dismiss()

                        } else {
                            Toast.makeText(it, "Invalid user name or password", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }


}

