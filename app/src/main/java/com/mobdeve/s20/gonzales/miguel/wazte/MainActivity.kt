package com.mobdeve.s20.gonzales.miguel.wazte

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    lateinit var btnLogout:Button
    lateinit var drawerLayout: DrawerLayout
    lateinit var exitBtn: Button
    lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val sideDrawerBtn: ImageButton = findViewById(R.id.sideDrawerBtn)

        // Sidebar buttons
        exitBtn = findViewById(R.id.exitBtn)

        exitBtn.setOnClickListener{
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // TODO: Logout btn

        sideDrawerBtn.setOnClickListener{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // display next location and distance of truck
        val locations = listOf(
            LocationAdapter.Location("Camatchile St.", "2km"),
            LocationAdapter.Location("Andrew St.", "10km")
        )
        val recyclerView = findViewById<RecyclerView>(R.id.locationRecyclerView)
        recyclerView.adapter = LocationAdapter(locations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        /*
        * Set up btnLogout behavior, on click, destroys main activity, and brings user back to login
        * screen.
        * */
        btnLogout = findViewById(R.id.logoutBtn)
        btnLogout.setOnClickListener{
            finish()
        }
    }
}