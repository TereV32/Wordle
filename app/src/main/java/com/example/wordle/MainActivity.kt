package com.example.wordle

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.jinatonic.confetti.CommonConfetti


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val submitButton = findViewById<Button>(R.id.submitButton)
        val enterText = findViewById<EditText>(R.id.editText)
        val guessOne = findViewById<TextView>(R.id.guessOneText)
        val guessTwo = findViewById<TextView>(R.id.guessTwoText)
        val guessThree = findViewById<TextView>(R.id.guessThreeText)
        val correctWord = findViewById<TextView>(R.id.answerText)

        var parentView = findViewById<ConstraintLayout>(R.id.parentView)
        val confettiColors = intArrayOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.MAGENTA,
            Color.CYAN,
        )

        var guessNumber = 1
        var wordToGuess = FourLetterWordList.getRandomFourLetterWord()
        Log.v("Word to guess", wordToGuess)


        // Function to check if input is valid
        fun isValidInput(input: String): Boolean {
            // Check if input has exactly 4 letters and contains no spaces or numbers
            return input.length == 4 && input.all { it.isLetter() }
        }

        // Function to check guess
        fun checkGuess(guess: String): SpannableStringBuilder {
            val result = SpannableStringBuilder()

            // Assign background color to letter depending on correctness
            for (i in 0 until 4) {
                val charToAppend = guess[i]
                val color: Int = when (charToAppend) {
                    wordToGuess[i] -> R.color.green
                    in wordToGuess -> R.color.yellow
                    else -> R.color.grey
                }
                val spannableText = SpannableStringBuilder(charToAppend.toString())
                spannableText.setSpan(
                    BackgroundColorSpan(ContextCompat.getColor(applicationContext, color)),
                    0,
                    1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                result.append(spannableText)

                // Add space between letters
                if (i < 3) {
                    val space = SpannableString(" ")
                    result.append(space)
                }
            }
            return result
        }

        // Function to reset game
        fun resetGame() {
            guessNumber = 0
            guessOne.text = ""
            guessTwo.text = ""
            guessThree.text = ""
            correctWord.text = ""
            submitButton.text = "Guess"
            wordToGuess = FourLetterWordList.getRandomFourLetterWord()
            Log.v("Word to guess", wordToGuess)
        }

        submitButton.setOnClickListener {

            // Hide the keyboard
            keyboard.hideSoftInputFromWindow(submitButton.windowToken, 0)

            // Reset the game when the "Play Again" button is pressed
            if (submitButton.text == "Play Again") {
                resetGame()
            }

            var guessValue = enterText.text.toString().uppercase()
            // Check if the input is valid
            if (!isValidInput(guessValue)) {
                // Show an error toast  if input is invalid
                Toast.makeText(
                    this,
                    "Invalid input. Please enter 4 letters with no spaces or numbers.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            when (guessNumber) {
                1 -> {
                    guessOne.text = checkGuess(guessValue)
                }

                2 -> {
                    guessTwo.text = checkGuess(guessValue)
                }

                3 -> {
                    guessThree.text = checkGuess(guessValue)
                    correctWord.text = wordToGuess

                    if (guessValue != wordToGuess) {
                        Toast.makeText(this, "You Lost!", Toast.LENGTH_LONG).show()
                        submitButton.text = "Play Again"
                    }

                }
            }

            if (guessValue == wordToGuess) {
                Toast.makeText(this, "You Won!", Toast.LENGTH_LONG).show()
                submitButton.text = "Play Again"
                CommonConfetti.rainingConfetti(parentView, confettiColors)
                    .oneShot()
            }
            guessNumber++
        }
    }


}