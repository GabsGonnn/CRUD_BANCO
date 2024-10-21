package com.example.crud_banco.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.crud_banco.R
import com.example.crud_banco.models.DispositivosModelo
import com.example.crud_banco.models.MqttManager
import com.example.crud_banco.models.Sensor
import com.google.firebase.database.FirebaseDatabase

class ControlesDetailsActivity : AppCompatActivity() {

    private lateinit var tvnome: TextView
    private lateinit var tvvalor: TextView
    private lateinit var tvacao: TextView
    private lateinit var btnDelete: Button
    private lateinit var btnUpdate: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controles_details)

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
        btnUpdate = findViewById(R.id.btnUpdateContr)
    }

    private fun setValuesToViews() {
        tvnome.text = intent.getStringExtra("nomeDisp")
        tvvalor.text = intent.getStringExtra("valor")
        tvacao.text = intent.getStringExtra("acao")
    }

    private fun openUpdateDialog(dispId: String, dispNome: String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_contr_dialog, null)

        mDialog.setView(mDialogView)
        val timepicker = mDialogView.findViewById<TimePicker>(R.id.timePickerLuzContr)

        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        val hora = (timepicker.hour).toString()
        val min = (timepicker.minute).toString()

        val etDispHorario: String = (hora + ":" + min)


        mDialog.setTitle("Atualizando registro de $dispNome")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateDispData(
                dispId,
                etDispNome.text.toString(),
                dispTipo,
                dispStatus,
                etDispLocal.text.toString(),
                etDispDtInst.text.toString(),
                etDispDtInst.text.toString(),
                dispAux
            )

            Toast.makeText(applicationContext, "Dados do dispositivo atualizados", Toast.LENGTH_LONG).show()

            // Atualiza os dados nos TextViews
            tvDispName.text = etDispNome.text.toString()
            tvDispLocal.text = etDispLocal.text.toString()
            tvDispDtInst.text = etDispDtInst.text.toString()
            alertDialog.dismiss()
        }
    }

    private fun updateDispData(id: String, nome: String, tipo: String, status: String, local: String, dtInst: String, dtAtt: String, aux: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Funci_Luz").child(id)
        val dispInfo = DispositivosModelo(id, nome, tipo, status, local, dtInst, dtAtt, aux)
        dbRef.setValue(dispInfo)
    }

    private fun deleteRecord(id: String) {
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

}