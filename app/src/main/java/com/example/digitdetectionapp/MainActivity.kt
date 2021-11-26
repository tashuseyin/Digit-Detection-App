package com.example.digitdetectionapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.digitdetectionapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var digitClassifier = DigitClassifier(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            drawView.setStrokeWidth(70.0f)
            drawView.setColor(Color.WHITE)
            drawView.setBackgroundColor(Color.BLACK)
        }
        setListeners()
        digitClassifier
            .initialize()
            .addOnFailureListener { e -> Log.e(TAG, "Error to setting up digit classifier.", e) }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.clearButton.setOnClickListener {
            binding.drawView.clearCanvas()
            binding.predictionText.text = getString(R.string.place_holder)
        }
        binding.drawView.setOnTouchListener { _, event ->
            binding.drawView.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP){
                classifyDrawing()
            }
            true
        }
    }

    private fun classifyDrawing() {
        val bitmap = binding.drawView.getBitmap()

        if (digitClassifier.isInitialized) {
            digitClassifier
                .classifyAsync(bitmap)
                .addOnSuccessListener { resultText -> binding.predictionText.text = resultText }
                .addOnFailureListener { e ->
                    binding.predictionText.text = e.localizedMessage
                    Log.e("TAG", "Error classifying drawing.", e)
                }
        }
    }

}
