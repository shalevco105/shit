package trashTalk.apps.trashTalk.modules.map

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import trashTalk.apps.trashTalk.R


class MapFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var cardView: CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        cardView = view.findViewById(R.id.cardView) // Initialize CardView
        cardView.visibility = View.GONE // Initially hide the card
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val israelPoints = listOf(
            LatLng(31.7683, 35.2137),  // Jerusalem
            LatLng(32.0853, 34.7818),  // Tel Aviv
            LatLng(29.5577, 34.9519),  // Eilat
            LatLng(32.7940, 34.9896),  // Haifa
            LatLng(31.2529, 34.7915)   // Be'er Sheva
        )

        for (point in israelPoints) {
            // Load custom image using Picasso (or your preferred image loading library)
            Picasso.get()
                .load(R.drawable.ic_launcher_foreground) // Replace with your image resource
                .resize(100,100) // Optional: resize image
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                        val markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
                        val marker = mMap?.addMarker(
                            MarkerOptions().position(point).icon(markerIcon)
                        )
                        marker?.tag = point // Store LatLng as marker tag

                        // Set click listener for each marker
                        mMap?.setOnMarkerClickListener { clickedMarker ->
                            val clickedPoint = clickedMarker.tag as LatLng
                            showCard(clickedPoint)
                            true // Consume the click
                        }

                    }

                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
                        // Handle image loading failure (e.g., use default marker)
                        val marker = mMap?.addMarker(MarkerOptions().position(point))
                        marker?.tag = point
                        mMap?.setOnMarkerClickListener { clickedMarker ->
                            val clickedPoint = clickedMarker.tag as LatLng
                            showCard(clickedPoint)
                            true // Consume the click
                        }
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        // Optional: show a placeholder while loading
                    }
                })
        }



        if (israelPoints.isNotEmpty()) {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(israelPoints[0], 7f))
        }
    }

    private fun showCard(coordinates: LatLng) {
        cardView.visibility = View.VISIBLE
        // Set the text in the card view with the coordinates
        // Example:
         val coordinatesTextView = cardView.findViewById<TextView>(R.id.coordinatesTextView)
         coordinatesTextView.text = "Latitude: ${coordinates.latitude}, Longitude: ${coordinates.longitude}"

        // You can customize the card's content as per your requirements
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