package com.example.crud_banco.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.crud_banco.HistoricoFragment
import com.example.crud_banco.HomeFragment
import com.example.crud_banco.InsertionFragment
import com.example.crud_banco.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.crud_banco.R.layout.activity_main)

        bottomNavigationView = findViewById(com.example.crud_banco.R.id.bottomNavigationView)
        fab = findViewById(com.example.crud_banco.R.id.fab)
        drawerLayout = findViewById(com.example.crud_banco.R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(com.example.crud_banco.R.id.nav_view)
        val toolbar: Toolbar = findViewById(com.example.crud_banco.R.id.toolbar)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            com.example.crud_banco.R.string.open_nav, com.example.crud_banco.R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(com.example.crud_banco.R.id.frame_layout, HomeFragment())
                .commit()
            navigationView.setCheckedItem(com.example.crud_banco.R.id.nav_home)
        }

        bottomNavigationView.setBackground(null)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.crud_banco.R.id.home -> replaceFragment(HomeFragment())
                com.example.crud_banco.R.id.shorts -> replaceFragment(InsertionFragment())
                com.example.crud_banco.R.id.Incricoes -> replaceFragment(HistoricoFragment())
                com.example.crud_banco.R.id.Biblioteca -> replaceFragment(HistoricoFragment())
            }
            true
        }

        fab.setOnClickListener {
            showBottomDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_settings -> {
                Toast.makeText(this, "Configurações clicadas", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.example.crud_banco.R.id.frame_layout, fragment)
        fragmentTransaction.commit()
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
}
