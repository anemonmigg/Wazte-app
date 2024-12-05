package com.mobdeve.s20.gonzales.miguel.wazte

import android.content.ContentValues.TAG
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

        // Access device location
        val locationService : LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider: DeviceLocationProvider? = null

        val request = LocationProviderRequest.Builder()
            .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
            .displacement(0F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build();

        val result = locationService.getDeviceLocationProvider(request)
        if (result.isValue) {
            locationProvider = result.value!!
        } else {
            // Log.error("Failed to get device location provider")
        }


        val locationObserver = object: LocationObserver {
            override fun onLocationUpdateReceived(locations: MutableList<Location>) {
                val loc = locations.first()
                val long = loc.longitude
                val lat = loc.latitude
                Log.e(TAG, "Location update received: " + loc)
                Log.e(TAG, "LAT: " + loc.latitude)
                Log.e(TAG, "LONG: " + loc.longitude)

                // Check if username is not null
                if (username != null) {
                    // Access Firestore
                    val db = Firebase.firestore

                    // Create an array with the new location data
                    val userLocationArray = arrayListOf(lat, long)

                    // Update Firestore document for the user with the username as the document ID
                    db.collection("users")
                        .document(username)  // Use the username as the document ID
                        .update("lastPosition", userLocationArray)  // Store the array in "lastPosition" field
                        .addOnSuccessListener {
                            Log.d(TAG, "Location data successfully updated in Firestore!")
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Error updating location: ", exception)
                        }
                } else {
                    Log.e(TAG, "Username is null. Unable to update location.")
                }
            }
        }

        if (locationProvider != null) {
            locationProvider.addLocationObserver(locationObserver)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}