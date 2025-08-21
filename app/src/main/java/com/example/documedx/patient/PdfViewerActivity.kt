package com.example.documedx.patient

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.documedx.R

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        val pdfUrl = intent.getStringExtra("pdf_url") ?: ""
        val pdfTitle = intent.getStringExtra("pdf_title") ?: "PDF Viewer"

        setupToolbar(pdfTitle)
        initViews()
        loadPdf(pdfUrl)
    }

    private fun setupToolbar(title: String) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
    }

    private fun initViews() {
        webView = findViewById(R.id.webview_pdf)
        progressBar = findViewById(R.id.progress_bar)

        // Configure WebView for PDF viewing
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.setSupportZoom(true)
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = android.view.View.GONE
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this@PdfViewerActivity, "Error loading PDF: $description", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPdf(pdfUrl: String) {
        if (pdfUrl.isNotEmpty()) {
            // Load PDF using Google Docs viewer for better compatibility
            val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
            webView.loadUrl(googleDocsUrl)
            progressBar.visibility = android.view.View.VISIBLE
        } else {
            Toast.makeText(this, "Error: PDF URL not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}