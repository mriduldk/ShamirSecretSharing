package com.codingstudio.shamirsecretsharing.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codingstudio.shamirsecretsharing.R
import kotlinx.android.synthetic.main.activity_user2.*

class UserActivity2 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user2)


        textViewRequestBtn.setOnClickListener {

            startActivity(Intent(this, UserActivity::class.java))
        }

        textViewConfirmation.setOnClickListener {

            startActivity(Intent(this, UserActivity3::class.java))
        }

    }
}