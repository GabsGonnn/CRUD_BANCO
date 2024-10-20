package com.example.crud_banco.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.crud_banco.R
import com.example.crud_banco.models.MqttManager
import com.example.crud_banco.models.Sensor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SensorDetailsActivity : AppCompatActivity() {
    private lateinit var tvnome: TextView
    private lateinit var tvacao: TextView
    private lateinit var tvdata: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var mqttManager: MqttManager

    private lateinit var tipoSensor: String
    private lateinit var dispId: String
    private lateinit var aux: String
    private lateinit var timestamp: String
    private lateinit var topic: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_details)
        initView()
        setValuesToViews()

        val host = "5c4f71f03f934cd08ccae931b3d4a457.s1.eu.hivemq.cloud"
        val username = "admin"
        val password = "Admin1234!"

        mqttManager = MqttManager(host, username, password)

        tipoSensor = intent.getStringExtra("tipoSensor").toString()
        dispId = intent.getStringExtra("dispId").toString()
        aux = intent.getStringExtra("aux").toString()
        timestamp = intent.getStringExtra("timestamp").toString()

        mqttManager.connect(
            onConnected = {
                getTopicForSensor()
            },
            onError = { Log.d("SensorDetailsActivity", "Erro na conexão") }
        )

        btnUpdate.setOnClickListener {
            openUpdateDialog(dispId, intent.getStringExtra("dispNome").toString())
        }

        btnDelete.setOnClickListener {
            deleteRecord(dispId)
        }
    }

    private fun getTopicForSensor() {
        topic = if (tipoSensor == "termometro") {
            "casa/temperatura/$aux"
        } else {
            "casa/umidade/$aux"
        }

        setupMqttSubscription(topic)
    }

    private fun setupMqttSubscription(topic: String) {
        mqttManager.subscribe(topic,
            onSubscribed = {
                mqttManager.setMessageCallback { message ->
                    handleSensorUpdate(message)
                }
            },
            onError = { Log.d("SensorDetailsActivity", "Erro ao se inscrever no tópico") }
        )
    }

    private fun handleSensorUpdate(message: String) {
        val valor : String = message.toString()
        val valorformatado: String? = valor?.let { String.format("%.1f", it.toDouble()) }
        var text: String? = null
        if (tipoSensor == "termometro"){
            text  = (valorformatado +" ºC")
        }else{
            text = (valorformatado +"%")
        }

        tvacao.text = text

        updateSensorData(dispId, tvnome.text.toString(), tipoSensor, message)
    }

    private fun initView() {
        tvnome = findViewById(R.id.tvlogId)
        tvacao = findViewById(R.id.tvacao)
        tvdata = findViewById(R.id.tvData)
        btnUpdate = findViewById(R.id.btnUpdateSen)
        btnDelete = findViewById(R.id.btnDeleteSen)
    }

    private fun setValuesToViews() {
        tvnome.text = intent.getStringExtra("nome")
        tvacao.text = intent.getStringExtra("valorinicial")
        tvdata.text = intent.getStringExtra("timestamp")
    }

    private fun openUpdateDialog(dispId: String, dispNome: String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.updatesen_dialog, null)
        val valorsen = intent.getStringExtra("valor").toString()

        mDialog.setView(mDialogView)
        val etsenNome = mDialogView.findViewById<EditText>(R.id.etsenNome)
        val etsenUnidade = mDialogView.findViewById<EditText>(R.id.etsenUnidade)

        etsenNome.setText(tvnome.text)
        etsenUnidade.setText(tvdata.text)

        val btnUpdateDataSen = mDialogView.findViewById<Button>(R.id.btnUpdateDataSen)

        mDialog.setTitle("Atualizando registro de $dispNome")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateDataSen.setOnClickListener {
            updateSensorData(
                dispId,
                etsenNome.text.toString(),
                tipoSensor,
                valorsen
            )

            Toast.makeText(applicationContext, "Dados do sensor atualizados", Toast.LENGTH_LONG).show()

            tvnome.text = etsenNome.text.toString()
            tvdata.text = etsenUnidade.text.toString()

            alertDialog.dismiss()
        }
    }

    private fun updateSensorData(id: String, nome: String, tipo: String, valor: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Sensores").child(id)
        val sensorInfo = Sensor(id, nome, tipo, valor, aux, timestamp)

        dbRef.setValue(sensorInfo)
            .addOnSuccessListener {
                Log.d("SensorDetailsActivity", "Dados do sensor atualizados no Firebase")
            }
            .addOnFailureListener { error ->
                Log.e("SensorDetailsActivity", "Falha ao atualizar dados no Firebase", error)
            }
    }

    private fun deleteRecord(id: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Sensores").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Dados do sensor deletados", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Erro ao deletar: ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttManager.disconnect()
    }
}
