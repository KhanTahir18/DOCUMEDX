package com.example.documedx.staff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.documedx.models.Patient
import com.example.documedx.R

class PatientsAdapter(
    private val patients: List<Patient>,
    private val onPatientClick: (Patient) -> Unit
) : RecyclerView.Adapter<PatientsAdapter.PatientViewHolder>() {

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientName: TextView = itemView.findViewById(R.id.tv_patient_name)
        val reportCount: TextView = itemView.findViewById(R.id.tv_report_count)
        val statusIndicator: View = itemView.findViewById(R.id.view_status_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]

        holder.patientName.text = patient.name

        val context = holder.itemView.context
        if (patient.reportCount > 0) {
            holder.reportCount.text = context.getString(R.string.reports_count, patient.reportCount)
            holder.reportCount.setTextColor(context.getColor(R.color.color_palette_5))
            holder.statusIndicator.setBackgroundColor(context.getColor(R.color.color_palette_5))
        } else {
            holder.reportCount.text = context.getString(R.string.no_reports_available)
            holder.reportCount.setTextColor(context.getColor(R.color.color_palette_4))
            holder.statusIndicator.setBackgroundColor(context.getColor(R.color.black))
        }

        holder.itemView.setOnClickListener {
            onPatientClick(patient)
        }
    }

    override fun getItemCount() = patients.size
}