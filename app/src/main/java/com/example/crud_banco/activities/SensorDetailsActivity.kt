package com.example.crud_banco.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.crud_banco.R

class SensorDetailsActivity : AppCompatActivity(){
    private lateinit var tvnome: TextView
    private lateinit var tvacao: TextView
    private lateinit var tvdata: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_details)
        initView()
        setValuesToViews()
    }

    private fun initView() {
        tvnome = findViewById(R.id.tvlogId)
        tvacao = findViewById(R.id.tvacao)
        tvdata = findViewById(R.id.tvData)
    }

    private fun setValuesToViews() {
        tvnome.text = intent.getStringExtra("nome")
        tvacao.text = intent.getStringExtra("valor")
        tvdata.text = intent.getStringExtra("unidade")
    }
}