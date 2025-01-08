package com.example.project_sma

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_sma.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        binding.SignUpButton.setOnClickListener{
            val signUpUsername = binding.signUpUsername.text.toString()
            val signUpPassword = binding.signUpPassword.text.toString()

            if(signUpUsername.isNotEmpty() && signUpPassword.isNotEmpty()){
                signUpUser(signUpUsername,signUpPassword)
            } else
            {
                Toast.makeText(this@SignUpActivity,"All fields are mandatory",Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirect.setOnClickListener{
            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
            finish()
        }
    }

    private fun signUpUser(email: String, password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(this@SignUpActivity,"Sign Up successful",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SignUpActivity, "Authentication failed.", Toast.LENGTH_SHORT).show();

                }
            }
    }


}