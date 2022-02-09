package it.sapienza.macc_project.ui.utilities

import android.content.Context
import android.hardware.Sensor
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.sapienza.macc_project.databinding.FragmentUtilitiesBinding
import androidx.appcompat.app.AppCompatActivity

import android.util.Log
import androidx.core.content.ContextCompat
import it.sapienza.macc_project.R

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class UtilitiesFragment : Fragment()  {

    private lateinit var utilitiesViewModel: UtilitiesViewModel
    private var _binding: FragmentUtilitiesBinding? = null

    private lateinit var cityNameText: TextView
    private lateinit var tempText: TextView
    private lateinit var minmaxTempText: TextView
    var myloc : Location = Location("A")
    private lateinit var locationManager: LocationManager
    private val binding get() = _binding!!

    lateinit var proxy : Proxy
    var time = "0"
    var value = "0"
    val callback =  fun (time : String, value:String){
        this.time = time
        this.value = value
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        utilitiesViewModel =
                ViewModelProvider(this).get(UtilitiesViewModel::class.java)

        _binding = FragmentUtilitiesBinding.inflate(inflater, container, false)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        cityNameText = binding.textView2
        tempText = binding.textView
        minmaxTempText = binding.textView3
        myloc = locationManager.getLastKnownLocation(locationManager.getProviders(true).get(0))
        myloc.latitude
        myloc.longitude




        val rightNow = Calendar.getInstance()
        time = rightNow.get(Calendar.HOUR_OF_DAY).toString()+":"+rightNow.get(Calendar.MINUTE).toString()

        val Hour: Int =rightNow.get(Calendar.HOUR_OF_DAY)+1 // return the hour in 24 hrs format (ranging from 0-23)

        if(6<Hour && Hour<=13) {
            view?.invalidate()
            view?.setBackgroundResource(R.drawable.sun)
        }else if (13<Hour && Hour<=20){
            view?.invalidate()
            view?.setBackgroundResource(R.drawable.giorno)
        }else if (20<Hour || Hour<=6){
            view?.invalidate()
            view?.setBackgroundResource(R.drawable.sun)
        }


        RunRequest()

        proxy = Proxy(1000,callback)


        val root: View = binding.root


        return root
    }
    private fun RunRequest()
    {

        val codice = resources.getString(R.string.owm_id)
        var cityName:String

        val url: URL? = try {
            URL( "https://api.openweathermap.org/data/2.5/weather?lat="+myloc.latitude+"&lon="+myloc.longitude+"&in&units=metric&appid="+codice)
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

        //Add your preferred location and API key obtained from OpenWeatherMap.org


        Log.d("Weather Report",data)
        val jsonObj = JSONObject(data)
        val main = jsonObj.getJSONObject("main")
        val temp = main.getString("temp")+"°C"
        cityName = jsonObj.getString("name")
        val minmaxTemp = main.getString("temp_min")+"°C/"+main.getString("temp_max")+"°C"

        cityNameText.text = cityName
        tempText.text = temp
        minmaxTempText.text = "min "+minmaxTemp+" max"

        value=temp


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        proxy.start()
    }

    override fun onPause() {
        super.onPause()
        proxy.pause()
    }
}