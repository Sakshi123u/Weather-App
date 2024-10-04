package com.example.myapplication1


import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication1.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
//2ffcfc20f0037ca0c0d30539fe4326da
class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Pune")
        searchcity()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    private fun searchcity() {
        val searchView = binding.SearchView as SearchView // Cast to SearchView if necessary
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
        private fun fetchWeatherData(cityname:String){
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build().create(apiinterFace::class.java)
            val response = retrofit.getWeatherData(cityname,appid="2ffcfc20f0037ca0c0d30539fe4326da",units="metric")
            response.enqueue(object : Callback<weatherApp>{
                override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                    val responseBody = response.body()
                    if(response.isSuccessful && responseBody !=null){
                        val temperature = responseBody.main.temp.toString()
                        val humidity = responseBody.main.humidity
                        val windspeed = responseBody.wind.speed
                        val sunRise = responseBody.sys.sunrise.toLong()
                        val sunset=responseBody.sys.sunset.toLong()
                        val sealevel=responseBody.main.pressure
                        val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                        val maxtemp = responseBody.main.temp_max
                        val mintemp = responseBody.main.temp_min
                        binding.temperature.text="$temperature\u00B0C "
                        binding.weather.text=condition
                        binding.maxTemp.text="Max Temp: $maxtemp°C"
                        binding.minTemp.text="Min Temp: $mintemp°C"
                        binding.humidity.text="$humidity %"
                        binding.windspeed.text="$windspeed m/s"
                        binding.sunrise.text="${time(sunRise)}"
                        binding.sunset.text="${time(sunset)}"
                        binding.sea.text="$sealevel hpa"
                        binding.condition.text=condition
                        binding.day.text=dayname(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityname"
                        //Log.d("TAG", "onResponse:$temperature ")
                        changeImageAccordingToWeatherCondition(condition)
                    }
                }

                override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }

    private fun changeImageAccordingToWeatherCondition(conditions:String) {
        when (conditions){
           "Clear Sky","Sunny","Clear"->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp:Long): String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayname(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}