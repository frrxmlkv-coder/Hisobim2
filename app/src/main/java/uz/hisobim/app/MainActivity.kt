
package uz.hisobim.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.text.InputType

class MainActivity : Activity() {

    private var balance = 800000L
    private var income = 0L
    private var expense = 0L
    private lateinit var balanceText: TextView
    private lateinit var historyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("hisobim", MODE_PRIVATE)
        balance = prefs.getLong("balance", 800000L)
        income = prefs.getLong("income", 0L)
        expense = prefs.getLong("expense", 0L)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 35, 24, 24)
        layout.setBackgroundColor(Color.rgb(245, 247, 250))

        fun text(value: String, size: Float): TextView {
            return TextView(this).apply {
                text = value
                textSize = size
                setTextColor(Color.rgb(25, 35, 50))
                gravity = Gravity.CENTER
                setPadding(8, 16, 8, 16)
            }
        }

        layout.addView(text("HISOBIM", 30f))

        balanceText = text("", 25f)
        balanceText.setBackgroundColor(Color.WHITE)
        layout.addView(balanceText)

        val incomeButton = Button(this)
        incomeButton.text = "➕ DAROMAD QO‘SHISH"
        layout.addView(incomeButton)

        val expenseButton = Button(this)
        expenseButton.text = "➖ XARAJAT QO‘SHISH"
        layout.addView(expenseButton)

        layout.addView(text("Operatsiyalar tarixi", 20f))

        historyText = text("", 16f)
        layout.addView(historyText)

        incomeButton.setOnClickListener {
            addAmount(true)
        }

        expenseButton.setOnClickListener {
            addAmount(false)
        }

        setContentView(layout)
        updateScreen()
    }

    private fun addAmount(isIncome: Boolean) {
        val input = EditText(this)
        input.hint = "Summani kiriting"
        input.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle(if (isIncome) "Daromad" else "Xarajat")
            .setView(input)
            .setPositiveButton("Saqlash") { _, _ ->
                val amount = input.text.toString().toLongOrNull() ?: 0L

                if (amount <= 0) return@setPositiveButton

                if (isIncome) {
                    income += amount
                    balance += amount
                } else {
                    if (amount > balance) {
                        Toast.makeText(
                            this, "Mablag‘ yetarli emas!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }
                    expense += amount
                    balance -= amount
                }

                saveData()
                updateScreen()
            }
            .setNegativeButton("Bekor qilish", null)
            .show()
    }

    private fun saveData() {
        getSharedPreferences("hisobim", MODE_PRIVATE)
            .edit()
            .putLong("balance", balance)
            .putLong("income", income)
            .putLong("expense", expense)
            .apply()
    }

    private fun updateScreen() {
        balanceText.text =
            "BALANS\n${balance} so‘m\n\nDaromad: $income so‘m\nXarajat: $expense so‘m"

        historyText.text =
            "Jami daromad: $income so‘m\nJami xarajat: $expense so‘m"
    }
}
