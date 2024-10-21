package com.example.crud_banco

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_banco.activities.ControlesDetailsActivity
import com.example.crud_banco.activities.SensorDetailsActivity
import com.example.crud_banco.adapters.ControleAdapter
import com.example.crud_banco.models.Controle
import com.example.crud_banco.models.Sensor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ControleFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var controleAdapter: ControleAdapter
    private lateinit var controleList: MutableList<Controle>
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_controle, container, false)

        // Inicializa a lista de sensores
        controleList = mutableListOf()

        // Inicializa o RecyclerView
        recyclerView = view.findViewById(R.id.rvSensoresLists)
        controleAdapter = ControleAdapter(controleList)
        val layoutManager = GridLayoutManager(requireContext(), 2)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = controleAdapter

        getSensorData()

        return view
    }

    private fun getSensorData() {
        dbRef = FirebaseDatabase.getInstance().getReference("Funci_Luz")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                controleList.clear()
                if (snapshot.exists()) {
                    for (sensorSnap in snapshot.children) {
                        val sensorData = sensorSnap.getValue(Controle::class.java)
                        sensorData?.let {
                            // Define a imagem com base no tipo do sensor
                            val iconResId = when (it.tipoDisp) {
                                "lampada" -> R.drawable.lamp_icon
                                "ventilador" -> R.drawable.vent_icon
                                else -> R.drawable.temp_icon // Imagem padrão, se necessário
                            }
                            it.imagem = iconResId
                            controleList.add(it) // Adiciona o sensor com a imagem correta
                        }
                    }
                    controleAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Tratar erro se necessário
            }
        })

        // Configurar o listener para cliques nos itens
        controleAdapter.setOnItemClickListener(object : ControleAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), ControlesDetailsActivity::class.java)
                intent.putExtra("controleId", controleList[position].controleId)
                intent.putExtra("nomeDisp", controleList[position].nomeDisp)
                intent.putExtra("valor", controleList[position].valor)
                intent.putExtra("acao", controleList[position].acao)
                intent.putExtra("tipoDisp",controleList[position].tipoDisp)
                intent.putExtra("aux",controleList[position].aux)
                intent.putExtra("requestCode",controleList[position].requestCode)
                startActivity(intent)
            }
        })
    }

}