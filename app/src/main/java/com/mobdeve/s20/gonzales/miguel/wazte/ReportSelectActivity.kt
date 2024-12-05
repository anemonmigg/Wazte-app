package com.mobdeve.s20.gonzales.miguel.wazte

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class ReportSelectActivity : AppCompatActivity() { // Remove the extra nested class
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportselect)

        // Set up click listeners for different sections
        val recurringBinLinearLayout = findViewById<LinearLayout>(R.id.recurring_bin_selector)
        recurringBinLinearLayout.setOnClickListener {
            val intent = Intent(this, RecurringLocationActivity::class.java)
            startActivity(intent)
        }

        val oneTimeBinLinearLayout = findViewById<LinearLayout>(R.id.onetime_bin_selector)
        oneTimeBinLinearLayout.setOnClickListener {
            val intent = Intent(this, OneTimeLocationActivity::class.java)
            startActivity(intent)
        }

        // Set up cancel button
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            finish() // Finish the activity and return to the previous one
        }
    }
}
