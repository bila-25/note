package com.example.tentangsaya

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tentangsaya.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)
        adapter = NotesAdapter(emptyList(), db, this) // Kirim context ke adapter

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Menutup activity saat ini agar tidak menumpuk di back stack
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes() // Saat kembali dari halaman lain, daftar catatan diperbarui
    }

    private fun loadNotes() {
        val notes = db.getAllNotes()
        adapter.updateNotes(notes) // Hanya memperbarui catatan, bukan informasi diri
    }
}