package com.example.crud_banco.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.crud_banco.R
import com.example.crud_banco.models.DispositivosModelo
import com.example.crud_banco.models.MqttManager
import com.google.firebase.database.FirebaseDatabase



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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispositivos_details)
        initView()
        setValuesToViews()

        val host = "5c4f71f03f934cd08ccae931b3d4a457.s1.eu.hivemq.cloud"
        val username = "admin"
        val password = "Admin1234!"

        mqttManager = MqttManager(host, username, password)

        mqttManager.connect(
            onConnected = {
                mqttManager.subscribe("casa/luz",
                    onSubscribed = {
                        // Configurar o switch após a inscrição
                        setupSwitch()
                    },
                    onError = { Log.d("MainActivity", "Erro ao se inscrever no topic")}
                )
            },
            onError = { Log.d("MainActivity", "Erro na conexão") }
        )


        btnUpdate.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("dispId").toString(),
                intent.getStringExtra("dispNome").toString()

            )
        }

        btnDelete.setOnClickListener {
            deleteRecord(
                intent.getStringExtra("dispId").toString()
            )
        }


    }

    private fun setupSwitch() {
        controlSwitch = findViewById(R.id.controlSwitch) // Certifique-se de que o ID corresponde ao seu layout

        controlSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            val message = if (isChecked) {
                "ligar"
            } else {
                "desligar"
            }

            updateDispStatus(intent.getStringExtra("dispId").toString(), message)
            mqttManager.publish("casa/luz", message,
                onSuccess = { Log.d("MainActivity", "Mensagem '$message' enviada com sucesso") },
                onError = { Log.e("MainActivity", "Falha ao enviar mensagem '$message'", it) }
            )
        }
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

    private fun openUpdateDialog(
        dispId: String,
        dispNome: String
    ) {
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

        mDialog.setTitle("Updating $dispNome Record")

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

            Toast.makeText(applicationContext, "Dispositivos Data Updated", Toast.LENGTH_LONG)
                .show()

            //we are setting updated data to our textviews
            tvDispName.text = etDispNome.text.toString()
            tvDispTipo.text = etDispTipo.text.toString()
            tvDispStatus.text = etDispStatus.text.toString()
            tvDispLocal.text = etDispLocal.text.toString()
            tvDispDtInst.text = etDispDtInst.text.toString()
            tvDispDtAtt.text = etDispDtAtt.text.toString()

            alertDialog.dismiss()
        }
    }

    private fun updateDispData(
        id: String,
        nome: String,
        tipo: String,
        status: String,
        local: String,
        dtInst: String,
        dtAtt: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp").child(id)
        val dispInfo = DispositivosModelo(id, nome, tipo, status, local, dtInst, dtAtt)
        dbRef.setValue(dispInfo)
    }

    private fun updateDispStatus(id: String, newStatus: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp").child(id)

        // Atualiza apenas o campo de status
        dbRef.child("dispStatus").setValue(newStatus)
            .addOnSuccessListener {
                tvDispStatus.text = newStatus.toString()
                Log.d("UpdateStatus", "Status atualizado com sucesso para: $newStatus")
            }
            .addOnFailureListener { error ->
                Log.e("UpdateStatus", "Falha ao atualizar status", error)
            }
    }


    private fun deleteRecord(
        id: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Dispositivo data deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mqttManager.disconnect()
    }
}