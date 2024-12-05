package com.mobdeve.s20.gonzales.miguel.wazte

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterActivity : AppCompatActivity() {
    private lateinit var etSignupUsername:EditText
    private lateinit var  tvSignupUsernameError:TextView
    private lateinit var etSignupPhone:EditText
    private lateinit var tvSignupPhoneError:TextView
    private lateinit var etSignupPassword:EditText
    private lateinit var tvSignupPasswordError:TextView
    private lateinit var etSignupConfirm:EditText
    private lateinit var tvSignupConfirmError:TextView
    private lateinit var btnSignup:Button
    private lateinit var tvLogin:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Access necessary UI elements
        etSignupUsername = findViewById(R.id.etSignupUsername)
        tvSignupUsernameError = findViewById(R.id.tvSignupUsernameError)
        etSignupPhone = findViewById(R.id.etSignupPhone)
        tvSignupPhoneError = findViewById(R.id.tvSignupPhoneError)
        etSignupPassword = findViewById(R.id.etSignupPassword)
        tvSignupPasswordError = findViewById(R.id.tvSignupPasswordError)
        etSignupConfirm = findViewById(R.id.etSignupConfirm)
        tvSignupConfirmError = findViewById(R.id.tvSignupConfirmError)

        btnSignup = findViewById(R.id.btnSignup)
        tvLogin = findViewById(R.id.tvLogin)

        // Create arrays for EditText and TextView elements for easier iteration
        val inputs = arrayOf(
            etSignupUsername,
            etSignupPhone,
            etSignupPassword,
            etSignupConfirm
        )

        val errors = arrayOf(
            tvSignupUsernameError,
            tvSignupPhoneError,
            tvSignupPasswordError,
            tvSignupConfirmError
        )

        // Make error elements invisible initially
        for (error in errors) {
            error.visibility = View.INVISIBLE
        }

        tvLogin.setOnClickListener{
            finish()
        }

        /*
        * Setup behavior of btnSignup, on click, the following occurs:
        * - Contents of EditText are validated
        * - Display errors in TextViews if applicable
        *   - If no errors, check if username already exists in DB
        *   - If no username, write contents of EditText to DB as a single user
        * - On successful adding, return user to login activity
        */
        btnSignup.setOnClickListener{
            var error_flag = 0

            // Check if fields are empty
            for (i in inputs.indices) {
                if (inputs[i].text.toString().isBlank()) {
                    errors[i].text = getString(R.string.field_cannot_be_empty)
                    errors[i].visibility = View.VISIBLE
                    error_flag = 1
                }
                else {
                    errors[i].visibility = View.INVISIBLE
                }
            }

            // TODO: Implement phone number validation

            // Check if passwords match
            if (!passwordsMatch(inputs[2].text.toString(), inputs[3].text.toString())) {
                errors[2].text = getString(R.string.passwords_must_match)
                errors[3].text = getString(R.string.passwords_must_match)
                errors[2].visibility = View.VISIBLE
                errors[3].visibility = View.VISIBLE
                error_flag = 1
            }
            else {
                errors[2].visibility = View.INVISIBLE
                errors[3].visibility = View.INVISIBLE
            }

            // Check if password is 8 characters long
            if (inputs[2].text.toString().length < 8) {
                errors[2].text = getString(R.string.password_too_short)
                errors[2].visibility = View.VISIBLE
                error_flag = 1
            }
            else {
                errors[2].visibility = View.INVISIBLE
            }

            val username = inputs[0].text.toString()
            val phoneNumber = inputs[1].text.toString()

            // If there are no errors, set error_flag to 0
            if(!containsEmptyInput(inputs) &&
                passwordsMatch(inputs[2].text.toString(), inputs[3].text.toString()) &&
                inputs[2].text.toString().length >= 8) {
                error_flag = 0
            }

            // If there are errors, do not execute the code below
            if (error_flag == 1) {
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val db = Firebase.firestore
                try {
                    val usernameQuery = db.collection("users")
                        .whereEqualTo("name", username)
                        .get()
                        .await()

                    if (!usernameQuery.isEmpty) {
                        // Username already exists
                        errors[0].text = getString(R.string.username_exists)
                        errors[0].visibility = View.VISIBLE
                        return@launch
                    } else {
                        errors[0].visibility = View.INVISIBLE
                    }

                    val phoneQuery = db.collection("users")
                        .whereEqualTo("phone_number", phoneNumber)
                        .get()
                        .await()

                    if (!phoneQuery.isEmpty) {
                        // Phone number already exists
                        errors[1].text = getString(R.string.phone_number_already_exists)
                        errors[1].visibility = View.VISIBLE
                        return@launch
                    } else {
                        errors[1].visibility = View.INVISIBLE
                    }

                    // Proceed with registration
                    val user = hashMapOf(
                        "name" to username,
                        "phone_number" to phoneNumber,
                        "password" to inputs[2].text.toString(),
                        "trashCount" to 0
                    )

                    try {
                        val result = db.collection("users").add(user).await()
                        Log.d(TAG, "DocumentSnapshot added with ID: ${result.id}")
                        Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_LONG).show()

                        // Redirect to login activity
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Log.w(TAG, "Error adding document", e)
                        Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking database for existing data", e)
                    Toast.makeText(this@RegisterActivity, "Error checking database. Please try again.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

// Defined helper functions
fun containsEmptyInput(inputs: Array<EditText>): Boolean {
    for (i in inputs.indices) {
        if (inputs[i].text.toString().isBlank()) {
            return true
        }
    }
    return false
}

fun passwordsMatch(password:String, confirmPassword:String): Boolean {
    return password == confirmPassword
}