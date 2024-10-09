package com.example.crud_banco.activities

import android.app.AlertDialog
import android.content.Intent
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
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.crud_banco.HistoricoFragment
import com.example.crud_banco.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

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
                com.example.crud_banco.R.id.shorts -> replaceFragment(InsertionFragment())
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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Adicionar Item")

        // Inflate o layout do diálogo
        val dialogView = layoutInflater.inflate(R.layout.insertion_dialog, null)
        builder.setView(dialogView)

        // Referências aos campos do diálogo
        val editTextName = dialogView.findViewById<EditText>(R.id.etDispNome)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerType)
        val editTextLocation = dialogView.findViewById<EditText>(R.id.etDispLocal)
        val editTextDate = dialogView.findViewById<EditText>(R.id.etDispDtInst)

        // Configurar o Spinner
        val types = arrayOf("lâmpada", "ventilador", "sensor")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Configurar os botões do diálogo
        builder.setPositiveButton("Adicionar") { dialog, which ->
            val name = editTextName.text.toString()
            val type = spinnerType.selectedItem.toString()
            val location = editTextLocation.text.toString()
            val date = editTextDate.text.toString()

            // Aqui você pode fazer o que quiser com os dados (ex: salvar em uma lista)
            // Exemplo: Toast.makeText(this, "Item Adicionado: $name", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
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




