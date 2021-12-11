package it.sapienza.macc_project.ui.buddies


import android.content.Context.LOCATION_SERVICE
import android.content.Context.SENSOR_SERVICE
import android.hardware.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.sapienza.macc_project.databinding.FragmentCompassbuddyBinding
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.ImageView
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Math.toDegrees


class CompassBuddyFragment : Fragment(),SensorEventListener {

    private var _binding: FragmentCompassbuddyBinding? = null
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var database: DatabaseReference = Firebase.database.reference.child("Myapp").child("user").child(firebaseAuth.currentUser?.uid.toString())
    private lateinit var mSensorManager: SensorManager
    var myloc : Location = Location("A")
    var target: Location = Location("B")

    private lateinit var locationManager: LocationManager
    private lateinit var iv: ImageView
    private lateinit var buddyname: TextView
    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var tv3: TextView
    private lateinit var geoField: GeomagneticField
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCompassbuddyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        iv= binding.mainImageHands
        buddyname = binding.buddyName
        tv1= binding.azimuth
        tv2= binding.bearing
        tv3= binding.direction
        myloc = locationManager.getLastKnownLocation(locationManager.getProviders(true).get(0))
        val name: String = arguments?.getString("name").toString()
        buddyname.text=name
        database.child("buddies").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                for (h in snapshot.children){
                    if(name.equals(h.key)){
                        val temp : String = h.child("last_position").value.toString()
                        val latlong = temp.split(",").toTypedArray()
                        target.latitude = latlong[0].toDouble()
                        target.longitude = latlong[1].toDouble()
                    }
                }
            } else {
                Log.d("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }}

        mSensorManager = requireContext().getSystemService(SENSOR_SERVICE)as SensorManager

        return root
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)

        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL)

        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("TARGET",target.toString())
        var azimuth = toDegrees(event!!.values[0].toDouble()).toFloat()
        azimuth= (azimuth +360)%360

        geoField =  GeomagneticField(myloc.latitude.toFloat(),
                                     myloc.longitude.toFloat(),
                                     myloc.altitude.toFloat(),
                                     System.currentTimeMillis())

        azimuth-= geoField.declination
        var bearTo = myloc.bearingTo(target)

        if(bearTo<0){
            bearTo += 360
        }
        var direction = bearTo - azimuth

        tv1.text="Azimuth: "+azimuth.toString()+" °"
        tv2.text="Bearing: "+bearTo.toString()+" °"
        tv3.text="Direction: "+direction.toString()+" °"

        iv.animate().rotation(direction).setDuration(3000).setInterpolator(LinearInterpolator()).start()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO("Not yet implemented")
    }





}