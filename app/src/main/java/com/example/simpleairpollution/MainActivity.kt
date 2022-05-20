package com.example.simpleairpollution

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.simpleairpollution.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private var cancellationTokenSource : CancellationTokenSource? = null

    private lateinit var geocoder: Geocoder

    private var lon : String = ""
    private var lat : String = ""

    var co = "" // 일산화 탄소
    var no = "" // 일산화 질소
    var no2 = "" // 이산화 질
    var o3 = "" // 오존
    var so2 = "" // 이산화 황
    var pm2_5 = "" // 초미세먼지
    var pm10 = "" // 미세먼
    var nh3 = "" // 암모니아
    var aqi = "" // 전체 공기질
    var address = ""


    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()

        val url3 = "http://api.openweathermap.org/data/2.5/air_pollution?lat=37.4856933&lon=127.1191588&appid=3bbea22f826e4eef49dc445bd1114a75"


    }

    private fun getAirPollution() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        retrofitService.getAirPollution(lat, lon).enqueue(object : Callback<Pollution> {
            override fun onResponse(call: Call<Pollution>, response: Response<Pollution>) {
                if (response.isSuccessful) {
                    val pollutionList = response.body()
                    val mainList = pollutionList?.list

                    Log.d("testt","${response.message()}")

                    Log.d("testt pollution","${pollutionList?.coord}")
                    Log.d("testt co","${mainList?.get(0)?.components?.co}")
                    Log.d("testt nh3","${mainList?.get(0)?.components?.nh3}")
                    Log.d("testt no","${mainList?.get(0)?.components?.no}")
                    Log.d("testt no2","${mainList?.get(0)?.components?.no2}")
                    Log.d("testt o3","${mainList?.get(0)?.components?.o3}")
                    Log.d("testt pm10","${mainList?.get(0)?.components?.pm10}")
                    Log.d("testt pm2_5","${mainList?.get(0)?.components?.pm2_5}")
                    Log.d("testt so2","${mainList?.get(0)?.components?.so2}")

                    Log.d("testt aqi","${mainList?.get(0)?.main?.aqi}")


                }
            }

            override fun onFailure(call: Call<Pollution>, t: Throwable) {
                Log.d("testt","${t.message}")
            }

        })
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("testt", "승낙")

                cancellationTokenSource = CancellationTokenSource()

                fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource!!.token

                ).addOnSuccessListener { location ->
                    //binding.textView.text = "${location.latitude} / ${location.longitude}"
                    lat = location.latitude.toString()
                    lon = location.longitude.toString()
                    Log.d("testt", "$lat / $lon")

                    binding.progressBar.visibility = View.GONE
                    //binding.progressBar.visibility = View.VISIBLE

                    getAirPollution()

                    geocoder = Geocoder(this, Locale.getDefault())

                    val address = geocoder.getFromLocation(lat.toDouble(), lon.toDouble(), 1)

                    Log.d("testt getAddressLine","${address[0].getAddressLine(0)}")
                    Log.d("testt locality","${address[0].locality}")
                    Log.d("testt locale","${address[0].locale}")
                    Log.d("testt adminArea","${address[0].adminArea}") // 서울특별시
                    Log.d("testt subAdminArea","${address[0].subAdminArea}")
                    Log.d("testt featureName","${address[0].featureName}")
                    Log.d("testt subLocality","${address[0].subLocality}") // 송파구
                    Log.d("testt subThoroughfare","${address[0].subThoroughfare}") // 201
                    Log.d("testt thoroughfare","${address[0].thoroughfare}") // 문정동
                    Log.d("testt extras","${address[0].extras}")
                    Log.d("testt phone","${address[0].phone}")
                    Log.d("testt postalCode","${address[0].postalCode}")
                    Log.d("testt premises","${address[0].premises}")
                    Log.d("testt address","${address[0]}")


                }

            } else {
                Log.d("testt", "거부")
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 1000
    }
}







































