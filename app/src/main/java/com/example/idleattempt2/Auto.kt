package com.example.idleattempt2

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import java.math.BigDecimal
import java.math.RoundingMode

class Auto(var upgradeLvl: Int,
           var upgradeCost: BigDecimal,
           var gainAmount: BigDecimal,
           var gainTime: BigDecimal, // Pos
           private var x: Int, private var y: Int, private var btnPaint: Paint, private var txtPaint: Paint, // Colors
           private var btnDePaint: Paint
) : Runnable{
    private val TAG = "AUTO"

    private lateinit var loopThread : Thread

    // Convert to seconds
    private var gainToSec = gainTime.divide(BigDecimal("1000"), 2,RoundingMode.HALF_UP)
    private var gainRunsPerSec = BigDecimal("1.0").divide(gainToSec, 2,RoundingMode.HALF_UP)
    private var gainPerSec = (gainRunsPerSec.multiply(gainAmount))

    // Button blueprint
    private var btnUpgradeRect = Rect(50,y+20,450,y+110)

    // Game things
    private var money = BigDecimal("0")
    var isRunning = false
    private var timeRemaining = gainTime

    // Upgrades
    private val upgradePath = mutableMapOf<Int, BigDecimal>().apply {
        this[10] = BigDecimal("2")
        this[25] = BigDecimal("2")
        this[50] = BigDecimal("5")
        this[75] = BigDecimal("2")
        this[100] = BigDecimal("10")
    }

    // Text
    private var upgradeLvlText = "${gainPerSec.toString()}$ /s"
    private var upgradeText = "Lvl: ${upgradeLvl.toString()} Cost: ${upgradeCost.toString()}$"



    init {
        // Fetch money
        money = getMoney()

    }

    override fun run() {
        // Game loop
        while (isRunning){
            update()
        }
    }

    fun updateText(){
        gainToSec = gainTime.divide(BigDecimal("1000"), 2,RoundingMode.HALF_UP)
        gainRunsPerSec = BigDecimal("1").divide(gainToSec, 2,RoundingMode.HALF_UP)
        gainPerSec = (gainRunsPerSec.multiply(gainAmount))

        upgradeText = "Lvl: ${upgradeLvl.toString()} Cost: $ ${upgradeCost.setScale(2,RoundingMode.FLOOR).toString()}"
    }

    // Upgrade/Level up
    private fun upgrade() {
        // Fetch money so we know if you can buy
        money = getMoney()
        if(money.compareTo(upgradeCost) >= 0){
            if(upgradeLvl == 0){
                GameActivity.instance.game.take(upgradeCost)
                upgradeLvl = upgradeLvl+1
            } else{
                GameActivity.instance.game.take(upgradeCost)
                upgradeLvl = upgradeLvl+1

                if(upgradePath.containsKey(upgradeLvl)){
                    val path = upgradePath[upgradeLvl]
                    gainAmount = gainAmount.plus(BigDecimal("0.2"))
                    gainAmount = gainAmount.multiply(path)
                    upgradeCost = upgradeCost.multiply(BigDecimal("1.1"))
                } else{
                    gainAmount = gainAmount.plus(BigDecimal("0.2"))
                    upgradeCost = upgradeCost.multiply(BigDecimal("1.1"))
                }
            }
            updateText()
        }
    }
    private fun update() {
        if(upgradeLvl != 0){
            if(gainTime.compareTo(BigDecimal("1000")) <= 0){
                upgradeLvlText = "$ ${gainPerSec.setScale(2,RoundingMode.FLOOR).toString()} /s"
                Thread.sleep(gainTime.toLong())
                gain()
            } else{
                repeat(gainTime.toInt()){
                    Thread.sleep(1)
                    timeRemaining = timeRemaining.minus(BigDecimal(1))
                    upgradeLvlText = "${timeRemaining.divide(BigDecimal("1000")).setScale(2,RoundingMode.FLOOR).toString()} Sec $ ${gainAmount.toString()}"
                }
                timeRemaining = gainTime
                gain()
            }
        }
    }

    private fun getMoney():BigDecimal{
        return BigDecimal(GameActivity.instance.game.money.toDouble())
    }

    fun render(canvas : Canvas){
        money = getMoney()
        if(money<upgradeCost){
            canvas.drawRect(btnUpgradeRect,btnDePaint)
        } else{
            canvas.drawRect(btnUpgradeRect,btnPaint)
        }

        canvas.drawText("Upgrade", btnUpgradeRect.centerX().toFloat()-80f,btnUpgradeRect.centerY().toFloat()+10f, txtPaint)
        if(upgradeLvl > 0){
            canvas.drawText(upgradeLvlText, x.toFloat(),y.toFloat(), txtPaint)
        }

        canvas.drawText(upgradeText, 50f,y.toFloat(), txtPaint)
    }

    private fun gain(){
        GameActivity.instance.game.add(gainAmount)
    }

    fun startThread() {
        loopThread = Thread(this)
        Log.d(TAG, "resume")
        loopThread.start()
    }

    fun joinThread(){
        loopThread = Thread(this)
        Log.d(TAG, "pause")
        loopThread.join()
    }

    fun takeTime(amount:BigDecimal){
        gainTime.minus(amount)
    }

    fun divideTime(amount:BigDecimal){
        gainTime.divide(amount)
    }

    fun checkClick(xTouch: Int, yTouch: Int){

        if (btnUpgradeRect.contains(xTouch, yTouch)) {

            upgrade()
        }
    }
}


