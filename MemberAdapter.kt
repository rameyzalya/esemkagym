package com.example.myesemka.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myesemka.databinding.ItemMemberBinding
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class MemberAdapter(
    val list: JSONArray,
    val status: String,
    private val onConfirm: (id: String) -> Unit,
    private val onResume: (id: String) -> Unit
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JSONObject) {
            when (status) {
                "ACTIVE" -> {
                    val membershipEnd = item.getString("membershipEnd")
                    binding.subs.text = "Member until $membershipEnd"
                    val endDate = LocalDate.parse(membershipEnd)
                if (endDate.isBefore(LocalDate.now().plusDays(7))) {
                    binding.rbutton.visibility = View.VISIBLE
                    binding.rbutton.text = "Resume"
                    binding.rbutton.setOnClickListener {
                        onResume(item.getString("id"))
                    }
                } else {
                    binding.rbutton.visibility = View.GONE
                }
            }


            "INACTIVE" -> {
                binding.subs.text = "Last membership at ${item.getString("joinedMemberAt")}"
                binding.rbutton.setOnClickListener {
                    onResume(item.getString("id"))
                }
            }
            "PENDING_APPROVAL" -> {
                binding.subs.text = "Register at ${item.getString("registerAt")}"
                binding.rbutton.text = "Confirm"
                binding.rbutton.setOnClickListener {
                    Log.d("TAG", "itemnya: ${item.getString("id")} ")
                    onConfirm(item.getString("id"))
                }
            }
        }
        Log.d("TAG", "isi list member $item")
        binding.name.text = item.getString("name")
    }
}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding = ItemMemberBinding.inflate(layoutInflater, parent, false)
    return ViewHolder(binding)
}

override fun getItemCount(): Int = list.length()


override fun onBindViewHolder(holder: MemberAdapter.ViewHolder, position: Int) {
    holder.bind(list.getJSONObject(position))
}
}