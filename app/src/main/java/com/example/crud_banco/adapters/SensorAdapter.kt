package com.example.crud_banco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_banco.R
import com.example.crud_banco.models.Sensor

class SensorAdapter(private val sensoresList: List<Sensor>) :
    RecyclerView.Adapter<SensorAdapter.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_sensores_list_item, parent, false)
        return MyViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentSensor = sensoresList[position]
        holder.tvSensorTittle.text = currentSensor.nome
        holder.ivSensorImg.setImageResource(currentSensor.imagem ?: R.drawable.temp_icon) // Use um recurso padrão se a imagem for nula

        holder.cardView.setOnClickListener {
            mListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return sensoresList.size
    }

    class MyViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvSensorTittle: TextView = itemView.findViewById(R.id.tvSensorTittle)
        val ivSensorImg: ImageView = itemView.findViewById(R.id.ivSensorImg)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}