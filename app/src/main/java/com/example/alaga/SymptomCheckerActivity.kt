package com.example.alaga

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import com.example.medicalapp.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SymptomCheckerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_symptom_checker)


        val input = findViewById<EditText>(R.id.symptom_input)
        val checkBtn = findViewById<Button>(R.id.check_button)
        val resultText = findViewById<TextView>(R.id.result_text)


        checkBtn.setOnClickListener {
            val symptoms = input.text.toString().trim()

            val prompt = """
                I am a medical app user. I have the following symptoms: $symptoms. 
                Please suggest possible medical conditions based on these symptoms, and indicate how serious they might be
                Respond in plain language. Do not provide treatment.
            """.trimIndent()

            val request = ChatRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    Message("system", "You are a helpful medical assistant. You assist patients by suggesting possible medical conditions based on their symptoms."),
                    Message("user", "I have a headache and fever.")
                )
            )

            val url = "https://api.openai.com/v1/chat/completions"
            Log.d("API Request", "Calling URL: $url")

            OpenAiClient.api.getChatCompletion(request)
                .enqueue(object : Callback<ChatResponse> {
                    override fun onResponse(
                        call: Call<ChatResponse>,
                        response: Response<ChatResponse>
                    ) {
                        if (response.isSuccessful) {
                            val reply = response.body()?.choices?.firstOrNull()?.message?.content
                            resultText.text = reply ?: "No response from OpenAI."
                        } else {
                            resultText.text = "Error: ${response.code()} ${response.message()}"
                            // Log the response body to help with debugging
                            Log.e("OpenAI Response", "Error: ${response.errorBody()?.string()}")
                            Log.e("OpenAI Response", "Error: ${response.code()} - ${response.message()}")
                            Log.e("OpenAI Error Body", response.errorBody()?.string().toString())


                        }

                    }

                    override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                        resultText.text = "Error: ${t.message}"
                        Log.e("OpenAI Failure", "Error: ${t.message}", t)

                    }
                })
        }

    }
}