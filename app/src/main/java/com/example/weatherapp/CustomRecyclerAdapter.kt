package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerAdapter(private val weather: WeatherData): RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){ //Инициализация поля на item ресайклера
        val sunriseText: TextView = itemView.findViewById(R.id.sunriseText)
        val sunsetText: TextView = itemView.findViewById(R.id.sunsetText)
        val windText: TextView = itemView.findViewById(R.id.windText)
        val pressureText: TextView = itemView.findViewById(R.id.pressureText)
        val humidityText: TextView = itemView.findViewById(R.id.humidityText)
        val eventText: TextView = itemView.findViewById(R.id.eventText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { //Связываем ресайклервью и ресайклер айтем
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false) //Расширяем контейнер с ресайклером подоплительный элементом
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) { //Метод заполнения полей информацией из активити
        holder.sunriseText.text = weather.feelsLikeText
        holder.sunsetText.text = weather.windDirText
        holder.windText.text = weather.windText
        holder.pressureText.text = weather.pressureText
        holder.humidityText.text = weather.humidityText
        holder.eventText.text = weather.eventText
    }

    override fun getItemCount(): Int { //Количество необходимых айтемов для ресайклера
        return 1
    }
}