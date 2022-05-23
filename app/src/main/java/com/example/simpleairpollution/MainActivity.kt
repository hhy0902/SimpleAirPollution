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
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private var cancellationTokenSource : CancellationTokenSource? = null

    private lateinit var geocoder: Geocoder

    private var lon : String = ""
    private var lat : String = ""

    var co = 0.0 // 일산화 탄소
    var no = 0.0 // 일산화 질소
    var no2 = 0.0 // 이산화 질
    var o3 = 0.0 // 오존
    var so2 = 0.0 // 이산화 황 // 아황산가
    var pm2_5 = 0.0 // 초미세먼지
    var pm10 = 0.0 // 미세먼
    var nh3 = 0.0 // 암모니아
    var aqi = 0 // 전체 공기질
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
        val url4 = "api.openweathermap.org/data/2.5/forecast?lat=37.4856933&lon=127.1191588&appid=3bbea22f826e4eef49dc445bd1114a75"


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

                    co = mainList?.get(0)?.components?.co!!.toDouble()
                    nh3 = mainList?.get(0)?.components?.nh3.toDouble()
                    no = mainList?.get(0)?.components?.no.toDouble()
                    no2 = mainList?.get(0)?.components?.no2.toDouble()
                    o3 = mainList?.get(0)?.components?.o3.toDouble()
                    pm10 = mainList?.get(0)?.components?.pm10.toDouble()
                    pm2_5 = mainList?.get(0)?.components?.pm2_5.toDouble()
                    so2 = mainList?.get(0)?.components?.so2.toDouble()
                    aqi = mainList?.get(0)?.main?.aqi!!.toInt()

                    binding.pm10.text = "미세먼지 : ${pm10} μg/m3"
                    binding.pm25.text = "초미세먼지 : ${pm2_5} μg/m3"
                    binding.no.text = "일산화질소 : $no μg/m3"
                    binding.co.text = "일산화탄소 : $co μg/m3"
                    binding.o3.text = "오존 : $o3 μg/m3"
                    binding.so2.text = "아황산가스 : $so2 μg/m3"

                    when(aqi) {
                        1 -> {
                            binding.airQuality.text = "좋음"
                            binding.mainImage.text = "😆"
                            binding.constraintlayout.setBackgroundResource(R.color.blue)
                        }
                        2 -> {
                            binding.airQuality.text = "보통"
                            binding.mainImage.text = "🙂"
                            binding.constraintlayout.setBackgroundResource(R.color.green)
                        }
                        3 -> {
                            binding.airQuality.text = "나쁨"
                            binding.mainImage.text = "🙁"
                            binding.constraintlayout.setBackgroundResource(R.color.yellow)
                        }
                        4 -> {
                            binding.airQuality.text = "매우나쁨"
                            binding.mainImage.text = "😫"
                            binding.constraintlayout.setBackgroundResource(R.color.red)
                        }
                        5 -> {
                            binding.airQuality.text = "매우나쁨"
                            binding.mainImage.text = "😫"
                            binding.constraintlayout.setBackgroundResource(R.color.red)
                        }
                        else -> {
                            binding.airQuality.text = "데이터 없음"
                            binding.mainImage.text = "🧐"
                            binding.constraintlayout.setBackgroundResource(R.color.gray)
                        }

                    }

                    if(pm10 > 0 && pm10 <= 25) {
                        binding.pm10Image.text = "\uD83D\uDE06"
                    } else if(pm10 > 25 && pm10 <= 50) {
                        binding.pm10Image.text = "\uD83D\uDE42"
                    } else if(pm10 > 50 && pm10 <= 90) {
                        binding.pm10Image.text = "\uD83D\uDE41"
                    } else if(pm10 > 90 && pm10 <= 180) {
                        binding.pm10Image.text = "\uD83D\uDE2B"
                    } else if(pm10 > 181){
                        binding.pm10Image.text = "😱"
                    } else {
                        binding.pm10Image.text = "\uD83E\uDDD0"
                    }

                    if(pm2_5 > 0 && pm2_5 <= 15) {
                        binding.pm25Image.text = "\uD83D\uDE06"
                    } else if(pm2_5 > 15 && pm2_5 <= 30) {
                        binding.pm25Image.text = "\uD83D\uDE42"
                    } else if(pm2_5 > 30 && pm2_5 <= 55) {
                        binding.pm25Image.text = "\uD83D\uDE41"
                    } else if(pm2_5 > 55 && pm2_5 <= 110) {
                        binding.pm25Image.text = "\uD83D\uDE2B"
                    } else if(pm2_5 > 110){
                        binding.pm25Image.text = "😱"
                    } else {
                        binding.pm25Image.text = "\uD83E\uDDD0"
                    }




                    binding.progressBar.visibility = View.GONE

                    binding.constraintlayout2.visibility = View.VISIBLE

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

                    geocoder = Geocoder(this, Locale.getDefault())

                    val address = geocoder.getFromLocation(lat.toDouble(), lon.toDouble(), 1)

                    Log.d("testt getAddressLine","${address[0].getAddressLine(0)}")
                    Log.d("testt locality","${address[0].locality}")
                    Log.d("testt adminArea","${address[0].adminArea}") // 서울특별시
                    Log.d("testt subLocality","${address[0].subLocality}") // 송파구
                    Log.d("testt subThoroughfare","${address[0].subThoroughfare}") // 201
                    Log.d("testt thoroughfare","${address[0].thoroughfare}") // 문정동

                    binding.addressDong.text = address[0].thoroughfare
                    binding.address.text = address[0].getAddressLine(0)

                    getAirPollution()
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







































