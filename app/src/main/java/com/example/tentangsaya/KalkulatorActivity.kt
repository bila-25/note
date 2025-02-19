package com.example.tentangsaya

import android.media.SoundPool
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class KalkulatorActivity : AppCompatActivity() {
    private lateinit var tvResult: TextView
    private val expression = StringBuilder()
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kalkulator)

        // Inisialisasi SoundPool
        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        soundId = soundPool.load(this, R.raw.klik_button, 1)

        // Initialize TextView
        tvResult = findViewById(R.id.tvResult)

        // Number buttons
        val numberButtons = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        numberButtons.forEachIndexed { index, buttonId ->
            findViewById<Button>(buttonId).setOnClickListener {
                playSound()
                onNumberClick(index.toString())
            }
        }

        // Operation buttons
        findViewById<Button>(R.id.btnPlus).setOnClickListener { playSound(); onOperationClick("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { playSound(); onOperationClick("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { playSound(); onOperationClick("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { playSound(); onOperationClick("/") }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { playSound(); onEqualsClick() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { playSound(); onClearClick() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { playSound(); onDeleteClick() }
        findViewById<Button>(R.id.btnBack).setOnClickListener { playSound(); onDeleteClick() }
        // Tombol Kembali
        findViewById<Button>(R.id.btnBack).setOnClickListener { playSound()
            finish() // Menutup KalkulatorActivity dan kembali ke MainActivity
        }

    }

    private fun playSound() {
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun onNumberClick(number: String) {
        expression.append(number)
        tvResult.text = expression.toString()
    }

    private fun onOperationClick(op: String) {
        if (expression.isNotEmpty() && !"+-*/".contains(expression.last())) {
            expression.append(op)
            tvResult.text = expression.toString()
        }
    }

    private fun onEqualsClick() {
        try {
            if (expression.isEmpty() || "+-*/".contains(expression.last())) {
                tvResult.text = "Error"
                return
            }

            val result = evaluateExpression(expression.toString()) // Evaluasi ekspresi
            val displayResult = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
            tvResult.text = displayResult
            expression.clear()
            expression.append(displayResult) // Menyimpan hasil sebagai input baru
        } catch (e: Exception) {
            tvResult.text = "Error"
            expression.clear()
        }
    }

    private fun onClearClick() {
        expression.clear()
        tvResult.text = "0"
    }

    private fun onDeleteClick() {
        if (expression.isNotEmpty()) {
            expression.deleteCharAt(expression.length - 1)
            tvResult.text = if (expression.isEmpty()) "0" else expression.toString()
        }
    }

    // Fungsi untuk mengevaluasi ekspresi matematika dalam bentuk string
    private fun evaluateExpression(expression: String): Double {
        return try {
            val sanitizedExpression = expression.replace(Regex("[^0-9+\\-*/.]"), "")
            val result = object : Any() {
                var pos = -1
                var ch = 0

                fun nextChar() {
                    ch = if (++pos < sanitizedExpression.length) sanitizedExpression[pos].code else -1
                }

                fun eat(charToEat: Int): Boolean {
                    while (ch == ' '.code) nextChar()
                    if (ch == charToEat) {
                        nextChar()
                        return true
                    }
                    return false
                }

                fun parse(): Double {
                    nextChar()
                    val x = parseExpression()
                    if (pos < sanitizedExpression.length) throw RuntimeException("Unexpected: " + ch.toChar())
                    return x
                }

                fun parseExpression(): Double {
                    var x = parseTerm()
                    while (true) {
                        when {
                            eat('+'.code) -> x += parseTerm()
                            eat('-'.code) -> x -= parseTerm()
                            else -> return x
                        }
                    }
                }

                fun parseTerm(): Double {
                    var x = parseFactor()
                    while (true) {
                        when {
                            eat('*'.code) -> x *= parseFactor()
                            eat('/'.code) -> x /= parseFactor()
                            else -> return x
                        }
                    }
                }

                fun parseFactor(): Double {
                    if (eat('+'.code)) return parseFactor()
                    if (eat('-'.code)) return -parseFactor()

                    var x: Double
                    val startPos = pos
                    if (eat('('.code)) {
                        x = parseExpression()
                        eat(')'.code)
                    } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                        while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                        x = sanitizedExpression.substring(startPos, pos).toDouble()
                    } else {
                        throw RuntimeException("Unexpected: " + ch.toChar())
                    }
                    return x
                }
            }.parse()

            result
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid expression")
        }
    }
}