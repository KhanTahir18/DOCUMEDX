package com.example.documedx.organization

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.documedx.Department
import com.example.documedx.Staff
import com.example.documedx.databinding.AddStaffToDeptItemsBinding
import com.example.documedx.databinding.ItemDepartmentBinding
import com.example.documedx.organization.DepartmentAdapter.DepartmentViewHolder
import com.example.documedx.staff.StaffDashboardActivity

class StaffAdapter(
    private val staffList: List<Staff>,
    private val onDeleteClick: (Staff) -> Unit // <-- Add this line
): RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    inner class StaffViewHolder(val binding: AddStaffToDeptItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val binding = AddStaffToDeptItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StaffViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = staffList[position]
        holder.binding.staffId.text = "ID: ${staff.employeeId}"
        holder.binding.staffName.text = "${staff.firstName} ${staff.lastName}"

        holder.binding.deleteBtn.setOnClickListener {
            onDeleteClick(staff)
        }

    }
    override fun getItemCount(): Int = staffList.size
}