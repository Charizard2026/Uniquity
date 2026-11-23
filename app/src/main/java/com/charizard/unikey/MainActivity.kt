package com.charizard.unikey

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24, 32, 24, 24) }
        root.addView(TextView(this).apply { text = "UniKey"; textSize = 30f })
        root.addView(TextView(this).apply { text = "Teclado Unicode sin ideogramas Han (chino y kanji)."; textSize = 16f; setPadding(0, 12, 0, 24) })
        root.addView(Button(this).apply { text = "1. Activar UniKey"; setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) } })
        root.addView(Button(this).apply { text = "2. Elegir UniKey"; setOnClickListener { (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() } })
        root.addView(TextView(this).apply { text = "La búsqueda usa los nombres oficiales de Unicode. La visualización depende de las fuentes instaladas en Android."; setPadding(0, 24, 0, 0) })
        setContentView(root)
    }
}
