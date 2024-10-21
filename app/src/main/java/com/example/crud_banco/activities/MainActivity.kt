package com.example.crud_banco.activities

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.crud_banco.HomeFragment
import com.example.crud_banco.R

import android.provider.Settings
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.crud_banco.ControleFragment
import com.example.crud_banco.HistoricoFragment
import com.example.crud_banco.SensorFragment
import com.example.crud_banco.databinding.ActivityMainBinding
import com.example.crud_banco.models.Controle
import com.example.crud_banco.models.DispositivosModelo
import com.example.crud_banco.models.Sensor
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.crud_banco.R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
            com.example.crud_banco.R.string.open_nav, com.example.crud_banco.R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener (this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.crud_banco.R.id.home -> replaceFragment(HomeFragment())
                com.example.crud_banco.R.id.shorts -> replaceFragment(SensorFragment())
                com.example.crud_banco.R.id.Incricoes -> replaceFragment(ControleFragment())
                com.example.crud_banco.R.id.Biblioteca -> replaceFragment(HistoricoFragment())
            }
            true
        }

        fragmentManager = supportFragmentManager
        replaceFragment(HomeFragment())



        binding.fab.setOnClickListener {
            showBottomDialog()
        }
    }


    private fun openNetworkConnections() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivity(intent)
    }

    private fun showBottomDialog() {
        val builder = Dialog(this)
        builder.setTitle("Adicionar Item")
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.setContentView(com.example.crud_banco.R.layout.bottomsheetlayout)


        val dispDialog = builder.findViewById<LinearLayout>(com.example.crud_banco.R.id.layoutVideo)
        val controlLuzDialog = builder.findViewById<LinearLayout>(com.example.crud_banco.R.id.layoutShorts)
        val cancelButton = builder.findViewById<ImageView>(com.example.crud_banco.R.id.cancelButton)


        dispDialog.setOnClickListener {
            builder.dismiss()
            showInsertDeviceDialog()
        }

        controlLuzDialog.setOnClickListener {
            builder.dismiss()
            showControleLuzDialog()
        }

        cancelButton.setOnClickListener {
            builder.dismiss()
        }

        builder.show()
        builder.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.window?.attributes?.windowAnimations = com.example.crud_banco.R.style.DialogAnimation
        builder.window?.setGravity(Gravity.BOTTOM)
    }


    private fun showControleLuzDialog() {
        val dialogBuilder = Dialog(this)
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogBuilder.setContentView(R.layout.insertion_controle_luz_dialog)

        // Inicializando os componentes do diálogo
        val spinnerNome = dialogBuilder.findViewById<Spinner>(R.id.spinnerLuzNome)
        val spinnerAcao = dialogBuilder.findViewById<Spinner>(R.id.spinnerLuzAção)
        val timePicker = dialogBuilder.findViewById<TimePicker>(R.id.timePickerLuz)
        val addButton = dialogBuilder.findViewById<Button>(R.id.btnAdd)

        // Carregar nomes dos dispositivos do banco "Exemplo_Disp"
        val dbRefDisp = FirebaseDatabase.getInstance().getReference("Exemplo_Disp")
        val nomes = mutableListOf<String>()

        dbRefDisp.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dispositivoSnap in snapshot.children) {
                        val dispositivo = dispositivoSnap.getValue(DispositivosModelo::class.java)
                        dispositivo?.let {
                            nomes.add(it.dispNome ?: "Nome Desconhecido") // Adiciona o nome do dispositivo
                        }
                    }
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, nomes)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerNome.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Erro ao carregar dispositivos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Configurando o Spinner para ações
        val acoes = arrayOf("ligar", "desligar") // Ações disponíveis
        val acaoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, acoes)
        acaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAcao.adapter = acaoAdapter

        // Configurando o botão de adicionar
        addButton.setOnClickListener {
            val nomeSelecionado = spinnerNome.selectedItem.toString()
            val acaoSelecionada = spinnerAcao.selectedItem.toString()
            val hora = timePicker.hour
            val minuto = timePicker.minute

            // Formatar a hora em string
            val horaFormatada = String.format("%02d:%02d", hora, minuto)

            // Buscar o tipo e o valor "aux" do dispositivo selecionado
            dbRefDisp.orderByChild("dispNome").equalTo(nomeSelecionado).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val dispositivo = snapshot.children.first().getValue(DispositivosModelo::class.java)
                        dispositivo?.let {
                            val tipoDisp = it.dispTipo // Supondo que você tenha um campo dispTipo
                            val aux = it.dispAux // Supondo que você tenha um campo aux

                            // Buscar o maior requestCode existente
                            val dbRefFunc = FirebaseDatabase.getInstance().getReference("Funci_Luz")
                            dbRefFunc.orderByChild("requestCode").limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val requestCode = if (snapshot.exists()) {
                                        val lastControle = snapshot.children.first().getValue(Controle::class.java)
                                        (lastControle?.requestCode ?: 0) + 1 // Incrementa o maior requestCode encontrado
                                    } else {
                                        1 // Se não houver nenhum controle, começa com 1
                                    }

                                    // Adicionar ao banco "Funci_Luz"
                                    val controleId = dbRefFunc.push().key ?: ""
                                    val controleLuz = Controle(controleId, nomeSelecionado, acaoSelecionada, horaFormatada, tipoDisp, aux, requestCode)

                                    dbRefFunc.child(controleId).setValue(controleLuz)
                                        .addOnCompleteListener {
                                            Toast.makeText(this@MainActivity, "Controle de Luz adicionado com sucesso", Toast.LENGTH_SHORT).show()
                                            dialogBuilder.dismiss()

                                            // Agendar a ação
                                            scheduleAction(hora, minuto, nomeSelecionado, acaoSelecionada, tipoDisp.toString(), aux.toString(), requestCode)
                                        }.addOnFailureListener { err ->
                                            Toast.makeText(this@MainActivity, "Erro: ${err.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@MainActivity, "Erro ao buscar requestCode: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Erro ao buscar dispositivo: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        dialogBuilder.show()
        dialogBuilder.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    // Função para agendar a ação
    private fun scheduleAction(hora: Int, minuto: Int, dispositivo: String, acao: String, tipoDisp: String, aux: String, requestCode: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("dispositivo", dispositivo)
            putExtra("acao", acao)
            putExtra("tipoDisp", tipoDisp)
            putExtra("aux", aux)
            putExtra("requestCode", requestCode)
        }

        // Definindo o tempo para o alarme
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            // Se a hora já passou, agendar para o próximo dia
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }


    private fun showInsertDeviceDialog() {
        val dialogBuilder = Dialog(this)
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogBuilder.setContentView(R.layout.insertion_dispositivo_dialog)

        val editTextName = dialogBuilder.findViewById<EditText>(R.id.etDispNome)
        val spinnerType = dialogBuilder.findViewById<Spinner>(R.id.spinnerType)
        val editTextLocation = dialogBuilder.findViewById<EditText>(R.id.etDispLocal)
        val textViewDate = dialogBuilder.findViewById<TextView>(R.id.tvDispDtInst)
        val buttonPickDate = dialogBuilder.findViewById<Button>(R.id.btnPickDate)

        val types = arrayOf("lampada", "ventilador", "termometro", "higrometro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Configurar o botão de selecionar data
        buttonPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    textViewDate.text = formattedDate // Atualiza o TextView com a data selecionada
                }, year, month, day)
            datePickerDialog.show()
        }

        // Configurar o botão de adicionar
        val addButton = dialogBuilder.findViewById<Button>(R.id.btnAdd)
        addButton.setOnClickListener {
            val nome = editTextName.text.toString()
            val tipo = spinnerType.selectedItem.toString()
            val local = editTextLocation.text.toString()
            val date = textViewDate.text.toString()

            if (nome.isEmpty() || local.isEmpty() || date == "Data de Instalação") {
                Toast.makeText(this@MainActivity, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tipo == "lampada" || tipo == "ventilador") {
                val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Exemplo_Disp")

                dbRef.orderByChild("dispTipo").equalTo(tipo).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val count = snapshot.children.count() + 1
                        val dispId = dbRef.push().key ?: ""
                        val dispositivosss = DispositivosModelo(dispId, nome, tipo, "desligado", local, date, date, count.toString())

                        dbRef.child(dispId).setValue(dispositivosss)
                            .addOnCompleteListener {
                                Toast.makeText(this@MainActivity, "Dado inserido com sucesso", Toast.LENGTH_SHORT).show()
                                clearFields(editTextName, editTextLocation, textViewDate, spinnerType)
                                dialogBuilder.dismiss()
                            }.addOnFailureListener { err ->
                                Toast.makeText(this@MainActivity, "Erro ${err.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MainActivity, "Erro ao contar dispositivos: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            } else {
                val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Exemplo_Sensores")
                val dispId = dbRef.push().key ?: ""

                dbRef.orderByChild("dispTipo").equalTo(tipo).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val count = snapshot.children.count() + 1
                        val dispositivosss = Sensor(dispId, nome, tipo, "0", count.toString(), date )

                        dbRef.child(dispId).setValue(dispositivosss)
                            .addOnCompleteListener {
                                Toast.makeText(this@MainActivity, "Dado inserido com sucesso", Toast.LENGTH_SHORT).show()
                                clearFields(editTextName, editTextLocation, textViewDate, spinnerType)
                                dialogBuilder.dismiss()
                            }.addOnFailureListener { err ->
                                Toast.makeText(this@MainActivity, "Erro ${err.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }

        dialogBuilder.show()
        dialogBuilder.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun clearFields(editTextName: EditText, editTextLocation: EditText, etDate: TextView, spinnerType: Spinner) {
        editTextName.text.clear()
        editTextLocation.text.clear()
        etDate.text = "Data de Instalação"
        spinnerType.setSelection(0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intentLog = Intent(this, SignInActivity::class.java)
        when(item.itemId){
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_conectar -> openNetworkConnections()
            R.id.nav_logout -> startActivity(intentLog)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.getOnBackPressedDispatcher().onBackPressed()
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.example.crud_banco.R.id.frame_container, fragment)
        fragmentTransaction.commit()
    }

    }




