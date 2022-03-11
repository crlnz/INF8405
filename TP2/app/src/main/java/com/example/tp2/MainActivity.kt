package com.example.tp2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location


class MainActivity : AppCompatActivity(), PermissionsListener{
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var favoriteAdapter: DeviceAdapter
    private lateinit var discoveredDevices: ArrayList<BluetoothDevice>
    private lateinit var favoriteDevices: ArrayList<BluetoothDevice>
    private lateinit var btnSwapTheme: Button
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var deviceFragment: DeviceFragment
    private lateinit var favoriteFragment: FavoriteFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        mapView = findViewById<View>(R.id.mapView) as MapView
        btnSwapTheme = findViewById(R.id.btnSwapTheme)
        map = mapView.getMapboxMap()
        discoveredDevices = ArrayList()
        favoriteDevices = ArrayList()
        setBluetoothAdapter()
        setUpFragments()
        setUpTabs()
        handlePermissions()
    }

    private fun setUpTabs(){
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(deviceFragment, "Devices")
        adapter.addFragment(favoriteFragment, "Favorites")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setUpFragments(){
        deviceFragment = DeviceFragment()
        favoriteFragment = FavoriteFragment()
    }

    @SuppressLint("MissingPermission")
    private fun onRecyclerViewItemClick(position: Int){
        //Toast.makeText(this, deviceAdapter.devices[position].name, Toast.LENGTH_SHORT).show()
        val dialog = Dialog(this)
        dialog.show()
    }




    private fun handlePermissions(){
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            onMapReady()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }

        discoverDevices()
    }

    private fun setBluetoothAdapter(){
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    @SuppressLint("MissingPermission")
    private fun discoverDevices(){
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(receiver, filter)
        bluetoothAdapter.startDiscovery()

    }


    private val receiver = object : BroadcastReceiver(){
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent?) {
            val action : String? = intent?.action
            when(action){
                BluetoothDevice.ACTION_FOUND ->{
                    val device : BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    Log.d("device", "${device?.name} + ${device?.address}")
                    if (device != null) {
                        deviceFragment.addDevice(device)
                        //favoriteFragment.addDevice(device)
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Toast.makeText(context, "Starting device discovery", Toast.LENGTH_SHORT).show()
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->{
                    Toast.makeText(context, "Scanning Done", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun onMapReady() {
       map.setCamera(
            CameraOptions.Builder()
                .zoom(18.0)
                .build()
       )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        )
        initLocationComponent()
        val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
            // Jump to the current indicator position
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        }
        mapView.location
            .addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location

        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                topImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon
                ),
                bearingImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_bearing_icon
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_stroke_icon
                ),
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(p0: MutableList<String>?) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(p0: Boolean) {
       if(p0){
           onMapReady();
       }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }
}