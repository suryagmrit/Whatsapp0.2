package com.example.whatsapp02

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class otpverification : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var resend: TextView
    lateinit var OTP1: EditText
    lateinit var OTP2: EditText
    lateinit var OTP3: EditText
    lateinit var OTP4: EditText
    lateinit var OTP5: EditText
    lateinit var OTP6: EditText
    lateinit var verify:Button
    lateinit var OTP:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var phoneNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!
        init()
        addTextChangeListener()
        resendOTPTvVisibility()

        resend.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }
        verify.setOnClickListener {
            //collect otp from all the edit texts
            val typedOTP =
                (OTP1.text.toString() + OTP2.text.toString() + OTP3.text.toString()
                        + OTP4.text.toString() + OTP5.text.toString() + OTP6.text.toString())
            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP, typedOTP
                    )
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun resendOTPTvVisibility() {
        OTP1.setText("")
        OTP2.setText("")
        OTP3.setText("")
        OTP4.setText("")
        OTP5.setText("")
        OTP6.setText("")
        resend.visibility = View.INVISIBLE
        resend.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            resend.visibility = View.VISIBLE
            resend.isEnabled = true
        }, 60000)
    }
    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }
        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }
        }
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            OTP = verificationId
            resendToken = token
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_SHORT).show()
                    sendToHome()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    private fun sendToHome() {
        startActivity(Intent(this, homescreen::class.java).putExtra("phoneNumber",phoneNumber))
    }

    private fun addTextChangeListener() {
        OTP1.addTextChangedListener(EditTextWatcher(OTP1))
        OTP2.addTextChangedListener(EditTextWatcher(OTP2))
        OTP3.addTextChangedListener(EditTextWatcher(OTP3))
        OTP4.addTextChangedListener(EditTextWatcher(OTP4))
        OTP5.addTextChangedListener(EditTextWatcher(OTP5))
        OTP6.addTextChangedListener(EditTextWatcher(OTP6))
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        verify = findViewById(R.id.btverify)
        resend = findViewById(R.id.resendtxt)
        OTP1 = findViewById(R.id.edtnum1)
        OTP2 = findViewById(R.id.edtnum2)
        OTP3 = findViewById(R.id.edtnum3)
        OTP4 = findViewById(R.id.edtnum4)
        OTP5 = findViewById(R.id.edtnum5)
        OTP6 = findViewById(R.id.edtnum6)
    }
    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
        override fun afterTextChanged(p0: Editable?) {

            val text = p0.toString()
            when (view.id) {
                R.id.edtnum1 -> if (text.length == 1) OTP2.requestFocus()
                R.id.edtnum2 -> if (text.length == 1) OTP3.requestFocus() else if (text.isEmpty()) OTP1.requestFocus()
                R.id.edtnum3 -> if (text.length == 1) OTP4.requestFocus() else if (text.isEmpty()) OTP2.requestFocus()
                R.id.edtnum4 -> if (text.length == 1) OTP5.requestFocus() else if (text.isEmpty()) OTP3.requestFocus()
                R.id.edtnum5 -> if (text.length == 1) OTP6.requestFocus() else if (text.isEmpty()) OTP4.requestFocus()
                R.id.edtnum6 -> if (text.isEmpty())   OTP5.requestFocus()

            }
        }

    }
    }