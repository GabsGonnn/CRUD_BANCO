package com.example.crud_banco.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.crud_banco.R


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnInsert = findViewById<Button>(R.id.btnInsert)

        var btnFetch = findViewById<Button>(R.id.btnFetch)

        btnInsert.setOnClickListener{
            val intent = Intent(this, InsertionActivity::class.java)
            startActivity(intent)
        }
        btnFetch.setOnClickListener{
            val intent = Intent(this, FetchingActivity::class.java)
            startActivity(intent)
        }
    }
}