package com.example.documedx.organization

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.documedx.Department
import com.example.documedx.databinding.ItemDepartmentBinding

class DepartmentAdapter(
    private val departmentList: List<Department>,
    private val onDeleteClick: (Department) -> Unit // <-- Add this line
     ): RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>() {

    inner class DepartmentViewHolder(val binding: ItemDepartmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val binding = ItemDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DepartmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val department = departmentList[position]
        holder.binding.tvDepartmentId.text = department.deptName
        holder.binding.tvDepartmentName.text = "ID: ${department.deptId}"

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(department)
        }
    }

    override fun getItemCount(): Int = departmentList.size
}