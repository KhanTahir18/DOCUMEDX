package com.example.documedx.organization

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.documedx.Department
import com.example.documedx.Staff
import com.example.documedx.User
import com.example.documedx.databinding.ItemDepartmentBinding
import com.example.documedx.databinding.ItemPatientBinding

class PatientsAdapter(
    private val patientList: List<User>,
    private val onDeleteClick: (User) -> Unit // <-- Add this line
): RecyclerView.Adapter<PatientsAdapter.PatientViewHolder>() {

    inner class PatientViewHolder(val binding: ItemPatientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patientList[position]
        holder.binding.patientNameTV.text = "${patient.firstName} ${patient.lastName}"
    }


    override fun getItemCount(): Int = patientList.size
}