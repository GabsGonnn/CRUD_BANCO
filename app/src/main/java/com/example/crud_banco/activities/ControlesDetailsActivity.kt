package com.example.crud_banco.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.crud_banco.R
import com.example.crud_banco.models.Controle
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import kotlin.properties.Delegates

class ControlesDetailsActivity : AppCompatActivity() {

    private lateinit var tvnome: TextView
    private lateinit var tvvalor: TextView
    private lateinit var tvacao: TextView
    private lateinit var btnDelete: Button
    private lateinit var btnUpdate: Button

    private var requestCode by Delegates.notNull<Int>()
    private lateinit var aux: String
    private lateinit var tipoDisp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controles_details)


        requestCode = intent.getIntExtra("requestCode", -1)
        aux = intent.getStringExtra("aux").toString()
        tipoDisp = intent.getStringExtra("tipoDisp").toString()
        val controleId = intent.getStringExtra("controleId").toString()

        initView()
        setValuesToViews()

        btnDelete.setOnClickListener {
            deleteRecord(controleId)
        }

    }

    private fun initView() {
        tvnome = findViewById(R.id.tvDispNameContr)
        tvvalor = findViewById(R.id.tvDispValorContr)
        tvacao = findViewById(R.id.tvDispAcaoContr)
        btnDelete = findViewById(R.id.btnDeleteContr)
    }

    private fun setValuesToViews() {
        tvnome.text = intent.getStringExtra("nomeDisp")
        tvvalor.text = intent.getStringExtra("valor")
        tvacao.text = intent.getStringExtra("acao")
    }



    private fun deleteRecord(id: String) {
        cancelAlarm(requestCode) // Cancela o alarme ao deletar o registro
        val dbRef = FirebaseDatabase.getInstance().getReference("Funci_Luz").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Dados do dispositivo deletados", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Erro ao deletar: ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun cancelAlarm(requestCode: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java) // Substitua pelo seu BroadcastReceiver
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancelando o alarme
        alarmManager.cancel(pendingIntent)
        Log.d("ControlesDetailsActivity", "Alarme cancelado com requestCode: $requestCode")
    }
}
