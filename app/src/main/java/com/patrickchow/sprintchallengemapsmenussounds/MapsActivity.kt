package com.patrickchow.sprintchallengemapsmenussounds

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var player: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        player = MediaPlayer.create(this, R.raw.zapsplat_horror_accent_metal_scrape_dark_processed_scary_001_39922)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    // Inflates the menu_sign and place it to the top right
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_icons, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Gives the item selected functionality
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.marker -> {
                Toast.makeText(this, "Marker Placed", Toast.LENGTH_SHORT).show()

                val latLng = mMap.cameraPosition.target
                mMap.addMarker(MarkerOptions().position(latLng).title("Pin"))

                player.start()
            }
            R.id.current_location -> {
                Toast.makeText(this, "Current Location", Toast.LENGTH_SHORT).show()
                requestPermission()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Request for permission for location
    fun requestPermission(){
        ActivityCompat
            .requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                111)
    }

    // If permission is okay, move to the current location of device
    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            val locationProvider = LocationServices.getFusedLocationProviderClient(this)

            // Listener here because getting location takes time
            locationProvider.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val latLng = LatLng(it.latitude, it.longitude)

                    //Moves the camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

                    //Places a mark on the location
                    mMap.addMarker(MarkerOptions().position(latLng).title("Location"))
                }
            }

        }
    }

    // If request is okay, move camera to getCurrentLocation()
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111) {
            getCurrentLocation()
        }
    }
}
