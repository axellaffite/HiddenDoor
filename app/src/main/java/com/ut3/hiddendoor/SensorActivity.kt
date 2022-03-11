package com.ut3.hiddendoor

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ut3.hiddendoor.databinding.SensorActivityBinding

class SensorActivity: AppCompatActivity() {
    private lateinit var binding: SensorActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = SensorActivityBinding.inflate(layoutInflater)

        binding.luminosityButton.apply {
            setOnClickListener {
                text = "✔"
                isEnabled = false

                binding.orientationButton.requestFocus()
            }
        }

        binding.orientationButton.apply {
            setOnClickListener {
                text = "✔"
                isEnabled = false

                binding.shakeButton.requestFocus()
            }
        }

        binding.shakeButton.apply {
            setOnClickListener {
                binding.playButton.requestFocus()
            }
        }

        setContentView(binding.root)
    }

}