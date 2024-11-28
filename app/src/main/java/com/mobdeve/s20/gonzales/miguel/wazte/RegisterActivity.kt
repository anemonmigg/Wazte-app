package com.mobdeve.s20.gonzales.miguel.wazte

import android.content.ContentValues.TAG
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {
    lateinit var etSignupUsername:EditText
    lateinit var  tvSignupUsernameError:TextView
    lateinit var etSignupPhone:EditText
    lateinit var tvSignupPhoneError:TextView
    lateinit var etSignupPassword:EditText
    lateinit var tvSignupPasswordError:TextView
    lateinit var etSignupConfirm:EditText
    lateinit var tvSignupConfirmError:TextView
    lateinit var btnSignup:Button
    lateinit var tvLogin:TextView

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

            // TODO: Check if username already exists in DB

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

            // Write data to DB
            val db = Firebase.firestore
            val user = hashMapOf(
                "name" to inputs[0].text.toString(),
                "phone_number" to inputs[1].text.toString(),
                "password" to inputs[2].text.toString()
            )
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(this, "Data added", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Data not added", Toast.LENGTH_LONG).show()
                }
            // Move to login activity
            // finish()
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
    if (password == confirmPassword) {
        return true
    }
    return false
}