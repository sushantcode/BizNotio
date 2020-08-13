package com.example.biznoti0

import android.content.Intent
import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_rating.*

class Rating : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        ratingBar.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {

                Toast.makeText(this@Rating, "Given rating is: $p1", Toast.LENGTH_SHORT).show()
                val intent = Intent (this@Rating, MainActivity::class.java)
                startActivity(intent)

            }
        })

    }


    }
