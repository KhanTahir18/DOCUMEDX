package com.example.documedx.patient

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.documedx.Organization
import com.example.documedx.OrganizationReport
import com.example.documedx.R
import com.example.documedx.databinding.ItemHospitalCardBinding
import com.example.documedx.databinding.ItemReportCardBinding

class HospitalsAdapter(
    private var hospitalsList: List<Organization>,
) : RecyclerView.Adapter<HospitalsAdapter.HospitalViewHolder>() {

    inner class HospitalViewHolder(val binding: ItemHospitalCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val binding = ItemHospitalCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HospitalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val hospital = hospitalsList[position]
        holder.binding.apply {
            tvHospitalName.text = hospital.organizationName ?: "Unknown"
            tvHospitalType.text = hospital.organizationType ?: "-"
            tvPhoneNumber.text = hospital.phoneNo ?: "-"
            tvEmail.text = hospital.emailId ?: "-"
        }


    }

    override fun getItemCount() = hospitalsList.size

    fun updateList(newList: List<OrganizationReport>) {
//        hospitalsList.clear()
//        hospitalsList.addAll(newList)
//        notifyDataSetChanged()
    }
}