package com.example.recipefinder.ui.battery

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BatteryFragmentViewModel : ViewModel() {

    private val _batteryLevel = MutableLiveData<Int>()
    val batteryLevel: LiveData<Int> get() = _batteryLevel

    private val _batteryIsCharging = MutableLiveData<Boolean>()
    val batteryIsCharging: LiveData<Boolean> get() = _batteryIsCharging

    private val _batteryTemperature = MutableLiveData<Int>()
    val batteryTemperature: LiveData<Int> get() = _batteryTemperature

    private val _batteryVoltage = MutableLiveData<Int>()
    val batteryVoltage: LiveData<Int> get() = _batteryVoltage

    private val _batteryTechnology = MutableLiveData<String>()
    val batteryTechnology: LiveData<String> get() = _batteryTechnology

    fun updateBatteryInfo(context: Context, intent: Intent?) {
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
        val isCharging = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
        val temperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)?.div(10) ?: 0
        val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)?.div(1000) ?: 0
        val technology = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

        _batteryLevel.value = level
        _batteryIsCharging.value = isCharging
        _batteryTemperature.value = temperature
        _batteryVoltage.value = voltage
        _batteryTechnology.value = technology?:"Unknown"
    }
}
