package trashTalk.apps.trashTalk.modules.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import trashTalk.apps.trashTalk.R
import trashTalk.apps.trashTalk.databinding.FragmentMapBinding
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.modules.TrashViewModel
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var infoCard: View? = null
    private var infoText: TextView? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    @SuppressLint("ServiceCast")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        binding.plusButton.setOnClickListener {
//            mMap.animateCamera(CameraUpdateFactory.zoomIn())
//        }
//
//        binding.minusButton.setOnClickListener {
//            mMap.animateCamera(CameraUpdateFactory.zoomOut())
//        }

        return binding.root

// --- working code
//        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Find info card views
        infoCard = view.findViewById(R.id.infoCard)
        infoText = view.findViewById(R.id.infoText)

        // Hide card initially
        infoCard?.visibility = View.GONE
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationProvider = LocationManager.GPS_PROVIDER

            val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider)
            lastKnownLocation?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    fun getLatLngFromAddress(address: String, callback: (LatLng?) -> Unit) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocationName(address, 1)
        Log.i("s", "geocoder Trash: ${geocoder}")
        Log.i("s", "addresses Trash: ${addresses}")

        if (addresses != null && addresses.isNotEmpty()) {
            val location = addresses[0]
            val latLng = LatLng(location.latitude, location.longitude)
            Log.i("s", "lating Trash: ${latLng}")

            callback(latLng)
        } else {
            callback(null)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true

        checkLocationPermission()
        moveToCurrentLocation()

        val viewModel: TrashViewModel by lazy {
            ViewModelProvider(this)[TrashViewModel::class.java]
        }

        viewModel.trashes?.observe(viewLifecycleOwner) { trashesList ->
            Log.i("s", "Trashes list size: ${trashesList.size}")
            for (trash in trashesList) {
            Log.i("s", "Trash address: ${trash.address}")
                getLatLngFromAddress(trash.address) { latLng ->
                    if (latLng != null) {
                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(trash.name)
                                .snippet(trash.address)
                        )
                        marker?.tag = trash
                    }
                }
            }
        }


//        val israelPoints = listOf(
//            LatLng(31.7683, 35.2137),  // Jerusalem
//            LatLng(32.0853, 34.7818),  // Tel Aviv
//            LatLng(29.5577, 34.9519),  // Eilat
//            LatLng(32.7940, 34.9896),  // Haifa
//            LatLng(31.2529, 34.7915)   // Be'er Sheva
//        )
//
//        for (point in israelPoints) {
//            mMap?.addMarker(
//                MarkerOptions()
//                    .position(point)
//                    .title("Custom Marker")
//                    .icon(getBitmapDescriptor(R.drawable.ic_map))
//            )
//        }

        // Handle marker clicks
        mMap?.setOnMarkerClickListener { marker ->
            showInfoCard(marker)
            true
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showInfoCard(marker: Marker) {
        infoCard?.visibility = View.VISIBLE
        infoText?.text = "Coordinates: ${marker.position.latitude}, ${marker.position.longitude}"
    }

    private fun getBitmapDescriptor(resourceId: Int): BitmapDescriptor {
        val drawable = resources.getDrawable(resourceId, null)
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }
}