package com.charizard.unikey

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.view.View
import android.widget.*
import android.text.Editable
import android.text.TextWatcher

data class UChar(val value: String, val name: String, val category: String, val cp: Int)

class UniKeyIme : InputMethodService() {
    private var chars: List<UChar> = emptyList()
    private lateinit var grid: GridLayout
    private lateinit var search: EditText
    private var selected = "All"
    private val favorites = linkedSetOf<Int>()

    override fun onCreate() { super.onCreate(); loadCharacters() }

    private fun loadCharacters() {
        try {
            chars = assets.open("unicode.tsv").bufferedReader().useLines { lines ->
                lines.mapNotNull { line ->
                    val p = line.split('\t')
                    if (p.size < 3) null else {
                        val cp = p[0].toIntOrNull(16) ?: return@mapNotNull null
                        UChar(String(Character.toChars(cp)), p[1], p[2], cp)
                    }
                }.toList()
            }
        } catch (_: Exception) { chars = emptyList() }
    }

    override fun onCreateInputView(): View {
        val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(6, 6, 6, 6); setBackgroundColor(Color.rgb(25,25,29)) }
        search = EditText(this).apply { hint = "Buscar nombre o código (ej. heart, 2192)"; textSize = 15f; singleLine = true; setTextColor(Color.WHITE); setHintTextColor(Color.LTGRAY); setBackgroundColor(Color.rgb(48,48,54)) }
        panel.addView(search, LinearLayout.LayoutParams(-1, 48))
        val categories = listOf("All", "Symbols", "Math", "Arrows", "Emoji", "Letters", "Favorites")
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        categories.forEach { name -> row.addView(Button(this).apply { text = name; textSize = 9f; isAllCaps = false; setOnClickListener { selected = name; render() } }, LinearLayout.LayoutParams(0, 42, 1f)) }
        panel.addView(row)
        grid = GridLayout(this).apply { columnCount = 9; useDefaultMargins = true }
        panel.addView(ScrollView(this).apply { addView(grid) }, LinearLayout.LayoutParams(-1, 0, 1f))
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { render() }
            override fun afterTextChanged(s: Editable?) {}
        })
        render(); return panel
    }

    private fun render() {
        if (!::grid.isInitialized) return
        grid.removeAllViews()
        val q = if (::search.isInitialized) search.text.toString().trim().lowercase() else ""
        val filtered = chars.asSequence().filter { c ->
            val catOk = when (selected) {
                "Symbols" -> c.category.startsWith("S") || c.category.startsWith("P")
                "Math" -> c.name.contains("MATH") || c.name.contains("PLUS") || c.name.contains("MINUS") || c.name.contains("EQUAL")
                "Arrows" -> c.name.contains("ARROW")
                "Emoji" -> c.name.contains("FACE") || c.name.contains("EMOJI") || c.name.contains("HEART") || c.name.contains("CAT") || c.name.contains("HAND")
                "Letters" -> c.category.startsWith("L")
                "Favorites" -> c.cp in favorites
                else -> true
            }
            catOk && (q.isEmpty() || c.name.lowercase().contains(q) || c.cp.toString(16).contains(q.removePrefix("u+")))
        }.take(180).toList()
        filtered.forEach { c ->
            val b = TextView(this).apply {
                text = c.value; textSize = 25f; gravity = android.view.Gravity.CENTER
                setTextColor(Color.WHITE); setBackgroundColor(Color.rgb(54,54,62))
                contentDescription = "${c.name} U+${c.cp.toString(16).uppercase()}"
                setOnClickListener { currentInputConnection?.commitText(c.value, 1) }
                setOnLongClickListener { favorites.add(c.cp); Toast.makeText(this@UniKeyIme, "Añadido a favoritos", Toast.LENGTH_SHORT).show(); true }
            }
            grid.addView(b, GridLayout.LayoutParams().apply { width = 0; height = 46; columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) })
        }
    }
}
