package com.example.crud_banco

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_banco.activities.DispositivosDetailsActivity
import com.example.crud_banco.adapters.DispAdapter
import com.example.crud_banco.models.DispositivosModelo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var dispRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var dispList: ArrayList<DispositivosModelo>
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infle o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inicialize as views
        dispRecyclerView = view.findViewById(R.id.rvDisp)
        dispRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        dispRecyclerView.setHasFixedSize(true)
        tvLoadingData = view.findViewById(R.id.tvLoadingData)

        dispList = arrayListOf()

        getEmployeesData()

        return view
    }

    private fun getEmployeesData() {
        dispRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dispList.clear()
                if (snapshot.exists()) {
                    for (empSnap in snapshot.children) {
                        val empData = empSnap.getValue(DispositivosModelo::class.java)
                        empData?.let {
                            // Filtra os dispositivos por tipo
                            if (it.dispTipo == "lampada" || it.dispTipo == "ventilador") {
                                dispList.add(it)
                            }
                        }
                    }
                    val mAdapter = DispAdapter(dispList)
                    dispRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : DispAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(requireContext(), DispositivosDetailsActivity::class.java)

                            // Coloque extras
                            intent.putExtra("dispId", dispList[position].dispId)
                            intent.putExtra("dispNome", dispList[position].dispNome)
                            intent.putExtra("dispTipo", dispList[position].dispTipo)
                            intent.putExtra("dispStatus", dispList[position].dispStatus)
                            intent.putExtra("dispLocal", dispList[position].dispLocal)
                            intent.putExtra("dispDtInst", dispList[position].dispDtInst)
                            intent.putExtra("dispDtAtt", dispList[position].dispDtAtt)
                            startActivity(intent)
                        }
                    })

                    dispRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Tratar erro se necessário
            }
        })
    }
}
