package com.sounekatlogo.sounekat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sounekatlogo.sounekat.R
import com.sounekatlogo.sounekat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { onBackPressed() }

        binding.loginBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if(email.isEmpty()) {
            Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        } else if(password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Kata sandi minimal 6 karakter!", Toast.LENGTH_SHORT).show()
        } else {
            binding.progressBar.visibility = View.VISIBLE
            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    binding.progressBar.visibility = View.GONE
                    if(it.isSuccessful) {
                        showSuccessDialog()
                    } else {
                        showFailureDialog()
                    }
                }
        }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Login Admin")
            .setMessage("Silahkan periksa kembali email & kata sandi anda!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Login Admin")
            .setMessage("Anda dapat menambahkan, mengedit, dan menghapus Photo!")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    private fun register() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    val data = mapOf(
                        "uid" to uid,
                        "email" to email,
                        "password" to password,
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(uid)
                        .set(data)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}