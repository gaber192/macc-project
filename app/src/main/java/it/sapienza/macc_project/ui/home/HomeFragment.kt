package it.sapienza.macc_project.ui.home

import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import it.sapienza.macc_project.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*



class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private lateinit var mMap: GoogleMap

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var me : LatLng
    lateinit var myMarker : Marker
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback : LocationCallback


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                me = LatLng(location!!.latitude, location!!.longitude)
                myMarker = mMap.addMarker(
                    MarkerOptions().position(me)
                        .title(me.latitude.toString() + " ; " + me.longitude.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me,14f))
                Log.d("POSITIONS ",me.latitude.toString()+" ; "+me.longitude.toString())
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d("POSITIONS ",location.latitude.toString()+" ; "+location.longitude.toString())
                    me = LatLng(location.latitude, location.longitude)
                    myMarker.position= me
                    myMarker.title=me.latitude.toString()+" ; "+me.longitude.toString()
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in rome and move the camera
        val colosseo = LatLng(41.890251, 12.492373)
        mMap.addMarker(MarkerOptions().position(colosseo).title("Colosseo"))
    }
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(LocationRequest().setInterval(3000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY), locationCallback, Looper.getMainLooper())
    }

}
