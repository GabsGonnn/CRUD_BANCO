package com.example.crud_banco.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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

        replaceFragment(HomeFragment())

        bottomNavigationView.setBackground(null)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.crud_banco.R.id.home -> replaceFragment(HomeFragment())
                com.example.crud_banco.R.id.shorts -> replaceFragment(InsertionFragment())
            }
            true
        }

        fab.setOnClickListener {

         showBottomDialog()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.example.crud_banco.R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showBottomDialog() {
        val items = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")

        val mDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.insertion_dialog, null)

        mDialog.setView(mDialogView)
        val etDispNome = mDialogView.findViewById<EditText>(R.id.etDispNome)
        val etDispLocal = mDialogView.findViewById<EditText>(R.id.etDispLocal)
        val etDispDtInst = mDialogView.findViewById<EditText>(R.id.etDispDtInst)
        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val alertDialog = mDialog.create()
        alertDialog.show()

        AlertDialog.Builder(this)
            .setTitle("Escolha um item")
            .setView(spinner)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedItem = items[spinner.selectedItemPosition]
                // Faça algo com o item selecionado
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}

//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(com.example.crud_banco.R.layout.bottomsheetlayout)
//
//        val videoLayout = dialog.findViewById<LinearLayout>(com.example.crud_banco.R.id.layoutVideo)
//        val shortsLayout = dialog.findViewById<LinearLayout>(com.example.crud_banco.R.id.layoutShorts)
//        val liveLayout = dialog.findViewById<LinearLayout>(com.example.crud_banco.R.id.layoutLive)
//        val cancelButton = dialog.findViewById<ImageView>(com.example.crud_banco.R.id.cancelButton)
//
//        videoLayout.setOnClickListener {
//            dialog.dismiss()
//            Toast.makeText(this, "Upload a Video is clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        shortsLayout.setOnClickListener {
//            dialog.dismiss()
//            Toast.makeText(this, "Create a short is Clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        liveLayout.setOnClickListener {
//            dialog.dismiss()
//            Toast.makeText(this, "Go live is Clicked", Toast.LENGTH_SHORT).show()
//        }
//
//        cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.window?.attributes?.windowAnimations = com.example.crud_banco.R.style.DialogAnimation
//        dialog.window?.setGravity(Gravity.BOTTOM)
//    }

