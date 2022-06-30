package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager

import android.location.Address
import android.location.Geocoder

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import org.json.JSONObject
import java.io.IOException

import java.util.*


class MainActivity : AppCompatActivity() {
    //Инициализация переменных под объекты активити
    private lateinit var swiper: SwipeRefreshLayout
    private lateinit var cityText: TextView
    private lateinit var backImage: ImageView
    private lateinit var weatherView: RecyclerView


    private lateinit var update: TextView
    private lateinit var tempText: TextView

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient //переменная для работы с gps модулем
    private val REQUEST_CODE = 100
    private lateinit var weatherData: WeatherData //Объект дата-класса

    //Переменные для работы с api
    private lateinit var CITY: String
    val API: String = "ffbdc880e0eb47a896d142413223105" // Use API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setBackGround(1)
        getLastLocation()
        refreshApp()
    }

    private fun setAdapter(){  //Метод инициализации ресайклера
        val recyclerView: RecyclerView = findViewById(R.id.weatherView) //инициализируем ресайклер на активити
        val linearLayoutManager = LinearLayoutManager(applicationContext) //Инициализация менеджера ресайклера
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL //задание вертикальной ориентации ресайклера
        recyclerView.layoutManager = linearLayoutManager //Передача менеджера во внутренний менеджер
        recyclerView.adapter = CustomRecyclerAdapter(weatherData) //Инициализация кастомного адаптера с передачей data-класса
    }

    private fun setBackGround(res: Int){ //метод обновления заднего фона
        var rnd: Int
        if(res == 1){
            rnd = (1..2).random()
        }
        else{
            rnd = (3..4).random()
        }
        println("image number ${rnd}")
        when(rnd){
            1 -> backImage.setImageResource(R.drawable.bg_one)
            2 -> backImage.setImageResource(R.drawable.bg_two)
            3 -> backImage.setImageResource(R.drawable.bg_three)
            4 -> backImage.setImageResource(R.drawable.bg_four)
        }
    }

    private fun refreshApp(){
        swiper.setOnRefreshListener {
            getLastLocation()
            setBackGround(1)
            swiper.isRefreshing = false
        }
    }

    private fun getLastLocation() { //метод считывания gps и показ погоды
        if (ContextCompat.checkSelfPermission( //Проверка разрешения на работу с геолокацией
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cityText.text = getString(R.string.check) // Вывод информации о начале работы программы
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location -> //Использование слушателя для получения координат
                    if (location != null) { //Если координаты получены
                        try {
                            val geocoder = Geocoder(this@MainActivity, Locale.ENGLISH) //Инициализация переменной для декодирования координат
                            val addresses: List<Address> =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1) //Декодируем координаты для получения названия города
                            cityText.text = getString(R.string.city_text) + addresses[0].locality //Выводим название города в соответствующее поле
                            CITY = addresses[0].locality
                            getResult() //Метод работы api

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    else{
                        cityText.text = getString(R.string.error_message)

                    }
                }

        } else { //Случай отсутствия прав использования gps
            askPermission() //Метод получения прав пользования gps
        }
    }

    private fun getResult(){
        val URL = "http://api.weatherapi.com/v1/current.json?key=$API&q=$CITY&aqi=no" //Формируем url с запросом для api
        val queue = Volley.newRequestQueue(this) //Инициализация переменной для передачи запроса
        val stringRequest = StringRequest(com.android.volley.Request.Method.GET, URL, { //Передача запроса и получение ответа
                response -> //Случай удачного результата отклика api
            val obj = JSONObject(response) //Получение json файла
            val res = obj.getJSONObject("current") //Работа с заголовком current json
            //Разбор файл по заголовкам и запись в поля
            tempText.text = res.getString("temp_c" ) + getText(R.string.grad)
            update.text = res.getString("last_updated")

            weatherData = WeatherData() //Иницализация объекта data-класса
            weatherData.pressureText = res.getString("pressure_mb")
            weatherData.humidityText = res.getString("humidity")
            weatherData.windText = res.getString("wind_kph")
            weatherData.eventText = obj.getJSONObject("current").getJSONObject("condition").getString("text")
            weatherData.feelsLikeText = res.getString("feelslike_c" ) + getText(R.string.grad)
            weatherData.windDirText = res.getString("wind_dir")
            setAdapter() //Инициализация ресайклервью

            var dayNight = res.getString("is_day").toInt() //Получение информации о времени суток
            setBackGround(dayNight) //Обновляем задний фон
        }, {
                error -> //Случай неудачного результата отклика api
            cityText.text = "Error"
            println(error.toString())
        })
        queue.add(stringRequest) //Добавление запроса в очередь
    }



    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        ) //Переопределения метода для получения прав доступа к gps для акивити
    }

    override fun onRequestPermissionsResult( //Метод получения прав
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Please provide the required permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun init(){ //метод инициализации переменных
        cityText = findViewById(R.id.cityText)
        swiper = findViewById(R.id.swiper)
        backImage = findViewById(R.id.backImage)
        weatherView = findViewById(R.id.weatherView)

        update = findViewById(R.id.updateText)

        tempText = findViewById(R.id.tempText)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) //инициализируем переменную для работы с gps
    }


}