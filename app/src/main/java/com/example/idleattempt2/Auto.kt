package com.example.idleattempt2

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import java.math.BigDecimal
import java.math.RoundingMode

class Auto(upgradeLvl : Int, upgradeCost : BigDecimal, gainAmount: BigDecimal, gainTime : BigDecimal, x : Int, y: Int, btnPaint: Paint, txtPaint: Paint, btnDePaint : Paint) : Runnable{
    val TAG = "AUTO"

    private val loopThread = Thread(this)
    public var upgradeLvl = upgradeLvl
    public var upgradeCost = upgradeCost
    public var gainAmount = gainAmount
    public var gainTime = gainTime

    // Convert to seconds
    var gainToSec = gainTime.divide(BigDecimal("1000"), 2,RoundingMode.HALF_UP)
    var gainRunsPerSec = BigDecimal("1.0").divide(gainToSec, 2,RoundingMode.HALF_UP)
    var gainPerSec = (gainRunsPerSec.multiply(gainAmount))

    // Pos
    var x = x
    var y = y

    // Colors
    var btnDePaint = btnDePaint
    var btnPaint = btnPaint
    var txtPaint = txtPaint

    // Button blueprint
    var btnUpgradeRect = Rect(50,y+10,450,y+100)

    // Game things
    private var money = BigDecimal("0")
    var isRunning = false
    var timeRemaining = gainTime

    // Upgrades
    val upgradePath = mutableMapOf<Int, BigDecimal>().apply {
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
        // Gameloop
        while (isRunning){
            update()
        }
    }

    fun updateText(){
        gainToSec = gainTime.divide(BigDecimal("1000"), 2,RoundingMode.HALF_UP)
        gainRunsPerSec = BigDecimal("1").divide(gainToSec, 2,RoundingMode.HALF_UP)
        gainPerSec = (gainRunsPerSec.multiply(gainAmount))

        upgradeText = "Lvl: ${upgradeLvl.toString()} Cost: ${upgradeCost.setScale(2,RoundingMode.FLOOR).toString()}$"
    }

    // Upgrade/Level up
    fun upgrade() {
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
                    val path = upgradePath.get(upgradeLvl)
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
    fun update() {
        if(upgradeLvl != 0){
            if(gainTime.compareTo(BigDecimal("1000")) <= 0){
                upgradeLvlText = "${gainPerSec.setScale(2,RoundingMode.FLOOR).toString()}$ /s"
                Thread.sleep(gainTime.toLong())
                gain()
            } else{
                repeat(gainTime.toInt()){
                    Thread.sleep(1)
                    timeRemaining = timeRemaining.minus(BigDecimal(1))
                    upgradeLvlText = "${timeRemaining.divide(BigDecimal("1000")).setScale(2,RoundingMode.FLOOR).toString()} Sec ${gainAmount.toString()} $"
                }
                timeRemaining = gainTime
                gain()
            }
        }
    }

    fun getMoney():BigDecimal{
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

    fun gain(){
        GameActivity.instance.game.add(gainAmount)
    }

    fun startThread() {
        Log.d(TAG, "resume")
        loopThread.start()
    }

    fun joinThread(){
        Log.d(TAG, "pause")
        loopThread.join()
    }

    fun checkClick(xTouch: Int, yTouch: Int){

        if (btnUpgradeRect.contains(xTouch.toInt(), yTouch.toInt())) {

            upgrade()
        }
    }
}


