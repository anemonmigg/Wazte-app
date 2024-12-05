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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {
    lateinit var etLoginUsername:EditText
    lateinit var tvLoginUsernameError:TextView
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

        // Access views
        etLoginUsername = findViewById(R.id.etLoginUsername)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        tvLoginUsernameError = findViewById(R.id.tvLoginUsernameError)
        tvLoginPasswordError = findViewById(R.id.tvLoginPasswordError)

        // Initialize errors as invisible
        var error_flag = 0 // if has a value of 1, that means there is an error
        tvLoginUsernameError.visibility = View.INVISIBLE
        tvLoginPasswordError.visibility = View.INVISIBLE

        /*
        * Setup btnLogin behavior, on click, does the following:
        * - Submits data in EditText to database, if there are errors, display errors in
        * corresponding TextViews, if no errors, user is redirected to MainActivity
        * */
        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {

            // Check for empty inputs
            if (etLoginUsername.text.toString().isEmpty()) {
                error_flag = 1
                tvLoginUsernameError.text = getString(R.string.field_cannot_be_empty)
                tvLoginUsernameError.visibility = View.VISIBLE
            }
            else {
                error_flag = 0
                tvLoginUsernameError.visibility = View.INVISIBLE
            }

            if (etLoginPassword.text.toString().isEmpty()) {
                error_flag = 1
                tvLoginPasswordError.text = getString(R.string.field_cannot_be_empty)
                tvLoginPasswordError.visibility = View.VISIBLE
            }
            else {
                error_flag = 0
                tvLoginPasswordError.visibility = View.INVISIBLE
            }

            // Query database for value under "username" to check if it exists, and if it does,
            // check if password is correct
            try {
                val db = Firebase.firestore
                db.collection("users")
                    .whereEqualTo("name", etLoginUsername.text.toString())
                    .get()
                    .addOnSuccessListener { result ->
                        if(result.isEmpty) {
                            error_flag = 1
                            tvLoginUsernameError.text = getString(R.string.login_username_not_found)
                            tvLoginUsernameError.visibility = View.VISIBLE
                        }
                        else {
                            error_flag = 0
                            tvLoginUsernameError.visibility = View.INVISIBLE
                            for (document in result) {
                                val storedPassword = document.getString("password")
                                if (storedPassword != etLoginPassword.text.toString()) {
                                    error_flag = 1
                                    tvLoginPasswordError.text = getString(R.string.login_password_incorrect)
                                    tvLoginPasswordError.visibility = View.VISIBLE
                                }
                                else {
                                    error_flag = 0
                                    // Inside the success block when the password is correct
                                    val intent = Intent(this, MainActivity::class.java)
                                    // Pass the username as an extra in the Intent
                                    intent.putExtra("username", etLoginUsername.text.toString())
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                // Although naive, good enough for now
                // Read entire database for username and password

            }
            catch (e:Error) {

            }
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