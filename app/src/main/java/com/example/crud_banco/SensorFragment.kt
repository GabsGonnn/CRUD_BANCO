package com.example.crud_banco

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_banco.activities.SensorDetailsActivity
import com.example.crud_banco.adapters.SensorAdapter
import com.example.crud_banco.models.Sensor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SensorFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var sensorAdapter: SensorAdapter
    private lateinit var sensorList: MutableList<Sensor>
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sensor, container, false)

        // Inicializa a lista de sensores
        sensorList = mutableListOf()

        // Inicializa o RecyclerView
        recyclerView = view.findViewById(R.id.rvSensoresLists)
        sensorAdapter = SensorAdapter(sensorList)
        val layoutManager = GridLayoutManager(requireContext(), 2)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = sensorAdapter

        getSensorData()

        return view
    }

    private fun getSensorData() {
        dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Sensores")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sensorList.clear()
                if (snapshot.exists()) {
                    for (sensorSnap in snapshot.children) {
                        val sensorData = sensorSnap.getValue(Sensor::class.java)
                        sensorData?.let {
                            // Define a imagem com base no tipo do sensor
                            val iconResId = when (it.tipoSensor) {
                                "termometro" -> R.drawable.temp_icon
                                "higrometro" -> R.drawable.umidade_icon
                                else -> R.drawable.temp_icon // Imagem padrão, se necessário
                            }
                            it.imagem = iconResId
                            sensorList.add(it) // Adiciona o sensor com a imagem correta
                        }
                    }
                    sensorAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Tratar erro se necessário
            }
        })

        // Configurar o listener para cliques nos itens
        sensorAdapter.setOnItemClickListener(object : SensorAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), SensorDetailsActivity::class.java)
                intent.putExtra("dispId", sensorList[position].dispId)
                intent.putExtra("nome", sensorList[position].nome)
                intent.putExtra("valor", sensorList[position].valor)
                intent.putExtra("unidade", sensorList[position].unidade)
                startActivity(intent)
            }
        })
    }
}