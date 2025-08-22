package com.example.documedx.patient

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.documedx.databinding.FragmentShareDialogBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class ShareDialogFragment : DialogFragment() {

    private var _binding: FragmentShareDialogBinding? = null
    private val binding get() = _binding!!

    private var reportId: String? = null
    private var patientId: String? = null

    private var fromOrg: String? = null

    private var reportUrl: String? = null
    private var prescriptionUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismiss()
        }
        reportId = arguments?.getString("reportId")
        patientId = arguments?.getString("patientId")
        fromOrg = arguments?.getString("fromOrg")
        reportUrl = arguments?.getString("reportUrl")

        generateQRCode(reportId.toString(), patientId.toString(), fromOrg.toString(), reportUrl.toString())

    }

    private fun generateQRCode(reportId: String,patientId: String, fromOrg: String, reportUrl: String) {
        try {
            val data = "reportId=$reportId;patientId=$patientId;reportUrl=$reportUrl;fromOrg=$fromOrg"
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE,
                500,
                500
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            binding.ivQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
//        dialog?.window?.setLayout(wra.dpToPx(), 400.dpToPx())
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
