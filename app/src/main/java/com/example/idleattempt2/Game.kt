package com.example.idleattempt2

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.math.RoundingMode


class Game(context: Context ?) : SurfaceView(context), Runnable{
    private val gameThread = Thread(this)
    private val TAG = "GameLog"
    val box = Rect(25,50,1025,1500)
    val resetBtn = Rect(200,1400,800,1600)

    // List of automatic things
    private var autoList = arrayOfNulls<Auto>(5)

    // Colors
    private var bgColor = Color.argb(100,182,145,107)
    private var acColor = Color.argb(100,242,235,215)
    private var btnColor = Color.argb(255,255,193,177)
    private var btnDeColor = Color.argb(255,192,192,192)
    private var txtColor = Color.argb(255,0,0,0)
    val acPaint = Paint()
    val btnPaint = Paint()
    val txtPaint = Paint()
    val moneyTxtPaint = Paint()
    val btnDePaint = Paint()

    var autoCreated = false;


    @Volatile
    public var isRunning = false
    public var money = BigDecimal("0")
    var clickAmount = BigDecimal("0.01")

    // Text
    private var moneyText = "${money.setScale(2, RoundingMode.FLOOR).toString()}$"
    init {

    }

    fun setPaintSettings(){
        acPaint.color = acColor
        btnPaint.color = btnColor
        btnDePaint.color = btnDeColor
        txtPaint.color = txtColor
        moneyTxtPaint.color = txtColor
        moneyTxtPaint.textSize = 128f
        txtPaint.textSize = 48f
    }
    fun saveData(type:String,data:String){
        GameActivity.instance.saveData(type,data)
    }
    fun getData(type: String):BigDecimal{
        return GameActivity.instance.getData(type)
    }

    fun getAutoData(){
        var temp = GameActivity.instance.getArrayData("Auto")

        for(i in 0..temp.size-1){
            autoList[i]?.upgradeLvl = temp[i]?.get("upgradeLvl")?.toInt() ?: 0
            autoList[i]?.upgradeCost = temp[i]?.get("upgradeCost") ?: BigDecimal("1")
            autoList[i]?.gainAmount = temp[i]?.get("GainAmount") ?: BigDecimal("100")
            autoList[i]?.gainTime = temp[i]?.get("GainTime") ?: BigDecimal("1000")
            autoList[i]?.updateText()
        }
    }

    fun createAuto(){
        setPaintSettings()
        autoList[0] = Auto(1,BigDecimal("2.0"),BigDecimal("0.5"),BigDecimal("500"),600,400, btnPaint,txtPaint,btnDePaint)
        autoList[1] = Auto(0,BigDecimal("5.0"), BigDecimal("1.0"),BigDecimal("1500"),600,600, btnPaint,txtPaint,btnDePaint)
        autoList[2] = Auto(0,BigDecimal("10.0"),BigDecimal("2.0"),BigDecimal("2000"),600,800, btnPaint,txtPaint,btnDePaint)
        autoList[3] = Auto(0,BigDecimal("100.0"),BigDecimal("5.0"),BigDecimal("5000"),600,1000, btnPaint,txtPaint,btnDePaint)
        autoList[4] = Auto(0,BigDecimal("1000.0"),BigDecimal("100.0"),BigDecimal("10000"),600,1200, btnPaint,txtPaint,btnDePaint)
        getAutoData()
    }

    fun saveAuto(){
        GameActivity.instance.saveArrayData(autoList)
    }

    override fun run() {
        while(isRunning == true){
            update()
            render()
        }
    }

    fun render(){
        val holder = holder
        if(holder == null){
            return
        }
        val surface = holder.surface
        if(surface == null){
            return
        }
        if(surface.isValid == false){
            return
        }
        val canvas = holder.lockCanvas() // hardwarecanvas gpu

        canvas.drawColor(bgColor)

        canvas.drawRect(resetBtn,btnPaint)
        canvas.drawRect(box,acPaint)
        canvas.drawText(moneyText, 150f,250f, moneyTxtPaint)

        for (i in autoList.indices ){
            autoList[i]?.render(canvas)
        }

        holder.unlockCanvasAndPost(canvas)
    }

    fun update(){
        moneyText = "${money.setScale(2,RoundingMode.FLOOR).toString()}$"
    }

    fun reset(){
        money = BigDecimal("0")

        autoList[0]?.upgradeLvl = 1
        autoList[0]?.upgradeCost = BigDecimal("2.0")
        autoList[0]?.gainAmount = BigDecimal("0.5")
        autoList[0]?.gainTime = BigDecimal("500")
        autoList[0]?.updateText()

        autoList[1]?.upgradeLvl = 0
        autoList[1]?.upgradeCost = BigDecimal("5.0")
        autoList[1]?.gainAmount = BigDecimal("1.0")
        autoList[1]?.gainTime = BigDecimal("1500")
        autoList[1]?.updateText()

        autoList[2]?.upgradeLvl = 0
        autoList[2]?.upgradeCost = BigDecimal("10.0")
        autoList[2]?.gainAmount = BigDecimal("2.0")
        autoList[2]?.gainTime = BigDecimal("2000")
        autoList[2]?.updateText()

        autoList[3]?.upgradeLvl = 0
        autoList[3]?.upgradeCost = BigDecimal("100.0")
        autoList[3]?.gainAmount = BigDecimal("5.0")
        autoList[3]?.gainTime = BigDecimal("5000")
        autoList[3]?.updateText()

        autoList[4]?.upgradeLvl = 0
        autoList[4]?.upgradeCost = BigDecimal("1000.0")
        autoList[4]?.gainAmount = BigDecimal("100.0")
        autoList[4]?.gainTime = BigDecimal("10000")
        autoList[4]?.updateText()
    }

    // Override the onTouchEvent method to detect when the user touches the screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            // Check if the user's touch coordinates are within the button's boundaries
            for (i in autoList.indices ){
                autoList[i]?.checkClick(x.toInt(),y.toInt())
                saveAuto()
            }
            if (resetBtn.contains(x.toInt(), y.toInt())) {
                reset()
                saveAuto()
            }
        }
        return super.onTouchEvent(event)
    }

    fun addClick(){
        money = money+clickAmount
        moneyText = "${money.toString()}$"
        saveData("Money",money.toString())
    }

    fun add(amount : BigDecimal){
        money = money+amount
        saveData("Money",money.toString())
    }

    fun take(amount : BigDecimal){
        money = money-amount
        saveData("Money",money.toString())
    }

    fun resume(){
        Log.d(TAG, "resume")
        isRunning = true
        money = getData("Money")


        if(!autoCreated){
            createAuto()
        }

        for(i in autoList){
            i?.startThread()
            i?.isRunning = true
        }
        getAutoData()
        gameThread.start()
    }

    fun pause(){
        Log.d(TAG, "pause")
        isRunning = false

        for(i in autoList){

            i?.joinThread()
            i?.isRunning = false
        }

        gameThread.join()
    }

}