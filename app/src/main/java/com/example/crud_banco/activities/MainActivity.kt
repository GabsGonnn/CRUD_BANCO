package com.example.crud_banco.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.crud_banco.HomeFragment
import com.example.crud_banco.InsertionFragment
import com.example.crud_banco.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import android.provider.Settings
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.crud_banco.HistoricoFragment
import com.example.crud_banco.SensorFragment
import com.example.crud_banco.databinding.ActivityMainBinding
import com.example.crud_banco.models.DispositivosModelo
import com.example.crud_banco.models.Sensor
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.crud_banco.R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                //com.example.crud_banco.R.id.Incricoes -> replaceFragment(HistoricoFragment())
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
        // val builder = AlertDialog.Builder(this)
        builder.setTitle("Adicionar Item")
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.setContentView(com.example.crud_banco.R.layout.bottomsheetlayout)


        val dispDialog = builder.findViewById<LinearLayout>(com.example.crud_banco.R.id.layoutVideo)
        val shortsLayout = builder.findViewById<LinearLayout>(com.example.crud_banco.R.id.layoutShorts)
        val liveLayout = builder.findViewById<LinearLayout>(com.example.crud_banco.R.id.layoutLive)
        val cancelButton = builder.findViewById<ImageView>(com.example.crud_banco.R.id.cancelButton)


        dispDialog.setOnClickListener {
            builder.dismiss()
            showInsertDeviceDialog()
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
    private fun showInsertDeviceDialog() {
        val dialogBuilder = Dialog(this)
        dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogBuilder.setContentView(R.layout.insertion_dialog)

        // Referências aos campos do diálogo
        val editTextName = dialogBuilder.findViewById<EditText>(R.id.etDispNome)
        val spinnerType = dialogBuilder.findViewById<Spinner>(R.id.spinnerType)
        val editTextLocation = dialogBuilder.findViewById<EditText>(R.id.etDispLocal)
        val editTextDate = dialogBuilder.findViewById<EditText>(R.id.etDispDtInst)

        // Configurar o Spinner
        val types = arrayOf("lampada", "ventilador", "termometro", "higrometro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Configurar o botão de adicionar
        val addButton = dialogBuilder.findViewById<Button>(R.id.btnAdd) // Supondo que você tenha um botão com esse ID
        addButton.setOnClickListener {
            val nome = editTextName.text.toString()
            val tipo = spinnerType.selectedItem.toString()
            val local = editTextLocation.text.toString()
            val date = editTextDate.text.toString()

            if (tipo == "lâmpada" || tipo == "ventilador") {
                val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Exemplo_Disp")
                val dispId = dbRef.push().key ?: ""
                val dispositivosss = DispositivosModelo(dispId, nome, tipo, "desligado", local, date, date)

                dbRef.child(dispId).setValue(dispositivosss)
                    .addOnCompleteListener {
                        Toast.makeText(this, "Dado inserido com sucesso", Toast.LENGTH_SHORT).show()
                        clearFields(editTextName, editTextLocation, editTextDate, spinnerType)
                        dialogBuilder.dismiss()
                    }.addOnFailureListener { err ->
                        Toast.makeText(this, "Erro ${err.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Exemplo_Sensores")
                val dispId = dbRef.push().key ?: ""
                val dispositivosss = Sensor(dispId, nome, tipo, "0", "0", date)

                dbRef.child(dispId).setValue(dispositivosss)
                    .addOnCompleteListener {
                        Toast.makeText(this, "Dado inserido com sucesso", Toast.LENGTH_SHORT).show()
                        clearFields(editTextName, editTextLocation, editTextDate, spinnerType)
                        dialogBuilder.dismiss()
                    }.addOnFailureListener { err ->
                        Toast.makeText(this, "Erro ${err.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        dialogBuilder.show()
        dialogBuilder.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun clearFields(editTextName: EditText, editTextLocation: EditText, editTextDate: EditText, spinnerType: Spinner) {
        editTextName.text.clear()
        editTextLocation.text.clear()
        editTextDate.text.clear()
        spinnerType.setSelection(0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_conectar -> openNetworkConnections()
            R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
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
        fragmentTransaction.replace(com.example.crud_banco.R.id.frame_container, fragment) // Atualizado
        fragmentTransaction.commit()
    }

    }




