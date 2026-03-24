package com.example.grocerylist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerylist.databinding.ActivityMainBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: GroceryAdapter

    private val translationCache = mutableMapOf<Int, String>()
    private var showingTranslation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listId = intent.getIntExtra(EXTRA_LIST_ID, -1)
        val listName = intent.getStringExtra(EXTRA_LIST_NAME) ?: getString(R.string.app_name)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = listName
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel.init(listId)

        adapter = GroceryAdapter(
            onToggle = viewModel::toggleBought,
            onDelete = viewModel::deleteItem
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.allItems.observe(this) { items ->
            if (showingTranslation) {
                adapter.submitList(items.map { item ->
                    translationCache[item.id]?.let { item.copy(name = it) } ?: item
                })
            } else {
                adapter.submitList(items)
            }
        }

        binding.buttonAdd.setOnClickListener { addItem() }
        binding.editTextItem.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { addItem(); true } else false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.action_theme -> { toggleTheme(); true }
            R.id.action_translate -> { toggleTranslation(); true }
            R.id.action_share -> { shareList(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addItem() {
        val name = binding.editTextItem.text.toString().trim()
        if (name.isNotEmpty()) {
            viewModel.addItem(name)
            binding.editTextItem.text?.clear()
        }
    }

    private fun toggleTheme() {
        val newMode = if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        getSharedPreferences("prefs", MODE_PRIVATE).edit().putInt("night_mode", newMode).apply()
        AppCompatDelegate.setDefaultNightMode(newMode)
    }

    private fun toggleTranslation() {
        if (showingTranslation) {
            showingTranslation = false
            viewModel.allItems.value?.let { adapter.submitList(it) }
            return
        }
        val items = viewModel.allItems.value ?: return
        if (items.isEmpty()) return

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ITALIAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val translator = Translation.getClient(options)

        Toast.makeText(this, R.string.translating, Toast.LENGTH_SHORT).show()

        translator.downloadModelIfNeeded(DownloadConditions.Builder().build())
            .addOnSuccessListener {
                val pending = items.filter { !translationCache.containsKey(it.id) }
                if (pending.isEmpty()) {
                    applyTranslations(items)
                    translator.close()
                    return@addOnSuccessListener
                }
                var remaining = pending.size
                pending.forEach { item ->
                    translator.translate(item.name)
                        .addOnSuccessListener { result ->
                            translationCache[item.id] = result
                            if (--remaining == 0) {
                                applyTranslations(viewModel.allItems.value ?: items)
                                translator.close()
                            }
                        }
                        .addOnFailureListener {
                            if (--remaining == 0) {
                                applyTranslations(viewModel.allItems.value ?: items)
                                translator.close()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.translation_failed, Toast.LENGTH_SHORT).show()
                translator.close()
            }
    }

    private fun applyTranslations(items: List<GroceryItem>) {
        showingTranslation = true
        adapter.submitList(items.map { item ->
            translationCache[item.id]?.let { item.copy(name = it) } ?: item
        })
    }

    private fun shareList() {
        val items = viewModel.allItems.value ?: return
        if (items.isEmpty()) {
            Toast.makeText(this, R.string.list_empty, Toast.LENGTH_SHORT).show()
            return
        }
        val title = supportActionBar?.title ?: getString(R.string.app_name)
        val text = buildString {
            appendLine("📋 $title")
            appendLine()
            items.forEach { item ->
                appendLine("${if (item.isBought) "✅" else "⬜"} ${item.name}")
            }
        }
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.whatsapp")
        }
        try {
            startActivity(whatsappIntent)
        } catch (e: Exception) {
            startActivity(Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                },
                getString(R.string.share_via)
            ))
        }
    }

    companion object {
        const val EXTRA_LIST_ID = "extra_list_id"
        const val EXTRA_LIST_NAME = "extra_list_name"
    }
}
