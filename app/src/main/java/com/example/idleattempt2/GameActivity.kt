package com.example.idleattempt2

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import java.math.BigDecimal

class GameActivity : AppCompatActivity() {
    lateinit var game : Game

    companion object {
        lateinit var instance: GameActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            else -> {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }


        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("test","main")
        } else {
            Log.d("test","not main")
        }
        super.onCreate(savedInstanceState)
        instance = this
        game = Game(this)
        setContentView(game)
    }

    override fun onStart() {
        super.onStart()
    }

    fun saveData(type : String, data: String){
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        when(type){
            "Money" -> editor.putFloat("Money",data.toFloat())
        }
        editor.apply()
    }

    fun saveArrayData(data: Array<Auto?>){
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        var counter = 0

        editor.putInt("Length",data.size)
        for(i in data){
            Log.d("Value", "myArray: ${i?.upgradeCost.toString()}")
            counter += 1
            Log.d("Value", "Counter: $counter")
            editor.putString(counter.toString(),"${i?.upgradeLvl.toString()}|${i?.gainAmount.toString()}|${i?.gainTime.toString()}|${i?.upgradeCost.toString()}")
        }
        editor.apply()
    }

    fun getData(type: String): BigDecimal {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        return BigDecimal(sharedPref.getFloat(type, 0f).toDouble())
    }

    fun getArrayData(): Array<Map<String, BigDecimal>?> {
                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                val len = sharedPref.getInt("Length", 0)
                val returnList = arrayOfNulls<Map<String, BigDecimal>?>(len)
                Log.d("Value", len.toString())
                for (i in 1 until len+1) {
                    val value = sharedPref.getString(i.toString(), "")?.split("|")


                    returnList[i-1] =
                        mapOf(
                            "upgradeLvl" to BigDecimal(value?.get(0) ?: "0"),
                            "GainAmount" to BigDecimal(value?.get(1) ?: "0"),
                            "GainTime" to BigDecimal(value?.get(2) ?: "1"),
                            "upgradeCost" to BigDecimal(value?.get(3) ?: "0")
                        )
                    Log.d("Value", "myArray: ${returnList.contentToString()}")

                }
        Log.d("Value", "myArray: ${returnList.contentToString()}")
        return returnList
    }

    override fun onResume() {
        super.onResume()
        game.resume()
    }

    override fun onPause() {
        super.onPause()
        game.pause()
    }
}