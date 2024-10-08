package com.example.crud_banco

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crud_banco.models.DispositivosModelo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InsertionFragment : Fragment() {

    private lateinit var dbRef: DatabaseReference

    private lateinit var etDispNome: EditText
    private lateinit var etDispTipo: EditText
    private lateinit var etDispLocal: EditText
    private lateinit var etDispDtInst: EditText
    private lateinit var btnSalvar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_insertion, container, false)

        etDispNome = view.findViewById(R.id.etDispNome)
        etDispTipo = view.findViewById(R.id.etDispTipo)
        etDispLocal = view.findViewById(R.id.etDispLocal)
        etDispDtInst = view.findViewById(R.id.etDispDtInst)
        btnSalvar = view.findViewById(R.id.btnSalvar)

        dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp")

        btnSalvar.setOnClickListener {
            val dispNome = etDispNome.text.toString()
            val dispTipo = etDispTipo.text.toString()
            val dispStatus = "desligado"
            val dispLocal = etDispLocal.text.toString()
            val dispDtInst = etDispDtInst.text.toString()
            val dispDtAtt = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            // Verificações de campo vazio
            if (dispNome.isEmpty()) {
                etDispNome.error = "Insira o nome do dispositivo pfv :)"
                return@setOnClickListener
            }
            if (dispTipo.isEmpty()) {
                etDispTipo.error = "Insira o tipo do dispositivo pfv :)"
                return@setOnClickListener
            }
            if (dispLocal.isEmpty()) {
                etDispLocal.error = "Insira o local do dispositivo pfv :)"
                return@setOnClickListener
            }
            if (dispDtInst.isEmpty()) {
                etDispDtInst.error = "Insira a data de instalação do dispositivo pfv :)"
                return@setOnClickListener
            }

            val dispId = dbRef.push().key ?: ""

            val dispositivos = DispositivosModelo(dispId, dispNome, dispTipo, dispStatus, dispLocal, dispDtInst, dispDtAtt)

            dbRef.child(dispId).setValue(dispositivos)
                .addOnCompleteListener {
                    Toast.makeText(requireContext(), "Dado inserido com sucesso", Toast.LENGTH_SHORT).show()

                    etDispNome.text.clear()
                    etDispTipo.text.clear()
                    etDispDtInst.text.clear()
                    etDispLocal.text.clear()

                }.addOnFailureListener { err ->
                    Toast.makeText(requireContext(), "Erro ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }
}
