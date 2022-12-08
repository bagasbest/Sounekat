package com.sounekatlogo.sounekat.ui.ui.gallery

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sounekatlogo.sounekat.R
import com.sounekatlogo.sounekat.databinding.ActivityGalleryDetailBinding
import com.sounekatlogo.sounekat.ui.ui.category.CategoryModel
import java.io.File

class GalleryDetailActivity : AppCompatActivity() {

    private var _binding : ActivityGalleryDetailBinding? = null
    private val binding get() = _binding!!
    private var model : GalleryDetailModel? = null
    private var permission = 0
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        permission = if(it) {
            1
        } else {
            0
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGalleryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        var categoryList = ArrayList<CategoryModel>()
        categoryList = intent.getParcelableArrayListExtra(CATEGORY)!!
        checkIsAdmin()

        if(model?.type == "Persegi") {
            binding.rectangles.visibility = VISIBLE
            Glide.with(this)
                .load(model?.image)
                .into(binding.rectangles)
        } else {
            binding.horizontal.visibility = VISIBLE
            Glide.with(this)
                .load(model?.image)
                .into(binding.horizontal)
        }
        binding.description.text = model?.description

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.edit.setOnClickListener {
            val intent = Intent(this, GalleryAddEditActivity::class.java)
            intent.putExtra(GalleryAddEditActivity.EXTRA_DATA, model)
            intent.putExtra(GalleryAddEditActivity.OPTION, "edit")
            intent.putExtra(GalleryAddEditActivity.CATEGORY, categoryList)
            startActivity(intent)
        }

        binding.delete.setOnClickListener {
            showAlertDialog()
        }

        binding.cardView.setOnClickListener {
            requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            Handler(Looper.getMainLooper()).postDelayed({
                if(permission==1) {
                    downloadImage()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }, 200)
        }

    }

    private fun downloadImage() {
        try {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val imageLink = Uri.parse(model?.image)
            val request = DownloadManager.Request(imageLink)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("image/jpeg")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(model?.title)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+model?.title+".jpg")
            downloadManager.enqueue(request)
            Toast.makeText(this, "Photo berhasil di download", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("tag", e.message.toString())
            Toast.makeText(this, "Photo gagal di download", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menghapus Photo Ini")
            .setMessage("Apakah anda yakin ingin menghapus photo ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deletePhoto()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun deletePhoto() {
        FirebaseFirestore
            .getInstance()
            .collection("image")
            .document(model?.uid!!)
            .delete()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    showSuccessDialog()
                } else {
                    showFailureDialog()
                }
            }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Menghapus Photo")
            .setMessage("Ups, sepertinya koneksi internet anda kurang stabil, silahkan coba beberapa saat lagi!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Menghapus Photo")
            .setMessage("Berhasil!")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    private fun checkIsAdmin() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            binding.edit.visibility = VISIBLE
            binding.delete.visibility = VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
        const val CATEGORY = "category"
    }
}