package com.example.documedx.patient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.documedx.R
import com.example.documedx.databinding.ItemHospitalCardBinding

class HospitalsAdapter(
    private var hospitalsList: List<Hospital>,
    private val onScheduleClick: (Hospital) -> Unit
) : RecyclerView.Adapter<HospitalsAdapter.HospitalViewHolder>() {

    class HospitalViewHolder(private val binding: ItemHospitalCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hospital: Hospital, onScheduleClick: (Hospital) -> Unit) {
            binding.tvHospitalName.text = hospital.name
            binding.tvHospitalType.text = hospital.type
            binding.tvPhoneNumber.text = hospital.phoneNumber
            binding.tvEmail.text = hospital.email

            // Load hospital image
            if (hospital.imageRes != 0) {
                binding.ivHospitalImage.setImageResource(hospital.imageRes)
            } else if (hospital.imageUrl.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(hospital.imageUrl)
                    .placeholder(R.drawable.ic_hospital_placeholder)
                    .error(R.drawable.ic_hospital_placeholder)
                    .into(binding.ivHospitalImage)
            } else {
                binding.ivHospitalImage.setImageResource(R.drawable.ic_hospital_placeholder)
            }

            // Schedule button click
            binding.btnSchedule.setOnClickListener {
                onScheduleClick(hospital)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val binding = ItemHospitalCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HospitalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        holder.bind(hospitalsList[position], onScheduleClick)
    }

    override fun getItemCount(): Int = hospitalsList.size

    fun updateList(newList: List<Hospital>) {
        hospitalsList = newList
        notifyDataSetChanged()
    }
}