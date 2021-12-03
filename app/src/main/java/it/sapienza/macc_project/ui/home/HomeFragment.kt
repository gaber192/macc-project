package it.sapienza.macc_project.ui.home

import android.app.AlertDialog
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
import it.sapienza.macc_project.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import android.os.StrictMode
import android.text.method.ScrollingMovementMethod
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    lateinit var mMap: GoogleMap
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var database: DatabaseReference = Firebase.database.reference.child("Myapp").child("user").child(firebaseAuth.currentUser?.uid.toString())
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var me : LatLng
    lateinit var myMarker : Marker
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback : LocationCallback
    lateinit var poiclicklistener : GoogleMap.OnPoiClickListener
    var monument_name : String = " "
    var monument_position : String = " "
    var Map : HashMap<String, String> = hashMapOf()
    lateinit var tv : TextView
    lateinit var iv : ImageView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        Map = hashMapOf()
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
        iv = binding.favourite
        iv.visibility= View.INVISIBLE
        tv.movementMethod = ScrollingMovementMethod()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Preferred")
        builder.setMessage("You want to add the monument to preferred?")
        builder.setPositiveButton("Yes") { dialog, which ->
            Toast.makeText(requireContext(),"Yes", Toast.LENGTH_SHORT).show()
            database.child("monuments_preferred").child(monument_name).setValue(monument_position)
            val latlong = monument_position.split(",").toTypedArray()
            val latitude = latlong[0].toDouble()
            val longitude = latlong[1].toDouble()
            mMap.addMarker(MarkerOptions().position(LatLng(latitude,longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
        }
        builder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(requireContext(),"No", Toast.LENGTH_SHORT).show()
            iv.visibility=View.INVISIBLE
            tv.text=""
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    me = LatLng(location.latitude, location.longitude)
                    myMarker.position= me
                    myMarker.title="Me"
                    database.child("last_position").setValue(location.latitude.toString()+" "+location.longitude.toString())
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
                    if (!obj.has("-1")) {
                        monument_name = element.getString("title")
                        monument_position = p0!!.latLng.latitude.toString()+","+p0!!.latLng.longitude.toString()
                        // fetch JSONObject data
                        tv.setText(monument_name + "\n"+ element.getString("extract"))
                        iv.visibility = View.VISIBLE
                        iv.setOnClickListener { v: View ->
                            builder.show()

                        }
                    }else {
                            tv.text=""
                            iv.visibility = View.INVISIBLE
                    }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
            }
        }
        database.child("monuments_preferred").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                Map = hashMapOf()
                if (p0!!.exists()) {
                    for (h in p0.children) {
                        val element = h.getValue(String::class.java)!!
                        Map[h.key!!] = element
                    }
                }
            }
        })
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
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnPoiClickListener(poiclicklistener)
        addMarker()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(LocationRequest().setInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY), locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun addMarker(){
        mMap.clear()
        Map.forEach { (key, value) ->
            val latlong = value.split(",").toTypedArray()
            val latitude = latlong[0].toDouble()
            val longitude = latlong[1].toDouble()
            mMap.addMarker(MarkerOptions().position(LatLng(latitude,longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
        }
    }
}
