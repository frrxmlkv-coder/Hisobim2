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
            openCredits()
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
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()}

private fun openCredits() {
    val credits = org.json.JSONArray(
        prefs.getString("credits", "[]")
    )

    val scroll = ScrollView(this)

    val root = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(18), dp(18), dp(18))
        setBackgroundColor(Color.rgb(247, 246, 252))
    }

    val purple = Color.rgb(100, 20, 210)
    val green = Color.rgb(20, 165, 100)
    val red = Color.rgb(220, 55, 75)

    fun makeText(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
            setPadding(dp(4), dp(5), dp(4), dp(5))
        }
    }

    fun makeCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(13), dp(15), dp(13))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(20).toFloat()
            }
            elevation = dp(3).toFloat()
        }
    }

    fun makeButton(
        title: String,
        color: Int,
        action: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = title
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(10), dp(13), dp(10), dp(13))
            background = GradientDrawable().apply {
                setColor(color)
                cornerRadius = dp(12).toFloat()
            }
            setOnClickListener { action() }
        }
    }

    var total = 0.0
    var paid = 0.0
    var monthly = 0.0

    for (i in 0 until credits.length()) {
        val item = credits.getJSONObject(i)
        total += item.optDouble("total")
        paid += item.optDouble("paid")
        monthly += item.optDouble("monthly")
    }

    val remaining = (total - paid).coerceAtLeast(0.0)

    root.addView(makeText("KREDITLARIM", 25f, purple, true))
    root.addView(makeText("Shaxsiy kredit boshqaruvi", 14f, Color.GRAY))

    val summary = makeCard()
    summary.setBackgroundColor(purple)

    summary.addView(makeText("UMUMIY QARZ QOLDIG‘I", 14f, Color.WHITE, true))
    summary.addView(makeText(
        "%,.0f so‘m".format(remaining),
        27f, Color.WHITE, true
    ))
    summary.addView(makeText(
        "Jami kredit: %,.0f so‘m".format(total),
        14f, Color.WHITE
    ))
    summary.addView(makeText(
        "To‘langan: %,.0f so‘m".format(paid),
        14f, Color.WHITE
    ))

    root.addView(summary)

    val stats = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(0, dp(12), 0, dp(12))
    }

    val c1 = makeCard()
    c1.addView(makeText("OYLIK TO‘LOV", 12f, Color.GRAY, true))
    c1.addView(makeText(
        "%,.0f so‘m".format(monthly), 17f, purple, true
    ))

    val c2 = makeCard()
    c2.addView(makeText("KREDITLAR", 12f, Color.GRAY, true))
    c2.addView(makeText("${credits.length()} ta", 17f, purple, true))

    stats.addView(c1, LinearLayout.LayoutParams(0, -2, 1f))
    stats.addView(c2, LinearLayout.LayoutParams(0, -2, 1f)
        .apply { leftMargin = dp(8) })

    root.addView(stats)
    root.addView(makeText("Mening kreditlarim", 20f, Color.BLACK, true))

    if (credits.length() == 0) {
        root.addView(makeText(
            "Hozircha kredit mavjud emas.",
            15f, Color.GRAY
        ))
    }

    for (i in 0 until credits.length()) {
        val item = credits.getJSONObject(i)

        val name = item.optString("name")
        val amount = item.optDouble("total")
        val paidAmount = item.optDouble("paid")
        val rate = item.optString("rate")
        val monthlyAmount = item.optDouble("monthly")

        val left = (amount - paidAmount).coerceAtLeast(0.0)

        val progress = if (amount > 0)
            ((paidAmount / amount) * 100).toInt().coerceIn(0, 100)
        else 0

        val box = makeCard()

        box.addView(makeText(name, 19f, purple, true))
        box.addView(makeText(
            "Kredit summasi: %,.0f so‘m".format(amount),
            14f, Color.DKGRAY
        ))
        box.addView(makeText(
            "Qolgan qarz: %,.0f so‘m".format(left),
            18f, red, true
        ))
        box.addView(makeText(
            "To‘langan: %,.0f so‘m".format(paidAmount),
            14f, green
        ))
        box.addView(makeText("Yillik foiz: $rate %", 14f, Color.GRAY))
        box.addView(makeText(
            "Oylik to‘lov: %,.0f so‘m".format(monthlyAmount),
            14f, Color.DKGRAY
        ))

        box.addView(makeText(
            "To‘lov bajarilishi: $progress %",
            13f, purple, true
        ))

        val bar = ProgressBar(
            this, null, android.R.attr.progressBarStyleHorizontal
        ).apply {
            max = 100
            this.progress = progress
            progressTintList =
                android.content.res.ColorStateList.valueOf(green)
        }

        box.addView(bar, LinearLayout.LayoutParams(-1, dp(9)))

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        actions.addView(makeButton("TO‘LOV QO‘SHISH", green) {
            addCreditPayment(i)
        }, LinearLayout.LayoutParams(0, -2, 1f))

        actions.addView(makeButton("O‘CHIRISH", red) {
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Kreditni o‘chirish")
                .setMessage("Kreditni o‘chirmoqchimisiz?")
                .setNegativeButton("YO‘Q", null)
                .setPositiveButton("HA") { _, _ ->
                    val updated = org.json.JSONArray(
                        prefs.getString("credits", "[]")
                    )
                    val result = org.json.JSONArray()

                    for (j in 0 until updated.length()) {
                        if (j != i) {
                            result.put(updated.getJSONObject(j))
                        }
                    }

                    prefs.edit()
                        .putString("credits", result.toString())
                        .apply()

                    openCredits()
                }
                .show()
        }, LinearLayout.LayoutParams(0, -2, 1f)
            .apply { leftMargin = dp(8) })

        box.addView(actions)

        root.addView(box, LinearLayout.LayoutParams(-1, -2)
            .apply { topMargin = dp(12) })
    }

    root.addView(makeButton("+ YANGI KREDIT QO‘SHISH", purple) {
        addCredit()
    }, LinearLayout.LayoutParams(-1, -2)
        .apply { topMargin = dp(18) })

    scroll.addView(root)

    val dialog = AlertDialog.Builder(this)
        .setView(scroll)
        .setNegativeButton("YOPISH", null)
        .create()

    dialog.show()
    dialog.window?.setLayout(
        -1,
        (resources.displayMetrics.heightPixels * 0.88).toInt()
    )
}


private fun addCredit() {

    val form = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(10), dp(20), dp(15))
    }

    fun field(
        hintText: String,
        type: Int = android.text.InputType.TYPE_CLASS_TEXT
    ): EditText {
        return EditText(this).apply {
            hint = hintText
            textSize = 15f
            inputType = type
            setSingleLine(true)
            form.addView(this)
        }
    }

    val numberType =
        android.text.InputType.TYPE_CLASS_NUMBER or
        android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

    val bank = field("Bank yoki kreditor nomi")
    val creditType = field("Kredit turi: iste'mol, avto, ipoteka")
    val amount = field("Kredit summasi (so'm)", numberType)
    val rate = field("Yillik foiz stavkasi (%)", numberType)
    val months = field("Kredit muddati (oy)", 
        android.text.InputType.TYPE_CLASS_NUMBER)
    val monthly = field("Oylik to'lov (so'm)", numberType)
    val startDate = field("Boshlanish sanasi (YYYY-MM-DD)")
    val paymentDay = field("Har oyning to'lov kuni (1-31)",
        android.text.InputType.TYPE_CLASS_NUMBER)
    val paid = field("Hozirgacha to'langan summa", numberType)
    val purpose = field("Kredit maqsadi")

    val scroll = ScrollView(this).apply {
        isFillViewport = true
        addView(form)
    }

    val dialog = AlertDialog.Builder(this)
        .setTitle("YANGI KREDIT")
        .setView(scroll)
        .setNegativeButton("BEKOR QILISH", null)
        .setPositiveButton("SAQLASH", null)
        .create()

    dialog.setOnShowListener {

        dialog.window?.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.92).toInt(),
            (resources.displayMetrics.heightPixels * 0.78).toInt()
        )

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {

                val total = amount.text.toString()
                    .toDoubleOrNull() ?: 0.0

                val interest = rate.text.toString()
                    .toDoubleOrNull() ?: 0.0

                val term = months.text.toString()
                    .toIntOrNull() ?: 0

                val monthlyAmount = monthly.text.toString()
                    .toDoubleOrNull() ?: 0.0

                val paidAmount = paid.text.toString()
                    .toDoubleOrNull() ?: 0.0

                val day = paymentDay.text.toString()
                    .toIntOrNull() ?: 0

                if (bank.text.isBlank() ||
                    total <= 0 ||
                    interest < 0 ||
                    term <= 0 ||
                    monthlyAmount <= 0 ||
                    paidAmount < 0 ||
                    paidAmount > total ||
                    day !in 1..31 ||
                    startDate.text.isBlank()
                ) {
                    message("Ma'lumotlarni to'g'ri to'ldiring")
                    return@setOnClickListener
                }

                val credits = org.json.JSONArray(
                    prefs.getString("credits", "[]")
                )

                val item = org.json.JSONObject().apply {
                    put("name", bank.text.toString())
                    put("type", creditType.text.toString())
                    put("total", total)
                    put("rate", interest.toString())
                    put("months", term)
                    put("monthly", monthlyAmount)
                    put("startDate", startDate.text.toString())
                    put("paymentDay", day)
                    put("paid", paidAmount)
                    put("purpose", purpose.text.toString())
                }

                credits.put(item)

                prefs.edit()
                    .putString("credits", credits.toString())
                    .apply()

                dialog.dismiss()

                message("Yangi kredit muvaffaqiyatli saqlandi!")
                openCredits()
            }
    }

    dialog.show()
}

private fun addCreditPayment(index: Int) {
    val credits = org.json.JSONArray(
        prefs.getString("credits", "[]")
    )

    if (index !in 0 until credits.length()) return

    val item = credits.getJSONObject(index)
    val total = item.optDouble("total")
    val paid = item.optDouble("paid")
    val remaining = (total - paid).coerceAtLeast(0.0)

    if (remaining <= 0) {
        message("Kredit to‘liq to‘langan!")
        return
    }

    val field = EditText(this).apply {
        hint = "To‘lov summasi"
        inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    AlertDialog.Builder(this)
        .setTitle("Kredit to‘lovi")
        .setMessage("Qolgan qarz: %,.0f so‘m".format(remaining))
        .setView(field)
        .setNegativeButton("BEKOR QILISH", null)
        .setPositiveButton("TO‘LASH") { _, _ ->
            val amount = field.text.toString()
                .toDoubleOrNull() ?: 0.0

            if (amount <= 0 || amount > remaining) {
                message("To‘lov summasini tekshiring")
                return@setPositiveButton
            }

            item.put("paid", paid + amount)

            prefs.edit()
                .putString("credits", credits.toString())
                .apply()

            message("To‘lov saqlandi!")
            openCredits()
        }
        .show()
}

 }
