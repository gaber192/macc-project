package it.sapienza.macc_project.ui.home

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.JsonObject
import it.sapienza.macc_project.BuildConfig.MAPS_API_KEY
import it.sapienza.macc_project.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import org.xml.sax.Parser
import java.net.HttpURLConnection

import java.net.MalformedURLException
import java.net.URL
import android.os.StrictMode
import android.text.method.ScrollingMovementMethod


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
    lateinit var placesClient : PlacesClient

    lateinit var poiclicklistener : GoogleMap.OnPoiClickListener
    lateinit var tv : TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync(this)
        Places.initialize(requireContext(), MAPS_API_KEY)

        // Create a new PlacesClient instance
        placesClient = Places.createClient(requireContext())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                me = LatLng(location!!.latitude, location!!.longitude)
                myMarker = mMap.addMarker(
                    MarkerOptions().position(me)
                        .title("Me")
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

        tv = binding.description
        tv.movementMethod = ScrollingMovementMethod()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d("POSITIONS ",location.latitude.toString()+" ; "+location.longitude.toString())
                    me = LatLng(location.latitude, location.longitude)
                    myMarker.position= me
                    myMarker.title="Me"
                }
            }
        }

        poiclicklistener = object : GoogleMap.OnPoiClickListener {
            override fun onPoiClick(p0: PointOfInterest?) {

                val name = p0!!.name.replace(" ","_")
                val url: URL? = try {
                    URL("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles="+name)

                }catch (e: MalformedURLException){
                    Log.d("Exception", e.toString())
                    null
                }
                if (Build.VERSION.SDK_INT > 9) {
                    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)
                }
                val connection = url!!.openConnection() as HttpURLConnection
                val data = connection.inputStream.bufferedReader().readText()

                try {
                    // get JSONObject from JSON file
                    val obj = JSONObject(data).getJSONObject("query").getJSONObject("pages")
                    val element = obj.getJSONObject(obj.names().get(0).toString())
                    // fetch JSONObject data
                    tv.setText(element.getString("title")+"\n"+element.getString("extract"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                //apiResponse.toString())
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

        mMap.setOnPoiClickListener(poiclicklistener)

    }
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(LocationRequest().setInterval(3000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY), locationCallback, Looper.getMainLooper())
    }

}
