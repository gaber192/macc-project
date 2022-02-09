package it.sapienza.macc_project.ui.meteosensor

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import it.sapienza.macc_project.R
import androidx.fragment.app.Fragment
import it.sapienza.macc_project.databinding.FragmentLightsensorBinding
import it.sapienza.macc_project.databinding.FragmentMeteosensorBinding


class MeteoSensorFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var pressure: Sensor? = null
    private var _binding: FragmentMeteosensorBinding? = null
    private val binding get() = _binding!!

    private var secondo: Sensor? = null

    private lateinit var text: TextView

    //private lateinit var text2: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeteosensorBinding.inflate(inflater, container, false)
        val root: View = binding.root
        text = binding.BarValue

        sensorManager = requireContext().getSystemService(SENSOR_SERVICE) as SensorManager
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)


        return root
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        val millibarsOfPressure = event.values[0]
        text.text = event.values[0].toString()+" millibar"


        // Do something with this sensor data.
    }

    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}