package com.example.documedx.organization

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.documedx.Department
import com.example.documedx.Staff
import com.example.documedx.User
import com.example.documedx.databinding.AddStaffToDeptItemsBinding
import com.example.documedx.databinding.ItemDepartmentBinding
import com.example.documedx.databinding.ViewPatientsItemsBinding
import com.example.documedx.organization.DepartmentAdapter.DepartmentViewHolder
import com.example.documedx.staff.StaffDashboardActivity

class PatientInOrgAdapter(
    private val patietList: List<User>,
    private val onDeleteClick: (User) -> Unit // <-- Add this line
): RecyclerView.Adapter<PatientInOrgAdapter.PatientViewHolder>() {

    inner class PatientViewHolder(val binding: ViewPatientsItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = ViewPatientsItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val user = patietList[position]
        holder.binding.patientGender.text = "${user.gender}"
        holder.binding.patientName.text = "${user.firstName} ${user.lastName}"

        holder.binding.deleteBtn.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(holder.itemView.context   )
            builder.setTitle("Remove Patient")
            builder.setMessage("Are you sure you want to remove ${user.firstName}?")

            builder.setPositiveButton("Delete") { _, _ ->
                onDeleteClick(user)
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Do nothing, stay on the page
            }

            builder.create().show()
        }

        holder.binding.patientName.setOnClickListener {
            val intent =
                Intent(holder.itemView.context, OrganizationPatientsDetailsActivity::class.java)
            intent.putExtra("patientPhoneNo", user.phoneNo)
            holder.itemView.context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int = patietList.size
}