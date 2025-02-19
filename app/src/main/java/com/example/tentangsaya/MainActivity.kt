package com.example.tentangsaya

import android.content.Intent
import android.media.SoundPool
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi SoundPool
        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        soundId = soundPool.load(this, R.raw.klik_button, 1)

        val kalkulatorButton: Button = findViewById(R.id.kalkulator_button)
        kalkulatorButton.setOnClickListener {
            playSound()
            val intent = Intent(this, KalkulatorActivity::class.java)
            startActivity(intent)
        }

        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            playSound()
            logoutButton.postDelayed({
                finishAffinity() // Menutup semua aktivitas setelah delay
            }, 300) // Delay 300ms agar suara sempat terdengar
        }
    }

    // Fungsi playSound dipindahkan ke luar onCreate
    private fun playSound() {
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}