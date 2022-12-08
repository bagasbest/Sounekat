package com.sounekatlogo.sounekat.ui.ui.gallery

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sounekatlogo.sounekat.R
import com.sounekatlogo.sounekat.databinding.ActivityGalleryAddEditBinding
import com.sounekatlogo.sounekat.ui.HomepageActivity
import com.sounekatlogo.sounekat.ui.ui.category.CategoryModel

class GalleryAddEditActivity : AppCompatActivity() {

    private var _binding : ActivityGalleryAddEditBinding? = null
    private val binding get() = _binding!!
    /// variable untuk menampung gambar dari galeri handphone
    private var image: String? = null

    /// variable untuk permission ke galeri handphone
    private val REQUEST_IMAGE_GALLERY = 1001
    private var option: String? = null
    private var model : GalleryDetailModel? = null

    private var category : String? = null
    private var type : String? = null
    var categoryList = ArrayList<CategoryModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGalleryAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        categoryList = intent.getParcelableArrayListExtra(CATEGORY)!!
        dropdownType()
        dropdownCategory()


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
            category = model?.category
            type = model?.type
            if(type == "Persegi") {
                binding.rectangles.visibility = View.VISIBLE
                binding.horizontal.visibility = View.INVISIBLE
                Glide.with(this)
                    .load(image)
                    .into(binding.rectangles)
            } else {
                binding.rectangles.visibility = View.INVISIBLE
                binding.horizontal.visibility = View.VISIBLE
                Glide.with(this)
                    .load(image)
                    .into(binding.horizontal)
            }


            binding.title.setText(model?.title)
            binding.description.setText(model?.description)
        }

        binding.addEdit.setOnClickListener {
            if(type != null) {
                ImagePicker.with(this)
                    .galleryOnly()
                    .compress(1024)
                    .start(REQUEST_IMAGE_GALLERY)
            } else {
                Toast.makeText(this, "Silahkan pilih bentuk gambar terlebih dahulu!", Toast.LENGTH_SHORT).show()
            }

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

    private fun dropdownType() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.type, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.type.setAdapter(adapter)
        binding.type.setOnItemClickListener { _, _, _, _ ->
            type = binding.type.text.toString()
            if(type == "Persegi") {
                binding.rectangles.visibility = View.VISIBLE
                binding.horizontal.visibility = View.INVISIBLE
            } else {
                binding.rectangles.visibility = View.INVISIBLE
                binding.horizontal.visibility = View.VISIBLE
            }
        }
    }

    private fun dropdownCategory() {
        val categoryName = ArrayList<String>()
        for(i in categoryList.indices) {
            categoryName.add(categoryList[i].category!!)
        }


        val adapter = ArrayAdapter(
            this,
           android.R.layout.simple_list_item_1,
            categoryName
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.category.setAdapter(adapter)
        binding.category.setOnItemClickListener { _, _, _, _ ->
            category = binding.category.text.toString()
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
                    .collection("image")
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
            .setTitle("Gagal Menghapus Gambar")
            .setMessage("Ups, sepertinya koneksi internet anda kurang stabil, silahkan coba beberapa saat lagi!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialogDelete() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Menghapus Gambar")
            .setMessage("Berhasil!")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val intent = Intent(this, HomepageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun formValidation() {
        val title = binding.title.text.toString().trim()
        val description = binding.description.text.toString().trim()

        if (image == null) {
            Toast.makeText(this, "Photo tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        } else if (title.isEmpty()) {
            Toast.makeText(this, "Title tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        } else if (type == null) {
            Toast.makeText(this, "Bentuk Gambar tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        } else if (category == null) {
            Toast.makeText(this, "Kategori tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        }
        else {
            binding.progressBar.visibility = View.VISIBLE

            if (intent.getStringExtra(OPTION) == "add") {
                val uid = System.currentTimeMillis().toString()
                val data = mapOf(
                    "uid" to uid,
                    "title" to title,
                    "description" to title,
                    "category" to category,
                    "type" to type,
                    "image" to image,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("image")
                    .document(uid)
                    .set(data)
                    .addOnCompleteListener {
                        binding.progressBar.visibility = View.GONE
                        if (it.isSuccessful) {
                            binding.title.setText("")
                            binding.description.setText("")
                            image = null
                            showSuccessDialog()
                        } else {
                            showFailureDialog()
                        }
                    }


            } else {
                val data = mapOf(
                    "title" to title,
                    "description" to title,
                    "category" to category,
                    "type" to type,
                    "image" to image,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("image")
                    .document(model?.uid!!)
                    .update(data)
                    .addOnCompleteListener {
                        binding.progressBar.visibility = View.GONE
                        if (it.isSuccessful) {
                            binding.title.setText("")
                            binding.description.setText("")
                            image = null

                            Toast.makeText(this, "Berhasil mengedit foto!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomepageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
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
                        if(type == "Persegi") {
                            Glide.with(this)
                                .load(image)
                                .into(binding.rectangles)
                        } else {
                            Glide.with(this)
                                .load(image)
                                .into(binding.horizontal)
                        }

                    }

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
            .setTitle("Gagal Mengunggah Foto")
            .setMessage("Ups, sepertinya koneksi internet anda kurang stabil!, silahkan coba beberapa saat lagi")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Mengunggah Foto Baru!")
            .setMessage("Photo akan segera terbit di halaman utama")
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
        const val CATEGORY = "category"
    }
}