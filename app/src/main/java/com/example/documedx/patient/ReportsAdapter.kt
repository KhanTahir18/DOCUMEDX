package com.example.documedx.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.documedx.R

class ReportsAdapter(
    private val reports: List<Report>,
    private val onReportClick: (Report) -> Unit,
    private val onShareClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportTime: TextView = itemView.findViewById(R.id.tv_report_time)
        val reportIcon: ImageView = itemView.findViewById(R.id.iv_report_icon)
        val reportName: TextView = itemView.findViewById(R.id.tv_report_name)
        val reportId: TextView = itemView.findViewById(R.id.tv_report_id)
        val reportDate: TextView = itemView.findViewById(R.id.tv_report_date)
        val shareIcon: ImageView = itemView.findViewById(R.id.iv_share_icon)
        val fromHospital: TextView? = itemView.findViewById(R.id.tv_from_hospital)
        val prescriptionLabel: TextView? = itemView.findViewById(R.id.tv_prescription_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_card, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]

        holder.reportTime.text = report.time
        holder.reportName.text = report.title
        holder.reportId.text = report.id
        holder.reportDate.text = report.date

        // Show hospital info for shared reports
        holder.fromHospital?.visibility = if (report.fromHospital.isNotEmpty()) View.VISIBLE else View.GONE
        holder.fromHospital?.text = "From: ${report.fromHospital}"

        // Show prescription label for shared reports
        holder.prescriptionLabel?.visibility = if (report.hasPrescription) View.VISIBLE else View.GONE

        // Click listeners
        holder.reportIcon.setOnClickListener {
            onReportClick(report)
        }

        holder.shareIcon.setOnClickListener {
            onShareClick(report)
        }

        holder.itemView.setOnClickListener {
            onReportClick(report)
        }
    }

    override fun getItemCount() = reports.size
}