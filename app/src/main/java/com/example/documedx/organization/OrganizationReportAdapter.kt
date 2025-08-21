package com.example.documedx.organization

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.documedx.OrganizationReport
import com.example.documedx.databinding.ItemReportCardBinding

class OrganizationReportAdapter(
    private val context: Context,
    private val reportsList: MutableList<OrganizationReport>
) : RecyclerView.Adapter<OrganizationReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(val binding: ItemReportCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportsList[position]

        holder.binding.apply {
            tvReportName.text = report.id ?: "Unknown"
            tvReportId.text = "ID: ${report.id ?: "-"}"
            tvReportDate.text = report.dateWhenSharred ?: "-"
            tvReportTime.text = report.timeWhenShared ?: "-"
            tvFromHospital.text = "From: ${report.fromOrg ?: "-"}"

            ivReportIcon.setOnClickListener {
                report.reportUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount() = reportsList.size

    fun updateList(newList: List<OrganizationReport>) {
        reportsList.clear()
        reportsList.addAll(newList)
        notifyDataSetChanged()
    }
}
