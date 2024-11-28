package com.mobdeve.s20.gonzales.miguel.wazte

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    lateinit var etLoginUsername:EditText
    lateinit var tvLoginUsername:TextView
    lateinit var etLoginPassword:EditText
    lateinit var tvLoginPasswordError:TextView
    lateinit var btnLogin:Button
    lateinit var tvSignup:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*
        * Setup btnLogin behavior, on click, does the following:
        * - Submits data in EditText to database, if there are errors, display errors in
        * corresponding TextViews, if no errors, user is redirected to MainActivity
        * */
        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            // Move to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /*
        * Setup behavior of clickable signup link, on click start signup activity
        * */
        tvSignup = findViewById(R.id.tvSignup)
        tvSignup.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })
    }
}