package com.sounekatlogo.sounekat.ui.ui.category

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sounekatlogo.sounekat.R
import com.sounekatlogo.sounekat.databinding.ActivityCategoryAddEditBinding
import com.sounekatlogo.sounekat.ui.HomepageActivity

class CategoryAddEditActivity : AppCompatActivity() {

    private var _binding : ActivityCategoryAddEditBinding? = null
    private val binding get() = _binding!!
    /// variable untuk menampung gambar dari galeri handphone
    private var image: String? = null

    /// variable untuk permission ke galeri handphone
    private val REQUEST_IMAGE_GALLERY = 1001
    private var option: String? = null
    private var model : CategoryModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCategoryAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)


        option = intent.getStringExtra(OPTION)
        if(option == "add") {
            binding.saveBtn.visibility = View.VISIBLE
            binding.addEdit.text = "Add Foto"
        } else {
            model = intent.getParcelableExtra(EXTRA_DATA)
            binding.update.visibility = View.VISIBLE
            binding.delete.visibility = View.VISIBLE
            binding.addEdit.text = "Edit Foto"
            image = model?.image
            Glide.with(this)
                .load(image)
                .into(binding.roundedImageView)

            binding.category.setText(model?.category)
        }

        binding.addEdit.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_IMAGE_GALLERY)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.update.setOnClickListener {
            formValidation()
        }

        binding.saveBtn.setOnClickListener {
            formValidation()
        }

        binding.delete.setOnClickListener {
            showAlertDialog()
        }

    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menghapus Kategori")
            .setMessage("Apakah anda yakin ingin menghapus kategori ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                FirebaseFirestore
                    .getInstance()
                    .collection("category")
                    .document(model?.uid!!)
                    .delete()
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            showSuccessDialogDelete()
                        } else {
                            showFailureDialogDelete()
                        }
                    }
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun showFailureDialogDelete() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Menghapus Kategori")
            .setMessage("Ups, sepertinya koneksi internet anda kurang stabil, silahkan coba beberapa saat lagi!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialogDelete() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Menghapus Kategori")
            .setMessage("Berhasil!")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    private fun formValidation() {
        val category = binding.category.text.toString().trim()

        if (image == null) {
            Toast.makeText(this, "Photo tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        } else if (category.isEmpty()) {
            Toast.makeText(this, "Kategori tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        }
        else {
            binding.progressBar.visibility = View.VISIBLE

            if (intent.getStringExtra(OPTION) == "add") {
                val uid = System.currentTimeMillis().toString()
                val data = mapOf(
                    "uid" to uid,
                    "category" to category,
                    "image" to image,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("category")
                    .document(uid)
                    .set(data)
                    .addOnCompleteListener {
                        binding.progressBar.visibility = View.GONE
                        if (it.isSuccessful) {
                            binding.category.setText("")
                            image = null
                            showSuccessDialog()
                        } else {
                            showFailureDialog()
                        }
                    }


            } else {
                val data = mapOf(
                    "category" to category,
                    "image" to image,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("category")
                    .document(model?.uid!!)
                    .update(data)
                    .addOnCompleteListener {
                        binding.progressBar.visibility = View.GONE
                        if (it.isSuccessful) {
                            binding.category.setText("")
                            image = null

                            Toast.makeText(this, "Berhasil mengedit kategori!", Toast.LENGTH_SHORT).show()
                            onBackPressed()

                        } else {
                            showFailureDialog()
                        }
                    }
            }
        }
    }

    /// ini adalah program untuk menambahkan gambar kedalalam halaman ini
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                uploadImageToDatabase(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private fun uploadImageToDatabase(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()


        val imageFileName = "photo/image_" + System.currentTimeMillis() + ".png"
        /// proses upload gambar ke databsae
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri ->

                        /// proses upload selesai, berhasil
                        mProgressDialog.dismiss()
                        image = uri.toString()
                        Glide.with(this)
                            .load(image)
                            .into(binding.roundedImageView)
                        }

                    }

                    /// proses upload selesai, gagal
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Gagal mengunggah gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            /// proses upload selesai, gagal
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Gagal mengunggah gambar",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Mengunggah Kategori")
            .setMessage("Ups, sepertinya koneksi internet anda kurang stabil!, silahkan coba beberapa saat lagi")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Mengunggah Kategori Baru!")
            .setMessage("Kategori akan segera terbit di halaman utama")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val OPTION = "option"
        const val EXTRA_DATA = "data"
    }
}