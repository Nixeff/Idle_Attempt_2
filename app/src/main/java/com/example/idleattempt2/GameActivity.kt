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

        // Fixes so that the orientation on yhe phoene is stuck on portrait
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

        super.onCreate(savedInstanceState)
        instance = this
        game = Game(this)
        setContentView(game)
    }

    // Saves money
    fun saveData(type : String, data: String){
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        when(type){
            "Money" -> editor.putFloat("Money",data.toFloat())
        }
        editor.apply()
    }

    // Saves the state of the auto objects
    fun saveArrayData(data: Array<Auto?>){
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        var counter = 0

        editor.putInt("Length",data.size)
        for(i in data){
            counter += 1
            editor.putString(counter.toString(),"${i?.upgradeLvl.toString()}|${i?.gainAmount.toString()}|${i?.gainTime.toString()}|${i?.upgradeCost.toString()}")
        }
        editor.apply()
    }

    // Gets the money
    fun getData(type: String): BigDecimal {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        return BigDecimal(sharedPref.getFloat(type, 0f).toDouble())
    }

    // Gets the data for the auto objects
    fun getArrayData(): Array<Map<String, BigDecimal>?> {
                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                val len = sharedPref.getInt("Length", 0)
                val returnList = arrayOfNulls<Map<String, BigDecimal>?>(len)

                for (i in 1 until len+1) {
                    val value = sharedPref.getString(i.toString(), "")?.split("|")
                    returnList[i-1] =
                        mapOf(
                            "upgradeLvl" to BigDecimal(value?.get(0) ?: "0"),
                            "GainAmount" to BigDecimal(value?.get(1) ?: "0"),
                            "GainTime" to BigDecimal(value?.get(2) ?: "1"),
                            "upgradeCost" to BigDecimal(value?.get(3) ?: "0")
                        )
                }
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