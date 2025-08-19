package com.example.documedx.organization

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
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
        holder.binding.tvDepartmentId.text = "ID: ${department.deptId}"
        holder.binding.tvDepartmentName.text = department.deptName
        holder.binding.descriptionTextView.text = department.description

        holder.binding.btnDelete.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(holder.itemView.context   )
            builder.setTitle("Confirm Delete")
            builder.setMessage("Are you sure you want to Delete ${department.deptId}?")

            builder.setPositiveButton("Delete") { _, _ ->
                onDeleteClick(department)
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Do nothing, stay on the page
            }

            builder.create().show()
        }

        holder.binding.viewDeptBtn.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ViewStaffActivity::class.java)
            intent.putExtra("deptId", department.deptId)
            intent.putExtra("deptName", department.deptName)
            context.startActivity(intent)
        }
        holder.binding.addStaffBtn.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AddStaffToDeptActivity::class.java)
            intent.putExtra("deptId", department.deptId)
            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int = departmentList.size
}