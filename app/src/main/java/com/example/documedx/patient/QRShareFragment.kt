package com.example.documedx.patient

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.documedx.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRShareFragment : DialogFragment() {

    private lateinit var qrImageView: ImageView
    private lateinit var reportTitle: TextView
    private lateinit var doctorIdInput: EditText
    private lateinit var uploadButton: Button
    private lateinit var closeButton: Button

    private var reportId: String = ""
    private var reportName: String = ""

    companion object {
        fun newInstance(reportId: String, reportName: String): QRShareFragment {
            val fragment = QRShareFragment()
            val args = Bundle()
            args.putString("report_id", reportId)
            args.putString("report_name", reportName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            reportId = it.getString("report_id", "")
            reportName = it.getString("report_name", "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_qr_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupQRCode()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        qrImageView = view.findViewById(R.id.iv_qr_code)
        reportTitle = view.findViewById(R.id.tv_report_title)
        doctorIdInput = view.findViewById(R.id.et_doctor_id)
        uploadButton = view.findViewById(R.id.btn_upload)
        closeButton = view.findViewById(R.id.btn_close)

        reportTitle.text = reportName
    }

    private fun setupQRCode() {
        try {
            // Create QR data with report info for doctor to scan
            val qrData = "DOCUMEDX_REPORT:${reportId}:${System.currentTimeMillis()}"

            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                qrData,
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            qrImageView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            // Keep placeholder image if QR generation fails
            Toast.makeText(context, "Error generating QR code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        uploadButton.setOnClickListener {
            val doctorId = doctorIdInput.text.toString().trim()
            if (doctorId.isEmpty()) {
                Toast.makeText(context, "Please enter Doctor ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            shareReportWithDoctor(doctorId)
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun shareReportWithDoctor(doctorId: String) {
        // TODO: Implement Firebase sharing logic
        // For now, show success message
        Toast.makeText(context, "Report shared with Doctor ID: $doctorId", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}