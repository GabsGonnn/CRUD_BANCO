package com.example.crud_banco

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_banco.activities.LogDetailsActivity
import com.example.crud_banco.adapters.LogAdapter
import com.example.crud_banco.models.LogModelo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoricoFragment : Fragment() {

    private lateinit var dispRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var dispList: ArrayList<LogModelo>
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infle o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_historico, container, false)

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

        dbRef = FirebaseDatabase.getInstance().getReference("logs_atividade")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dispList.clear()
                if (snapshot.exists()) {
                    for (empSnap in snapshot.children) {
                        val empData = empSnap.getValue(LogModelo::class.java)
                        empData?.let { dispList.add(it) }
                    }
                    val mAdapter = LogAdapter(dispList)
                    dispRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : LogAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(requireContext(), LogDetailsActivity::class.java)

                            intent.putExtra("nome", dispList[position].nome)
                            intent.putExtra("acao", dispList[position].acao)
                            intent.putExtra("timestamp", dispList[position].timestamp)
                            startActivity(intent)
                        }
                    })

                    dispRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}