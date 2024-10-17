package com.example.crud_banco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_banco.R
import com.example.crud_banco.models.LogModelo

class LogAdapter(private val dispList: ArrayList<LogModelo>) :
    RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.disp_list_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { // Corrigido para ViewHolder
        val currentDisp = dispList[position]
        val logname = currentDisp.lognome
        val loghoras = currentDisp.loghora

        val bum: String = loghoras.toString()
        val buma: String = logname.toString()

        val text: String = (buma + " - " + bum)
        holder.tvDispName.text = text
    }

    override fun getItemCount(): Int {
        return dispList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val tvDispName: TextView = itemView.findViewById(R.id.tvDispName)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}