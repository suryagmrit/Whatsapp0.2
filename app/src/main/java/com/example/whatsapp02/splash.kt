package com.example.whatsapp02

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        android.os.Handler().postDelayed({display()},3000)
    }
    private fun display(){
        startActivity(Intent(this,mobnopage::class.java))
        finish()
    }
}