package com.example.crud_banco.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_banco.R
import com.example.crud_banco.adapters.DispAdapter
import com.example.crud_banco.models.DispositivosModelo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FetchingActivity : AppCompatActivity() {

    private lateinit var dispRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var dispList: ArrayList<DispositivosModelo>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)

        dispRecyclerView = findViewById(R.id.rvDisp)
        dispRecyclerView.layoutManager = LinearLayoutManager(this)
        dispRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        dispList = arrayListOf<DispositivosModelo>()

        getEmployeesData()

    }

    private fun getEmployeesData() {

        dispRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dispList.clear()
                if (snapshot.exists()){
                    for (empSnap in snapshot.children){
                        val empData = empSnap.getValue(DispositivosModelo::class.java)
                        dispList.add(empData!!)
                    }
                    val mAdapter = DispAdapter(dispList)
                    dispRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : DispAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@FetchingActivity, DispositivosDetailsActivity::class.java)

                            //put extras
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
                TODO("Not yet implemented")
            }

        })
    }
}