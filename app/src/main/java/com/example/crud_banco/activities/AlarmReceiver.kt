package com.example.crud_banco.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.crud_banco.models.LogModelo
import com.example.crud_banco.models.MqttManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var mqttManager: MqttManager
    private lateinit var topic: String
    private lateinit var acao: String

    override fun onReceive(context: Context, intent: Intent) {
        val dispositivo = intent.getStringExtra("dispositivo")
        acao = intent.getStringExtra("acao").toString()
        val tipoDisp = intent.getStringExtra("tipoDisp")
        val aux = intent.getStringExtra("aux")
        val requestCode = intent.getIntExtra("requestCode", -1)

        // Configurações do MQTT
        val host = "5c4f71f03f934cd08ccae931b3d4a457.s1.eu.hivemq.cloud"
        val username = "admin"
        val password = "Admin1234!"

        mqttManager = MqttManager(host, username, password)

        mqttManager.connect(
            onConnected = {
                topic = if (tipoDisp == "lampada") {
                    "casa/luz/$aux"
                } else {
                    "casa/ventilador/$aux"
                }

                mqttManager.subscribe(topic,
                    onSubscribed = {
                        mqttManager.publish(topic, acao.toString(),
                            onSuccess = {
                                Log.d("AlarmReceiver", "Mensagem '$acao' enviada com sucesso")
                                // Após enviar a mensagem, remover a ação e atualizar o status
                                removeActionByRequestCode(dispositivo, requestCode, context)
                            },
                            onError = { Log.e("AlarmReceiver", "Falha ao enviar mensagem '$acao'", it) }
                        )
                    },
                    onError = { Log.d("AlarmReceiver", "Erro ao se inscrever no tópico") }
                )
            },
            onError = { Log.d("AlarmReceiver", "Erro na conexão") }
        )

        Toast.makeText(context, "Ação: $acao no dispositivo: $dispositivo", Toast.LENGTH_SHORT).show()

        // Log de atividade
        val dbRef2 = FirebaseDatabase.getInstance().getReference("logs_atividade")
        val logtimestamp = getCurrentDateOnly()
        val logtimestamphora = getCurrentDateOnlyhora()
        val dispId = dbRef2.push().key ?: ""

        val loggg = LogModelo(dispId, dispositivo, acao, logtimestamp, logtimestamphora)

        dbRef2.child(dispId).setValue(loggg)
            .addOnCompleteListener {
                Log.d("AlarmReceiver", "Log de atividade salvo com sucesso")
            }.addOnFailureListener { err ->
                Log.e("AlarmReceiver", "Erro ao salvar log: ${err.message}")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateOnly(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return currentDate.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateOnlyhora(): String {
        val currentTimehora = LocalTime.now()
        val formatterhora = DateTimeFormatter.ofPattern("HH:mm")
        return currentTimehora.format(formatterhora)
    }

    private fun removeActionByRequestCode(dispositivo: String?, requestCode: Int, context: Context) {
        val dbRefFunc = FirebaseDatabase.getInstance().getReference("Funci_Luz")

        // Remover a ação específica do banco "Funci_Luz" usando o requestCode
        dbRefFunc.orderByChild("nomeDisp").equalTo(dispositivo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (actionSnap in snapshot.children) {
                    val actionRequestCode = actionSnap.child("requestCode").getValue(Int::class.java) // Supondo que você tenha um campo "requestCode"
                    if (actionRequestCode == requestCode) {
                        actionSnap.ref.removeValue().addOnCompleteListener {
                            Log.d("AlarmReceiver", "Ação removida com sucesso: $requestCode")
                        }.addOnFailureListener { err ->
                            Log.e("AlarmReceiver", "Erro ao remover a ação: ${err.message}")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AlarmReceiver", "Erro ao buscar ações: ${error.message}")
            }
        })

        // Atualizar o status do dispositivo no banco "Exemplo_Disp"
        val dbRefDisp = FirebaseDatabase.getInstance().getReference("Exemplo_Disp")
        dbRefDisp.orderByChild("dispNome").equalTo(dispositivo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (deviceSnap in snapshot.children) {
                        deviceSnap.ref.child("dispStatus").setValue(acao)
                            .addOnCompleteListener {
                                Log.d("AlarmReceiver", "Status do dispositivo atualizado para: $acao")
                            }
                            .addOnFailureListener { err ->
                                Log.e("AlarmReceiver", "Erro ao atualizar status: ${err.message}")
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AlarmReceiver", "Erro ao buscar dispositivos: ${error.message}")
            }
        })
    }
}
