package com.example.crud_banco

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InsertionActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference

    private lateinit var etDispNome: EditText
    private lateinit var etDispTipo: EditText
    private lateinit var etDispLocal: EditText
    private lateinit var etDisptDtInst: EditText
    private lateinit var btnSalvar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertion)

        etDispNome = findViewById(R.id.etDispNome)
        etDispTipo = findViewById(R.id.etDispTipo)
        etDispLocal = findViewById(R.id.etDispLocal)
        etDisptDtInst = findViewById(R.id.etDispDtInst)
        btnSalvar = findViewById(R.id.btnSalvar)

        dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp")

        btnSalvar.setOnClickListener{
            saveDispositivos()
        }
    }

    private fun saveDispositivos(){
        val dispNome = etDispNome.text.toString()
        val dispTipo = etDispTipo.text.toString()
        val dispStatus = "desligado"
        val dispLocal = etDispLocal.text.toString()
        val dispDtInst = etDisptDtInst.text.toString()
        val dispDtAtt = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        if (dispNome.isEmpty()){
            etDispNome.error ="Insira o nome do dispositivo pfv :)"
        }
        if (dispTipo.isEmpty()){
            etDispTipo.error ="Insira o tipo do dispositivo pfv :)"
        }
        if (dispLocal.isEmpty()){
            etDispLocal.error ="Insira o local do dispositivo pfv :)"
        }
        if (dispDtInst.isEmpty()){
            etDisptDtInst.error ="Insira a data de instalação do dispositivo pfv :)"
        }

        val dispId = dbRef.push().key?: ""

        val dispositivosss = DispositivosModelo(dispId,dispNome,dispTipo,dispStatus,dispLocal,dispDtInst,dispDtAtt)

        dbRef.child(dispId).setValue(dispositivosss)
            .addOnCompleteListener{
                Toast.makeText(this,"Dado inserido com sucesso",Toast.LENGTH_SHORT).show()

                etDispNome.text.clear()
                etDispTipo.text.clear()
                etDisptDtInst.text.clear()
                etDispLocal.text.clear()

            }.addOnFailureListener{ err->
                Toast.makeText(this,"Erro ${err.message}",Toast.LENGTH_SHORT).show()
            }
    }

}