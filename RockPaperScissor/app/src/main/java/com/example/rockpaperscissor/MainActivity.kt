package com.example.rockpaperscissor

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rockpaperscissor.databinding.ActivityMainBinding
import kotlin.random.Random




class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vib: Vibrator

    private var playerScore = 0
    private var cpuScore = 0

    private var choices = listOf("Taş", "Kağıt", "Makas")

    private lateinit var cpuChoice: String
    private lateinit var playerChoice: String

    private lateinit var sharedPrefences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        vib = getSystemService(VIBRATOR_SERVICE) as Vibrator

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPrefences = this.getSharedPreferences("com.example.rockpaperscissor", Context.MODE_PRIVATE)
        getSavedScores()
        printScores()
    }

    override fun onPause() {
        super.onPause()
        saveScores(playerScore, cpuScore)
    }

    private fun getSavedScores() {
        playerScore = sharedPrefences.getInt("playerScore", 0);
        cpuScore = sharedPrefences.getInt("cpuScore", 0);
    }

    private fun saveScores(playerScore: Int, cpuScore: Int) {
        val editSP = sharedPrefences.edit()
        editSP.putInt("playerScore", playerScore)
        editSP.putInt("cpuScore", cpuScore)
        editSP.apply()
    }

    fun resetScores(view: View) {
        saveScores(0, 0)
        playerScore = 0
        cpuScore = 0
        printScores()
    }

    private fun printScores() {
        val cpuScoreTextView = binding.cpuText
        val playerScoreTextView = binding.playerText

        cpuScoreTextView.text = "Bilgisayar : $cpuScore"
        playerScoreTextView.text = "Oyuncu : $playerScore"
    }

    fun onRock(view: View) {
        playerChoice = "Taş"
        startRaunt()
    }

    fun onPaper(view: View) {
        playerChoice = "Kağıt"
        startRaunt()
    }

    fun onScissors(view: View) {
        playerChoice = "Makas"
        startRaunt()
    }

    private fun startRaunt() {
        reChooseCPU()

        val playerHand = binding.playerHand
        val cpuHand = binding.cpuHand

        val cpuHead = binding.cpuHead
        val playerHead = binding.playerHead

        setHandImage(playerHand, "Taş")
        setHandImage(cpuHand, "Taş")

        setHeadImage(playerHead, "normal")
        setHeadImage(cpuHead, "normal")

        handJumpAnim(playerHand, true)
        handJumpAnim(cpuHand, false)
    }

    private fun afterHandJumping() {
        val playerHand = binding.playerHand
        val cpuHand = binding.cpuHand

        setHandImage(playerHand, playerChoice)
        setHandImage(cpuHand, cpuChoice)

        compareChoices()
        printScores()
    }

    private fun compareChoices() {
        when (playerChoice) {
            "Taş" -> when (cpuChoice) {
                "Taş" -> draw()
                "Kağıt" -> cpuWin()
                "Makas" ->  playerWin()
            }

            "Kağıt" -> when (cpuChoice) {
                "Taş" -> playerWin()
                "Kağıt" -> draw()
                "Makas" -> cpuWin()
            }

            "Makas" -> when (cpuChoice) {
                "Taş" -> cpuWin()
                "Kağıt" -> playerWin()
                "Makas" -> draw()
            }
        }
    }

    private fun reChooseCPU() {
        cpuChoice = choices[Random.nextInt(choices.size)]
    }

    private fun setHandImage(view: ImageView, choice: String) {
        when (choice) {
            "Taş" -> view.setImageResource(R.drawable.rock)
            "Kağıt" -> view.setImageResource(R.drawable.paper)
            "Makas" -> view.setImageResource(R.drawable.scissors)
        }
    }

    private fun setHeadImage(view: ImageView, choice: String) {
        when (choice) {
            "smile" -> view.setImageResource(R.drawable.headsmile)
            "mad" -> view.setImageResource(R.drawable.headmad)
            "suprized" -> view.setImageResource(R.drawable.headsuprized)
            "normal" -> view.setImageResource(R.drawable.headnormal)
        }
    }

    private fun playerWin() {
        val gameMessage = binding.gameMessage
        val cpuHead = binding.cpuHead
        val playerHead = binding.playerHead

        playerScore += 1
        gameMessage.text = "Oyuncu Kazandı!"

        setHeadImage(cpuHead, "mad")
        setHeadImage(playerHead, "smile")
    }

    private fun cpuWin() {
        val gameMessage = binding.gameMessage
        val cpuHead = binding.cpuHead
        val playerHead = binding.playerHead

        cpuScore += 1
        gameMessage.text = "Bilgisayar Kazandı!"

        setHeadImage(cpuHead, "smile")
        setHeadImage(playerHead , "mad")
    }

    private fun draw() {
        val gameMessage = binding.gameMessage
        val cpuHead = binding.cpuHead
        val playerHead = binding.playerHead

        gameMessage.text = "Berabere..!"

        setHeadImage(cpuHead, "suprized")
        setHeadImage(playerHead, "suprized")
    }

    private fun handJumpAnim(view: View, endEvent: Boolean) {
        var funcCalled = false
        val jumpAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, -100f, 0f, -100f, 0f, -100f, 0f)
        jumpAnimator.duration = 800L
        jumpAnimator.interpolator = LinearInterpolator()
        jumpAnimator.start()

        if (endEvent) {
            jumpAnimator.addUpdateListener { animation ->
                val progress = animation.animatedFraction
                if (progress > .93f) {
                    if (!funcCalled) {
                        funcCalled = true
                        afterHandJumping()
                        shakeScreen(binding.mainLinearLayout)

                        @Suppress("DEPRECATION")
                        vib.vibrate(250)
                    }
                }
            }
        }
    }

    private fun shakeScreen(view: View) {
        val shakeAnimatorX = ObjectAnimator.ofFloat(view, "translationX", 0f, (Random.nextFloat() * 40 - 20), (Random.nextFloat() * 40 - 20), (Random.nextFloat() * 40 - 20), (Random.nextFloat() * 40 - 20), 0f)
        shakeAnimatorX.duration = 500L
        shakeAnimatorX.interpolator = LinearInterpolator()

        val shakeAnimatorY = ObjectAnimator.ofFloat(view, "translationY", 0f, (Random.nextFloat() * 40 - 20), (Random.nextFloat() * 40 - 20), (Random.nextFloat() * 40 - 20), (Random.nextFloat() * 40 - 20), 0f)
        shakeAnimatorY.duration = 500L
        shakeAnimatorY.interpolator = LinearInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(shakeAnimatorX, shakeAnimatorY)
        animatorSet.start()
    }
}