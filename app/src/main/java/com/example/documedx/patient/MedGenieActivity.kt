package com.example.documedx.patient

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.documedx.R
import com.example.documedx.databinding.ActivityMedgenieBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class MedGenieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedgenieBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var generativeModel: GenerativeModel
    private var hasShownDisclaimer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedgenieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupGemini()
        setupClickListeners()
        showWelcomeMessage()
    }

    private fun setupToolbar() {
        supportActionBar?.hide()
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(this@MedGenieActivity)
            adapter = chatAdapter
        }
    }

    private fun setupGemini() {
        val apiKey = getString(R.string.gemini_api_key)
        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey
        )
    }

    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                if (!hasShownDisclaimer) {
                    showMedicalDisclaimer {
                        sendMessage(message)
                    }
                } else {
                    sendMessage(message)
                }
            }
        }
    }

    private fun showWelcomeMessage() {
        val welcomeMessage = ChatMessage(
            message = "Hello! I'm MedGenie, your AI health assistant. I can help with general health questions, medication information, first aid guidance, and appointment preparation. How can I assist you today?",
            isUser = false
        )
        chatAdapter.addMessage(welcomeMessage)
        scrollToBottom()
    }

    private fun showMedicalDisclaimer(onAccepted: () -> Unit) {
        val disclaimer = "Welcome to MedGenie! I'm your AI health assistant, designed to provide general health information and educational guidance. Please remember that I am an artificial intelligence and cannot replace the expertise and judgment of qualified healthcare professionals. For any medical concerns, symptoms, or health decisions, always consult with your doctor or healthcare provider. In case of medical emergencies, contact emergency services immediately."

        AlertDialog.Builder(this)
            .setTitle("Medical Disclaimer")
            .setMessage(disclaimer)
            .setIcon(R.drawable.ic_genie)
            .setPositiveButton("I Understand") { _, _ ->
                hasShownDisclaimer = true
                onAccepted()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun sendMessage(message: String) {
        // Add user message
        val userMessage = ChatMessage(message, isUser = true)
        chatAdapter.addMessage(userMessage)
        scrollToBottom()

        // Clear input
        binding.etMessage.text?.clear()

        // Show loading
        binding.progressLoading.visibility = View.VISIBLE

        // Get AI response
        getGeminiResponse(message)
    }

    private fun getGeminiResponse(userMessage: String) {
        lifecycleScope.launch {
            try {
                val medicalPrompt = createMedicalPrompt(userMessage)
                val response = generativeModel.generateContent(medicalPrompt)
                val aiResponse = response.text ?: "I'm sorry, I couldn't process your request right now."

                // Hide loading
                binding.progressLoading.visibility = View.GONE

                // Add AI response
                val botMessage = ChatMessage(aiResponse, isUser = false)
                chatAdapter.addMessage(botMessage)
                scrollToBottom()

            } catch (e: Exception) {
                binding.progressLoading.visibility = View.GONE

                val errorMessage = ChatMessage(
                    "I'm experiencing some technical difficulties. Please try again later or consult a healthcare professional for immediate assistance.",
                    isUser = false
                )
                chatAdapter.addMessage(errorMessage)
                scrollToBottom()
            }
        }
    }

    private fun createMedicalPrompt(userMessage: String): String {
        return """
            You are MedGenie, a helpful AI health assistant. Provide general health information and educational content only.
            
            IMPORTANT GUIDELINES:
            - Always emphasize consulting healthcare professionals for medical advice
            - Provide general information, not diagnoses
            - For emergency symptoms, immediately advise contacting emergency services
            - Be supportive but professional
            - Focus on: general health Q&A, medication information, first aid basics, health tips, appointment preparation
            
            User question: $userMessage
            
            Respond helpfully while maintaining medical safety standards.
        """.trimIndent()
    }

    private fun scrollToBottom() {
        if (messages.isNotEmpty()) {
            binding.recyclerChat.smoothScrollToPosition(messages.size - 1)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}