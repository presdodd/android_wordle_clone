package com.example.empyapplication

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Random
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    data class CharNumberTuple(val charValue: Char, val intValue: Int)

    val tag = "main"
    val rows = mutableListOf<LetterRow>()

    private lateinit var loadingView: View
    private lateinit var mainView: View

    private val wordList = mutableListOf<String>()
    private var selectedWord = ""

    // Lists for autoplay
    private val badLetters = mutableSetOf<Char>()
    private val yellowLetters = mutableSetOf<Char>()
    private val greenLetters = mutableSetOf<CharNumberTuple>()


    class LetterRow : LinearLayoutCompat {
        val letters = mutableListOf<LetterEditor>()

        constructor(context: Context) : super(context) {
            this.layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
            )
            this.orientation = LinearLayoutCompat.HORIZONTAL

            var letterWidgets = mutableListOf<EditText>()

            val rowIndex = 0
            for (i in 0..4) {
                var letterEditor = LetterEditor(context, i, letters)
                letters.add(letterEditor)
                this.addView(letterEditor)
            }

        }
    }

    class LetterEditor : androidx.appcompat.widget.AppCompatEditText {
        private val rows: MutableList<LetterEditor>
        val tag = "main"
        private val index: Int

        constructor(
            context: Context,
            index: Int,
            rows: MutableList<LetterEditor>
        ) : super(context) {
            this.index = index
            this.rows = rows

            setBackgroundColor(Color.GRAY)
            var layout = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                200
            )
            layout.setMargins(10, 10, 10, 10)
            this.gravity = Gravity.CENTER

            layout.weight = 1.0f
            this.layoutParams = layout
            this.inputType =
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            val filter = InputFilter { source, start, end, dest, dstart, dend ->
                val pattern = Pattern.compile("[a-zA-Z]+")
                if (!pattern.matcher(source).matches()) {
                    ""
                } else {
                    null // Accept alphabetic characters
                }
                // Accept alphabetic characters

            }
            this.filters = arrayOf(
                filter,
                InputFilter.LengthFilter(1),
                InputFilter.AllCaps(),
            )

            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This method is called before the text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called when the text changes
                    val inputText = s.toString()
                    // You can do something with the inputText here
                    Log.d(tag, "text changed")

                    val i = this@LetterEditor.index;

                    if (inputText.isEmpty()) {
                        if (i > 0) {
                            val prev = this@LetterEditor.rows[i - 1]
                            prev.requestFocus();
                        }
                    } else if (this@LetterEditor.index + 1 <= 4) {
                        val next = this@LetterEditor.rows[i + 1]
                        next.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // This method is called after the text changes

                }
            })
        }

    }

    fun createRow(): LetterRow {
        val row = LetterRow(this)
        rows.add(row)

        return row
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainView = findViewById<LinearLayout>(R.id.main_container)
        mainView.addView(createRow())

        // Display the loading screen
        val loadingScreen = findViewById<LinearLayout>(R.id.loading_screen)
        loadingScreen.visibility = View.VISIBLE

        // Load the words in the background thread
        val loader = Thread(Runnable {
            loadWords()
            runOnUiThread {
                // Hide the loading screen after 3 seconds or when loading is done (whichever comes first)
                Handler(Looper.getMainLooper()).postDelayed({
                    loadingScreen.visibility = View.GONE
                }, 3000)
            }
        })
        loader.start()
    }

    private fun loadWords() {
        try {
            val inputStream = assets.open("words_5letters.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            //val wordList = mutableListOf<String>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                wordList.add(line!!)
            }
            reader.close()

            // Select a random word from the list
            val random = Random()
            selectedWord = wordList[random.nextInt(wordList.size)]
        } catch (e: IOException) {
            Log.e(tag, "Error reading words file", e)
        }
    }

    fun b0_click(v: View?) {
        val mainView = findViewById<LinearLayout>(R.id.main_container)
        val newRow = createRow()
        mainView.addView(newRow)

        Log.d(tag, selectedWord)
        Log.d(tag, "BRUHHHH")

        for (i in 0 until mainView.childCount - 1) {
            val row = mainView.getChildAt(i)
            if (row is LetterRow) {
                // adjust color of boxes of last row to match game rules
                if (i == mainView.childCount - 2) {
                    colorLetters(row)
                }
                for (editor in row.letters) {
                    editor.isEnabled = false
                }
            }
        }
    }

    fun colorLetters(row: LetterRow) {
        var place = 0
        for (editor in row.letters) {
            val editorString = editor.text.toString().lowercase()
            if (editorString != "" && editorString in selectedWord) {
                if (editorString[0] == selectedWord[place]) {
                    editor.setBackgroundColor(Color.GREEN)
                    // For Autoplay
                    if (editorString[0] in yellowLetters){
                        yellowLetters.remove(editorString[0])
                    }
                    greenLetters.add(CharNumberTuple(editorString[0], place))
                } else {
                    editor.setBackgroundColor(Color.YELLOW)

                    // if the letter is in green don't add it to yellow
                    var addYellow = true
                    for (tuple in greenLetters){
                        if(tuple.charValue == editorString[0]) {
                            addYellow = false
                        }
                    }
                    if (addYellow) {
                        yellowLetters.add(editorString[0])
                    }
                }
            } else if (editorString != ""){
                badLetters.add(editorString[0])
            }
            editor.isEnabled = false
            editor.setTextColor(Color.BLACK)
            place += 1
        }
    }


    fun autoplay(v: View?) {
        // Make a lil algorithm that removes words in the wordlist that
        // don't match what we've found so far
        if (wordList.size == 1){
            return
        }

        eraseWords()

        var guessWord = ""
        val random = Random()
        val randomInt = random.nextInt(wordList.size)
        guessWord = wordList[randomInt]

        val mainView = findViewById<LinearLayout>(R.id.main_container)

        val row = mainView.getChildAt(mainView.childCount-1)
        if (row is LetterRow) {
            for ((place, editor) in row.letters.withIndex()) {
                editor.setText(guessWord[place].toString())
            }
        }

        val button = findViewById<Button>(R.id.b0)
        button.performClick()

        return autoplay(v)
    }

    private fun eraseWords() {
        for (word in wordList) {
            for (tuple in greenLetters){
                if (word[tuple.intValue] != tuple.charValue) {
                    wordList.remove(word)
                    return eraseWords()
                }
            }

            for (char in badLetters) {
                if (char in word) {
                    wordList.remove(word)
                    return eraseWords()
                }
            }

            for (char in yellowLetters) {
                if (char !in word) {
                    wordList.remove(word)
                    return eraseWords()
                }
            }
        }
    }
}
