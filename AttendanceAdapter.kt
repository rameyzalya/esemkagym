package com.example.myesemka.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myesemka.databinding.ListAttendanceBinding
import org.json.JSONArray
import org.json.JSONObject

class AttendanceAdapter(val list : JSONArray): RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ListAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: JSONObject,position: Int) {
        val user =item.getJSONObject("user")
        binding.tvMembername.text=user.getString("name")

        //terpecah menjadi 2 index, yang [0] itu date, [1] merupakan time
        //split T yang memisahkan date dan hour
        //contoh data 2024-07-19T10:35:04.8506
        val checkInDateTime = item.getString("checkIn").split("T")


        binding.`in`.text = checkInDateTime[0]
        //kita ingin menghilangkan data untuk milisecondnya, jadi kita split titik yang merupakan pemisah dari second dan milisecond. lalu hanya mengambil index 0 yang merupakan second.
        binding.inHour.text = checkInDateTime[1].split(".")[0]


        if (item.getString("checkOut") != "null"){
            val checkOutDateTime : List<String?>? = item.getString("checkOut").split("T")
            binding.out.text = checkOutDateTime?.get(0) ?: ""
            binding.outHour.text = checkOutDateTime?.get(1)?.split(".")?.get(0) ?: ""
        }

    }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListAttendanceBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(list.getJSONObject(position),position)
    }
}