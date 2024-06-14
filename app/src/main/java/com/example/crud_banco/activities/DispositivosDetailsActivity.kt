package com.example.crud_banco.activities
import org.json.JSONObject
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.example.crud_banco.R
import com.example.crud_banco.models.DispositivosModelo
import com.google.firebase.database.FirebaseDatabase
import MQTTManager

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
    private lateinit var switchPower: SwitchCompat
    //private lateinit var mqttManager: MQTTManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispositivos_details)
        initView()
        setValuesToViews()
        //mqttManager = MQTTManager(this)

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
        tvDispId = findViewById(R.id.tvDispId)
        tvDispName = findViewById(R.id.tvDispName)
        tvDispStatus = findViewById(R.id.tvDispStatus)
        tvDispTipo = findViewById(R.id.tvDispTipo)
        tvDispLocal = findViewById(R.id.tvDispLocal)
        tvDispDtInst = findViewById(R.id.tvDispDtInst)
        tvDispDtAtt = findViewById(R.id.tvDispDtAtt)

        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        switchPower = findViewById(R.id.switchPower)
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

        etDispNome.setText(intent.getStringExtra("dispNome").toString())
        etDispTipo.setText(intent.getStringExtra("dispTipo").toString())
        etDispStatus.setText(intent.getStringExtra("dispStatus").toString())
        etDispLocal.setText(intent.getStringExtra("dispLocal").toString())
        etDispDtInst.setText(intent.getStringExtra("dispDtInst").toString())
        etDispDtAtt.setText(intent.getStringExtra("dispDtAtt").toString())

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

    private fun deleteRecord(
        id: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Exemplo_Disp").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Dispositivo data deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, FetchingActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }

    }


    fun onSwitchClick(view: View) {
        val message = JSONObject()
        if (switchPower.isChecked) {
            message.put("led_Control", "1")
        } else {
            message.put("led_Control", "0")
        }

        val topic = "esp32/sub"
        //mqttManager.publish(topic, message.toString())
    }
}