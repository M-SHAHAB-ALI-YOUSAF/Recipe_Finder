package com.example.recipefinder.ui.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.recipefinder.R
import com.example.recipefinder.databinding.FragmentBatteryBinding

class BatteryFragment : Fragment(R.layout.fragment_battery) {

    private lateinit var binding: FragmentBatteryBinding
    private lateinit var viewModel: BatteryFragmentViewModel

    private val batteryInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.updateBatteryInfo(context!!, intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBatteryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(BatteryFragmentViewModel::class.java)

        observeBatteryInfo()

        return binding.root
    }

    private fun observeBatteryInfo() {
        viewModel.batteryLevel.observe(viewLifecycleOwner) { batteryLevel ->
            binding.batteryProgress.setProgress(100 - batteryLevel)
            if(batteryLevel > 50){
                binding.tvBatteryLevel.setTextColor(Color.WHITE)
            }
            binding.tvBatteryLevel.text = "$batteryLevel%"
        }

        viewModel.batteryIsCharging.observe(viewLifecycleOwner) { isCharging ->
            binding.tvPlugInValue.text = if (isCharging) "plug in" else "plug out"
        }

        viewModel.batteryVoltage.observe(viewLifecycleOwner) { voltage ->
            binding.tvVoltageValue.text = "$voltage V"
        }

        viewModel.batteryTemperature.observe(viewLifecycleOwner) { temperature ->
            binding.tvTemperatureValue.text = "$temperature C"
        }

        viewModel.batteryTechnology.observe(viewLifecycleOwner) { technology ->
            binding.tvTechnologyValue.text = technology
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(batteryInfoReceiver)
    }
}
