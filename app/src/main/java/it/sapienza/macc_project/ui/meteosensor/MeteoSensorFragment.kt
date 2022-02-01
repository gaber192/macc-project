package it.sapienza.macc_project.ui.meteosensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import it.sapienza.macc_project.R
import androidx.fragment.app.Fragment



class MeteoSensorFragment : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var pressure: Sensor? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_meteosensor)

        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        sensorManager = applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager

        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        val millibarsOfPressure = event.values[0]
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