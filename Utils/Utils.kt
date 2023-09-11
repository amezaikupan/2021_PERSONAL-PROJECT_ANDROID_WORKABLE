package com.improver.workable.Utils

import android.graphics.Color
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.improver.workable.R
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.emitters.StreamEmitter
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun areAllTrue(array: BooleanArray): Boolean {
        for (b in array) if (!b) return false
        return true
    }

    fun makeHour(millis: Long): String {
        var dateFormatter = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        var date = Date(millis)
        var reDate = dateFormatter.format(date)
        return reDate.toString()
    }

    fun makeDate(millis: Long): String {
        var dateFormatter = SimpleDateFormat("dd/MMM", Locale.ENGLISH)
        var date = Date(millis)
        var reDate = dateFormatter.format(date)
        return reDate.toString()
    }


    fun emptyFieldSafeguard(inputField: EditText, noticeText: String){
        if (inputField.text.isEmpty()){
            Toast.makeText(inputField.context,
                "You haven't filled in your " + noticeText,
                Toast.LENGTH_SHORT).show()
        }
    }

    fun emptyTextFieldSafeguard(textDisplay: TextView, noticeText: String){
        if (textDisplay.text.isEmpty()){
            Toast.makeText(textDisplay.context,
                "You haven't filled in your " + noticeText,
                Toast.LENGTH_SHORT).show()
        }
    }

    fun goodDay(): String{
        var hourOfDaty = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var goodDay : String =""
        if (hourOfDaty <13){
            goodDay = "Good morning 🌻!"
        }else if (18>hourOfDaty && hourOfDaty>=13){
            goodDay = "Good afternoon 🍪!"
        }else if (hourOfDaty>=18){
            goodDay = "Good evening 🍓!"

        }
        return goodDay
    }

    fun dateRangeDeadlineAlarmer(deadline: Long): Int {
        val today = Calendar.getInstance().timeInMillis
        val days = ((deadline - today) / (1000 * 60 * 60 * 24)).toInt()
        val color: Int

        if (days> 7){
            color = 1
        }else if (days<=7 && days>3){
            color = 2
        }else if (0<= days && days<=3 ){
            color = 3
        }else{
            color = -1
        }

        return color
    }

    fun setTaskColor(rangeCode: Int): Int{
        var color : Int = 0
        when(rangeCode){
            1 -> color = R.color.green_task
            2 -> color = R.color.yellow_task
            3 -> color = R.color.red_background
            -1 -> color = R.color.offtask

        }
        return color
    }

    fun setBackgroundColor(rangeCode: Int): Int{
        var color : Int = 0
        when(rangeCode){
            1 -> color = R.color.green_background
            2 -> color = R.color.yellow_background
            3 -> color = R.color.red_background

        }
        return color
    }
    fun finishTaskConsgrats(konfettiView: KonfettiView){
        konfettiView.build()
            .addColors(ContextCompat.getColor(konfettiView.context, R.color.green_task), ContextCompat.getColor(konfettiView.context, R.color.yellow_task), ContextCompat.getColor(konfettiView.context, R.color.red_task), Color.YELLOW, Color.BLUE, Color.RED)
            .setDirection(0.0, 359.0)
            .setSpeed(0.5f, 2f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle, Shape.DrawableShape((R.drawable.ic_baseline_check_circle_outline_24.toDrawable())))
            .addSizes(Size(12))
            .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
            .streamFor(particlesPerSecond = 200, emittingTime = StreamEmitter.INDEFINITE)
    }

    fun makeMillis(time: String) : Long{


        var dateFormatter = SimpleDateFormat("dd/MM/yyyy")

        var outputDate = dateFormatter.parse(time)

        val dateInMilli: Long

        dateInMilli = outputDate.time

        return dateInMilli
    }


}
