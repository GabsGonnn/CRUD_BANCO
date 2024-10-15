package com.example.crud_banco.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.crud_banco.R
import com.example.crud_banco.models.Sensor
import com.google.firebase.database.FirebaseDatabase

class SensorDetailsActivity : AppCompatActivity() {
    private lateinit var tvnome: TextView
    private lateinit var tvacao: TextView
    private lateinit var tvdata: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_details)
        initView()
        setValuesToViews()

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

    private fun initView() {
        tvnome = findViewById(R.id.tvlogId)
        tvacao = findViewById(R.id.tvacao)
        tvdata = findViewById(R.id.tvData)
        btnUpdate = findViewById(R.id.btnUpdateSen) // Inicializando o botão de atualizar
        btnDelete = findViewById(R.id.btnDeleteSen) // Inicializando o botão de deletar
    }

    private fun setValuesToViews() {
        tvnome.text = intent.getStringExtra("nome")
        tvacao.text = intent.getStringExtra("valor")
        tvdata.text = intent.getStringExtra("unidade")
    }

    private fun openUpdateDialog(dispId: String, dispNome: String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.updatesen_dialog, null)

        mDialog.setView(mDialogView)
        val etsenNome = mDialogView.findViewById<EditText>(R.id.etsenNome)
        val etsenValor = mDialogView.findViewById<EditText>(R.id.etsenValor)
        val etsenUnidade = mDialogView.findViewById<EditText>(R.id.etsenUnidade)

        // Definindo os valores atuais
        etsenNome.setText(tvnome.text)
        etsenValor.setText(tvacao.text)
        etsenUnidade.setText(tvdata.text)

        val btnUpdateDataSen = mDialogView.findViewById<Button>(R.id.btnUpdateDataSen)

        mDialog.setTitle("Updating $dispNome Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateDataSen.setOnClickListener {
            updateDispData(
                dispId,
                etsenNome.text.toString(),
                etsenValor.text.toString(),
                etsenUnidade.text.toString()
            )

            Toast.makeText(applicationContext, "Dispositivo Data Updated", Toast.LENGTH_LONG).show()

            // Atualiza os textos na Activity
            tvnome.text = etsenNome.text.toString()
            tvacao.text = etsenValor.text.toString()
            tvdata.text = etsenUnidade.text.toString()

            alertDialog.dismiss()
        }
    }

    private fun updateDispData(id: String, nome: String, tipo: String, status: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Sensores").child(id)
        val dispInfo = Sensor(id, nome, tipo, status)
        dbRef.setValue(dispInfo)
    }

    private fun deleteRecord(id: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Sensores").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Dispositivo data deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Deleting Err: ${error.message}", Toast.LENGTH_LONG).show()
        }
    }
}
