package com.example.recipefinder.adaptor

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipefinder.R
import com.example.recipefinder.databinding.ItemDateBinding
import java.text.SimpleDateFormat
import java.util.*

interface OnDateSelectedListener {
    fun onDateSelected(selectedDate: String)
}

class DateAdapter(
    private val dates: List<String>,
    private val listener: OnDateSelectedListener
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private val todayDate = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
    private var selectedPosition = getTodayPosition()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dates[position]
        val parts = date.split(", ")
        holder.bind(parts[0], parts[1])

        if (date == todayDate) {
            holder.binding.dateText.setBackgroundResource(R.drawable.today_date_background)
            holder.binding.dateText.setTextColor(Color.WHITE)
        } else {
            holder.binding.dateText.setBackgroundResource(0)
            holder.binding.dateText.setTextColor(Color.BLACK)
        }

        if (position == selectedPosition) {
            holder.binding.dateText.setBackgroundResource(R.drawable.today_date_background)
            holder.binding.dateText.setTextColor(Color.WHITE)
        } else {
            holder.binding.dateText.setBackgroundResource(0)
            holder.binding.dateText.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            selectDate(position)
        }
    }

    override fun getItemCount() = dates.size

    inner class DateViewHolder(val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(day: String, monthDay: String) {
            binding.dateText.text = "$day\n$monthDay"
        }
    }

    private fun selectDate(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)

        listener.onDateSelected(dates[selectedPosition])
    }

    private fun getTodayPosition(): Int {
        return dates.indexOfFirst { it == todayDate }.takeIf { it >= 0 } ?: 0
    }
}
