package com.example.documedx.staff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.documedx.models.MedicalReport
import com.example.documedx.R

class ReportsAdapter(
    private val reports: List<MedicalReport>,
    private val onReportClick: (MedicalReport) -> Unit
) : RecyclerView.Adapter<ReportsAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportTitle: TextView = itemView.findViewById(R.id.tv_report_title)
        val reportDate: TextView = itemView.findViewById(R.id.tv_report_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]

        holder.reportTitle.text = report.title
        holder.reportDate.text = holder.itemView.context.getString(
            R.string.report_received_on,
            report.receivedDate
        )

        holder.itemView.setOnClickListener {
            onReportClick(report)
        }
    }

    override fun getItemCount() = reports.size
}