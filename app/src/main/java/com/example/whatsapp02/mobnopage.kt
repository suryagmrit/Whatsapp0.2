package com.example.whatsapp02

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class mobnopage : AppCompatActivity() {
    lateinit var verify:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobnopage)
        verify=findViewById(R.id.btverify)
        verify.setOnClickListener{
            startActivity(Intent(this,otpverification::class.java))

        }
    }
}