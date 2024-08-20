package com.example.myesemka.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myesemka.databinding.ItemCheckoutBinding
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CheckinAdapter(private val  item: JSONArray): RecyclerView.Adapter<CheckinAdapter.ViewHolder>() {

    inner class ViewHolder (val binding: ItemCheckoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JSONObject) {
            //ambil data checkIn
            val checkInDate = item.getString("checkIn")
            if (!item.isNull("checkIn")) {
                val parseDate = LocalDateTime.parse(checkInDate)
                val formatDate = parseDate.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"))
                binding.checkin.text = formatDate
            }
            val checkOutDate = item.getString("checkOut")
            if (!item.isNull("checkOut")) {
                val parseDate = LocalDateTime.parse(checkOutDate)
                val formatDate = parseDate.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"))
                binding.checkout.text = formatDate
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCheckoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = item.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item.getJSONObject(position))
    }
}