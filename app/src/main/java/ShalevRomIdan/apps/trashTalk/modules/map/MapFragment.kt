package trashTalk.apps.trashTalk.modules.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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

class MapFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var infoCard: View? = null
    private var infoText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // List of points in Israel
        val israelPoints = listOf(
            LatLng(31.7683, 35.2137),  // Jerusalem
            LatLng(32.0853, 34.7818),  // Tel Aviv
            LatLng(29.5577, 34.9519),  // Eilat
            LatLng(32.7940, 34.9896),  // Haifa
            LatLng(31.2529, 34.7915)   // Be'er Sheva
        )

        // Add markers with custom icons
        for (point in israelPoints) {
            mMap?.addMarker(
                MarkerOptions()
                    .position(point)
                    .title("Custom Marker")
                    .icon(getBitmapDescriptor(R.drawable.ic_map))
            )
        }

        // Move the camera to the first point
        if (israelPoints.isNotEmpty()) {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(israelPoints[0], 7f))
        }

        // Handle marker clicks
        mMap?.setOnMarkerClickListener { marker ->
            showInfoCard(marker)
            true
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
}


//check
//this is original
//package trashTalk.apps.trashTalk.modules.map
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import trashTalk.apps.trashTalk.R
//
//class MapFragment : Fragment(), OnMapReadyCallback {
//
//    private var mMap: GoogleMap? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.fragment_map, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
//        mapFragment?.getMapAsync(this)
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // List of points in Israel
//        val israelPoints = listOf(
//            LatLng(31.7683, 35.2137),  // Jerusalem
//            LatLng(32.0853, 34.7818),  // Tel Aviv
//            LatLng(29.5577, 34.9519),  // Eilat
//            LatLng(32.7940, 34.9896),  // Haifa
//            LatLng(31.2529, 34.7915)   // Be'er Sheva
//        )
//
//        // Add markers for each point
//        for (point in israelPoints) {
//            mMap?.addMarker(MarkerOptions().position(point).title("Marker in Israel"))
//        }
//
//        // Move the camera to the first point in the list
//        if (israelPoints.isNotEmpty()) {
//            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(israelPoints[0], 7f))
//        }
//    }
//}