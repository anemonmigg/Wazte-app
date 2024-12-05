package com.mobdeve.s20.gonzales.miguel.wazte

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProvider
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState
import com.mapbox.maps.plugin.viewport.viewport

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var exitBtn: Button
    lateinit var permissionsManager: PermissionsManager
    lateinit var mapView: MapView
    lateinit var tvUserName: TextView
    lateinit var tvPickupCount: TextView
    lateinit var reportBinButton: Button

    private val db = Firebase.firestore

    var permissionsListener: PermissionsListener = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {

        }

        override fun onPermissionResult(granted: Boolean) {
            if (granted) {

                // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location

            } else {

                // User denied the permission

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("username")
        tvPickupCount = findViewById(R.id.tvPickupCount)
        reportBinButton = findViewById(R.id.reportBinButton)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserName.text = username

        drawerLayout = findViewById(R.id.drawer_layout)
        val sideDrawerBtn: ImageButton = findViewById(R.id.sideDrawerBtn)

        // Get trash count
        db.collection("users")
            .whereEqualTo("name", username)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Assuming there's only one user with this username
                    for (document in result) {
                        val userName = document.getString("name")
                        val pickupCount = document.getLong("pickupCount") ?: 0  // Default to 0 if not found

                        // Set the user details to the corresponding TextViews
                        tvUserName.text = userName
                        tvPickupCount.text = pickupCount.toString()
                    }
                } else {
                    // Handle case where no user is found with the given username
                    tvUserName.text = "User not found"
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure in fetching user data
                tvUserName.text = "Failed to load user"
            }

        // Sidebar buttons
        exitBtn = findViewById(R.id.exitBtn)

        exitBtn.setOnClickListener{
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        sideDrawerBtn.setOnClickListener{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val reportBinButton: Button = findViewById(R.id.reportBinButton)
        reportBinButton.setOnClickListener {
            val intent = Intent(this, ReportSelectActivity::class.java)
            startActivity(intent)
        }

        // Handle user permissions
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
        } else {
            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }

        // Make camera track user's position
        // transition to followPuckViewportState with default transition
        mapView = findViewById(R.id.mapView)
        val viewportPlugin = mapView.viewport

        val followPuckViewportState: FollowPuckViewportState = viewportPlugin.makeFollowPuckViewportState(
            FollowPuckViewportStateOptions.Builder()
                .bearing(FollowPuckViewportStateBearing.Constant(0.0))
                .padding(EdgeInsets(200.0 * resources.displayMetrics.density, 0.0, 0.0, 0.0))
                .build()
        )
        viewportPlugin.transitionTo(followPuckViewportState) { success ->
            // the transition has been completed with a flag indicating whether the transition succeeded
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}