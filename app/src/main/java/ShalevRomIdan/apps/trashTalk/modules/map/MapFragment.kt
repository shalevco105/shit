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
import androidx.navigation.fragment.findNavController
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
import trashTalk.apps.trashTalk.models.Model
import trashTalk.apps.trashTalk.models.Trash
import trashTalk.apps.trashTalk.modules.TrashViewModel
import trashTalk.apps.trashTalk.modules.trashes.TrashesFragmentDirections
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

    private fun showTrashDialog(trash: Trash) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(trash.name)
            .setMessage("Address: ${trash.address}")
            .setPositiveButton("Go to") { dialog, which ->
                val action = MapFragmentDirections.actionMapFragmentToTrashDetailsFragment(
                    trash.name, trash.imageUrl, trash.author)
                findNavController().navigate(action)
            }
            .setNegativeButton("Close", null)
            .create()

        dialog.show()
    }

    fun getLatLngFromAddress(address: String, callback: (LatLng?) -> Unit) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocationName(address, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val location = addresses[0]
            val latLng = LatLng(location.latitude, location.longitude)
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

        viewModel.trashes = Model.instance.getAllTrashes()

        viewModel.trashes?.observe(viewLifecycleOwner) { trashesList ->
            for (trash in trashesList) {
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

        mMap.setOnMarkerClickListener { marker ->
            val trash = marker.tag as? Trash
            trash?.let {
                showTrashDialog(trash)
            }
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