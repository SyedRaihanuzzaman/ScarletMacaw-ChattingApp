package com.raihan.scarletmacaw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.raihan.scarletmacaw.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVerificationBinding
    var auth:FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if(auth!!.currentUser!=null){
            startActivity(Intent(this@VerificationActivity,MainActivity::class.java))
            finish()
        }

        binding.numberEtxt.requestFocus()
        binding.continueBtn.setOnClickListener {
            var intent = Intent(this@VerificationActivity,OTPActivity::class.java)
            intent.putExtra("phoneNumber",binding.numberEtxt.text.toString())
            startActivity(intent)
        }

    }
}