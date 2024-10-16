package com.example.crud_banco.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.crud_banco.R
import com.example.crud_banco.models.DispositivosModelo
import com.example.crud_banco.models.LogModelo
import com.example.crud_banco.models.MqttManager
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DispositivosDetailsActivity : AppCompatActivity() {

    private lateinit var tvDispId: TextView
    private lateinit var tvDispName: TextView
    private lateinit var tvDispTipo: TextView
    private lateinit var tvDispStatus: TextView
    private lateinit var tvDispLocal: TextView
    private lateinit var tvDispDtInst: TextView
    private lateinit var tvDispDtAtt: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var controlSwitch: SwitchCompat
    private lateinit var mqttManager: MqttManager

    private lateinit var dispTipo: String
    private lateinit var dispId: String
    private lateinit var dispAux: String

    private lateinit var dbRef2: DatabaseReference

    private lateinit var topic: String // Adicione uma variável para armazenar o tópico

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispositivos_details)
        initView()
        setValuesToViews()

        val host = "5c4f71f03f934cd08ccae931b3d4a457.s1.eu.hivemq.cloud"
        val username = "admin"
        val password = "Admin1234!"

        mqttManager = MqttManager(host, username, password)

        dispTipo = intent.getStringExtra("dispTipo").toString()
        dispId = intent.getStringExtra("dispId").toString()
        dispAux = intent.getStringExtra("dispAux").toString()

        mqttManager.connect(
            onConnected = {
                getTopicForDevice() // Chame para obter o tópico
            },
            onError = { Log.d("MainActivity", "Erro na conexão") }
        )

        btnUpdate.setOnClickListener {
            openUpdateDialog(dispId, intent.getStringExtra("dispNome").toString())
        }

        btnDelete.setOnClickListener {
            deleteRecord(dispId)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateOnly(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return currentDate.format(formatter)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateOnlyhora(): String {
        val currentTimehora = LocalTime.now()
        val formatterhora = DateTimeFormatter.ofPattern("HH:mm")
        return currentTimehora.format(formatterhora)
    }

    private fun getTopicForDevice() {


        topic = if (dispTipo == "lampada")
        {
            "casa/luz/$dispAux"
        } else {
            "casa/ventilador/$dispAux"
        }

        setupMqttSubscription(topic)
        setupSwitch()
    }


    private fun setupSwitch() {
        controlSwitch = findViewById(R.id.controlSwitch)

        controlSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            val message = if (isChecked) {
                "ligar"
            } else {
                "desligar"
            }
            updateDispStatus(dispId, message)

            val acao = message
            dbRef2 = FirebaseDatabase.getInstance().getReference("logs_atividade")

            val lognome = intent.getStringExtra("dispNome")
            val logtimestamp = getCurrentDateOnly()
            val logtimestamphora = getCurrentDateOnlyhora()

            val dispId = dbRef2.push().key?: ""

            val loggg = LogModelo(dispId,lognome, acao, logtimestamp, logtimestamphora)

            dbRef2.child(dispId).setValue(loggg)
                .addOnCompleteListener{
                    Toast.makeText(this,"Dado inserido com sucesso",Toast.LENGTH_SHORT).show()

                }.addOnFailureListener{ err->
                    Toast.makeText(this,"Erro ${err.message}",Toast.LENGTH_SHORT).show()
                }

            mqttManager.publish(topic, message, // Use o tópico armazenado
                onSuccess = { Log.d("MainActivity", "Mensagem '$message' enviada com sucesso") },
                onError = { Log.e("MainActivity", "Falha ao enviar mensagem '$message'", it) }
            )
        }
    }

    private fun setupMqttSubscription(topic: String) {
        mqttManager.subscribe(topic,
            onSubscribed = {
                setupSwitch()
            },
            onError = { Log.d("MainActivity", "Erro ao se inscrever no tópico") }
        )
    }


    private fun initView() {
        tvDispId = findViewById(R.id.tvDispId)
        tvDispName = findViewById(R.id.tvDispName)
        tvDispStatus = findViewById(R.id.tvDispStatus)
        tvDispTipo = findViewById(R.id.tvDispTipo)
        tvDispLocal = findViewById(R.id.tvDispLocal)
        tvDispDtInst = findViewById(R.id.tvDispDtInst)
        tvDispDtAtt = findViewById(R.id.tvDispDtAtt)

        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
    }

    private fun setValuesToViews() {
        tvDispId.text = intent.getStringExtra("dispId")
        tvDispName.text = intent.getStringExtra("dispNome")
        tvDispTipo.text = intent.getStringExtra("dispTipo")
        tvDispStatus.text = intent.getStringExtra("dispStatus")
        tvDispLocal.text = intent.getStringExtra("dispLocal")
        tvDispDtInst.text = intent.getStringExtra("dispDtInst")
        tvDispDtAtt.text = intent.getStringExtra("dispDtAtt")
    }

    private fun openUpdateDialog(dispId: String, dispNome: String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog, null)

        mDialog.setView(mDialogView)
        val etDispNome = mDialogView.findViewById<EditText>(R.id.etDispNome)
        val etDispTipo = mDialogView.findViewById<EditText>(R.id.etDispTipo)
        val etDispStatus = mDialogView.findViewById<EditText>(R.id.etDispStatus)
        val etDispLocal = mDialogView.findViewById<EditText>(R.id.etDispLocal)
        val etDispDtInst = mDialogView.findViewById<EditText>(R.id.etDispDtInst)
        val etDispDtAtt = mDialogView.findViewById<EditText>(R.id.etDispDtAtt)

        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        etDispNome.setText(tvDispName.text.toString())
        etDispTipo.setText(tvDispTipo.text.toString())
        etDispStatus.setText(tvDispStatus.text.toString())
        etDispLocal.setText(tvDispLocal.text.toString())
        etDispDtInst.setText(tvDispDtInst.text.toString())
        etDispDtAtt.setText(tvDispDtAtt.text.toString())

        mDialog.setTitle("Atualizando registro de $dispNome")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateDispData(
                dispId,
                etDispNome.text.toString(),
                etDispTipo.text.toString(),
                etDispStatus.text.toString(),
                etDispLocal.text.toString(),
                etDispDtInst.text.toString(),
                etDispDtAtt.text.toString(),
            )

            Toast.makeText(applicationContext, "Dados do dispositivo atualizados", Toast.LENGTH_LONG).show()

            // Atualiza os dados nos TextViews
            tvDispName.text = etDispNome.text.toString()
            tvDispTipo.text = etDispTipo.text.toString()
            tvDispStatus.text = etDispStatus.text.toString()
            tvDispLocal.text = etDispLocal.text.toString()
            tvDispDtInst.text = etDispDtInst.text.toString()
            tvDispDtAtt.text = etDispDtAtt.text.toString()

            alertDialog.dismiss()
        }
    }

    private fun updateDispData(id: String, nome: String, tipo: String, status: String, local: String, dtInst: String, dtAtt: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp").child(id)
        val dispInfo = DispositivosModelo(id, nome, tipo, status, local, dtInst, dtAtt)
        dbRef.setValue(dispInfo)
    }

    private fun updateDispStatus(id: String, newStatus: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp").child(id)

        // Atualiza apenas o campo de status
        dbRef.child("dispStatus").setValue(newStatus)
            .addOnSuccessListener {
                tvDispStatus.text = newStatus
                Log.d("UpdateStatus", "Status atualizado com sucesso para: $newStatus")
            }
            .addOnFailureListener { error ->
                Log.e("UpdateStatus", "Falha ao atualizar status", error)
            }
    }

    private fun deleteRecord(id: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp").child(id)
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

    override fun onDestroy() {
        super.onDestroy()
        mqttManager.disconnect()
    }
}