package uz.hisobim.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.InputType

class MainActivity : Activity() {

    private val purple = Color.rgb(100, 20, 210)
    private var balance = 0L
    private var income = 0L
    private var expense = 0L

    private lateinit var balanceText: TextView
    private lateinit var incomeText: TextView
    private lateinit var expenseText: TextView
    private lateinit var historyText: TextView

    private val prefs by lazy {
        getSharedPreferences("hisobim", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        balance = prefs.getLong("balance", 0L)
        income = prefs.getLong("income", 0L)
        expense = prefs.getLong("expense", 0L)

        buildScreen()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun card(color: Int, radius: Int = 22): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun label(
        value: String,
        size: Float,
        color: Int = Color.BLACK,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.CENTER_VERTICAL
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(248, 247, 252))
        }

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // BOSH QISM
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(25), dp(28), dp(25), dp(30))
            background = card(purple, 35)
        }

        val titleRow = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        titleRow.addView(
            label("▥", 34f, Color.WHITE, true),
            LinearLayout.LayoutParams(dp(55), dp(60))
        )

        val titleColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        titleColumn.addView(label("HISOBIM", 30f, Color.WHITE, true))
        titleColumn.addView(label("Shaxsiy moliya hisobingiz", 16f, Color.WHITE))

        titleRow.addView(titleColumn, LinearLayout.LayoutParams(0, -2, 1f))
        titleRow.addView(label("♧", 34f, Color.WHITE))

        header.addView(titleRow)

        // BALANS KARTASI
        val balanceCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(20), dp(22), dp(22))
            background = card(Color.WHITE, 25)
        }

        balanceCard.addView(label("Joriy balans", 19f, Color.GRAY))

        balanceText = label("0 so‘m", 32f, purple, true)
        balanceCard.addView(balanceText)

        val subtitle = label("Barcha hisoblaringiz bo‘yicha", 15f, Color.GRAY)
        balanceCard.addView(subtitle)

        val balanceParams = LinearLayout.LayoutParams(
            -1, dp(135)
        ).apply {
            topMargin = dp(25)
        }

        header.addView(balanceCard, balanceParams)

        content.addView(header)

        // TUGMALAR
        val grid = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(18), dp(15), dp(5))
        }

        val row1 = LinearLayout(this)
        row1.addView(tile("+", "Kirim", Color.rgb(20, 170, 85)) {
            addAmount(true)
        }, weightParams())

        row1.addView(tile("−", "Chiqim", Color.rgb(235, 40, 55)) {
            addAmount(false)
        }, weightParams())

        row1.addView(tile("↔", "O‘tkazma", Color.rgb(20, 140, 220)) {
            transfer()
        }, weightParams())

        row1.addView(tile("K", "Kreditlar", purple) {
            message("Kreditlar bo‘limi")
        }, weightParams())

        val row2 = LinearLayout(this)

        row2.addView(tile("U", "Kommunal", Color.rgb(245, 160, 20)) {
            message("Kommunal to‘lovlar")
        }, weightParams())

        row2.addView(tile("H", "Hisobot", purple) {
            message("Hisobot bo‘limi")
        }, weightParams())

        row2.addView(tile("S", "Statistika", Color.rgb(245, 160, 20)) {
            message("Statistika bo‘limi")
        }, weightParams())

        row2.addView(tile("T", "Taqsimot", Color.rgb(20, 140, 220)) {
            message("Taqsimot bo‘limi")
        }, weightParams())

        grid.addView(row1)
        grid.addView(row2)
        content.addView(grid)

        // OYLIK KO‘RSATKICHLAR
        val monthTitle = LinearLayout(this).apply {
            setPadding(dp(22), dp(18), dp(22), dp(12))
        }

        monthTitle.addView(
            label("Oylik ko‘rsatkichlar", 19f, Color.BLACK, true),
            LinearLayout.LayoutParams(0, -2, 1f)
        )
        monthTitle.addView(label("Noyabr 2026", 14f, purple))

        content.addView(monthTitle)

        val stats = LinearLayout(this).apply {
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = card(Color.WHITE, 25)
        }

        val incColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        incColumn.addView(label("↑ Kirim", 19f, Color.rgb(30, 160, 90)))
        incomeText = label("0 so‘m", 20f, Color.rgb(30, 160, 90), true)
        incColumn.addView(incomeText)

        val expColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        expColumn.addView(label("↓ Chiqim", 19f, Color.rgb(210, 65, 75)))
        expenseText = label("0 so‘m", 20f, Color.rgb(210, 65, 75), true)
        expColumn.addView(expenseText)

        stats.addView(incColumn, LinearLayout.LayoutParams(0, -2, 1f))
        stats.addView(expColumn, LinearLayout.LayoutParams(0, -2, 1f))

        val statsParams = LinearLayout.LayoutParams(-1, dp(105))
        statsParams.setMargins(dp(22), dp(5), dp(22), dp(15))
        content.addView(stats, statsParams)

        content.addView(
            label("Xarajatlar bo‘yicha taqsimot", 19f, Color.BLACK, true)
        )

        historyText = label(
            "Hozircha operatsiyalar mavjud emas",
            15f,
            Color.GRAY
        )
        historyText.setPadding(dp(22), dp(20), dp(22), dp(20))
        content.addView(historyText)

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        // PASTKI MENYU
        val bottom = LinearLayout(this).apply {
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val names = listOf(
            "⌂\nAsosiy",
            "▥\nHisobot",
            "+\n+",
            "☷\nOperatsiyalar",
            "●\nProfil"
        )

        names.forEachIndexed { index, name ->
            val item = TextView(this).apply {
                text = name
                textSize = 14f
                setTextColor(purple)
                gravity = Gravity.CENTER
                setPadding(0, dp(12), 0, dp(12))
                setOnClickListener {
                    when (index) {
                        2 -> addAmount(true)
                        1 -> message("Hisobot")
                        3 -> message("Operatsiyalar tarixi")
                        4 -> message("Profil")
                    }
                }
            }
            bottom.addView(item, LinearLayout.LayoutParams(0, dp(70), 1f))
        }

        root.addView(bottom)
        setContentView(root)

        updateScreen()
    }

    private fun weightParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, dp(100), 1f).apply {
            setMargins(dp(4), dp(5), dp(4), dp(7))
        }
    }

    private fun tile(
        icon: String,
        name: String,
        color: Int,
        action: () -> Unit
    ): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = card(Color.WHITE, 20)
            elevation = dp(4).toFloat()
            setOnClickListener { action() }
        }

        val circle = TextView(this).apply {
            text = icon
            textSize = 25f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            background = card(color, 100)
        }

        box.addView(circle, LinearLayout.LayoutParams(dp(48), dp(48)))

        val title = label(name, 12f, Color.BLACK, true)
        title.gravity = Gravity.CENTER

        box.addView(title)

        return box
    }

    private fun addAmount(isIncome: Boolean) {
        val input = EditText(this).apply {
            hint = "Summani kiriting"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        AlertDialog.Builder(this)
            .setTitle(if (isIncome) "Daromad qo‘shish" else "Xarajat qo‘shish")
            .setView(input)
            .setPositiveButton("Saqlash") { _, _ ->
                val amount = input.text.toString().toLongOrNull() ?: 0L

                if (amount <= 0) {
                    message("Summani to‘g‘ri kiriting")
                    return@setPositiveButton
                }

                if (isIncome) {
                    income += amount
                    balance += amount
                } else {
                    if (amount > balance) {
                        message("Mablag‘ yetarli emas")
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

    private fun transfer() {
        message("O‘tkazma bo‘limi keyingi bosqichda qo‘shiladi")
    }

    private fun saveData() {
        prefs.edit()
            .putLong("balance", balance)
            .putLong("income", income)
            .putLong("expense", expense)
            .apply()
    }

    private fun updateScreen() {
        balanceText.text = "${balance} so‘m"
        incomeText.text = "${income} so‘m"
        expenseText.text = "${expense} so‘m"

        historyText.text =
            "Jami daromad: ${income} so‘m\nJami xarajat: ${expense} so‘m"
    }

    private fun message(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
  private fun openCredits() {
    val credits = org.json.JSONArray(
        prefs.getString("credits", "[]")
    )

    val summary = StringBuilder()

    for (i in 0 until credits.length()) {
        val item = credits.getJSONObject(i)
        val name = item.getString("name")
        val total = item.getLong("total")
        val paid = item.getLong("paid")

        summary.append(
            "$name\nKredit: $total so‘m\n" +
            "To‘langan: $paid so‘m\n" +
            "Qoldiq: ${total - paid} so‘m\n\n"
        )
    }

    AlertDialog.Builder(this)
        .setTitle("Kreditlarim")
        .setMessage(
            if (credits.length() == 0)
                "Hozircha kredit mavjud emas."
            else summary.toString()
        )
        .setPositiveButton("Kredit qo‘shish") { _, _ ->
            addCredit()
        }
        .setNegativeButton("Yopish", null)
        .show()
}

private fun addCredit() {
    val form = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(10), dp(20), dp(10))
    }

    fun field(hintText: String): EditText {
        return EditText(this).apply {
            hint = hintText
            inputType = InputType.TYPE_CLASS_TEXT
            form.addView(this)
        }
    }

    val name = field("Bank yoki kreditor nomi")
    val total = field("Kredit summasi")
    total.inputType = InputType.TYPE_CLASS_NUMBER

    val rate = field("Yillik foiz stavkasi (%)")
    rate.inputType = InputType.TYPE_CLASS_NUMBER or
        InputType.TYPE_NUMBER_FLAG_DECIMAL

    val monthly = field("Oylik to‘lov")
    monthly.inputType = InputType.TYPE_CLASS_NUMBER

    val paid = field("To‘langan summa")
    paid.inputType = InputType.TYPE_CLASS_NUMBER

    AlertDialog.Builder(this)
        .setTitle("Yangi kredit")
        .setView(form)
        .setPositiveButton("Saqlash") { _, _ ->
            val amount = total.text.toString().toLongOrNull() ?: 0L
            val paidAmount = paid.text.toString().toLongOrNull() ?: 0L

            if (name.text.isBlank() ||
                amount <= 0 ||
                paidAmount < 0 ||
                paidAmount > amount
            ) {
                message("Ma’lumotlarni to‘g‘ri kiriting")
                return@setPositiveButton
            }

            val credits = org.json.JSONArray(
                prefs.getString("credits", "[]")
            )

            val item = org.json.JSONObject().apply {
                put("name", name.text.toString())
                put("total", amount)
                put("rate", rate.text.toString())
                put("monthly", monthly.text.toString())
                put("paid", paidAmount)
            }

            credits.put(item)

            prefs.edit()
                .putString("credits", credits.toString())
                .apply()

            message("Kredit saqlandi!")
            openCredits()
        }
        .setNegativeButton("Bekor qilish", null)
        .show()
}  }
}
