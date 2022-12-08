package com.sounekatlogo.sounekat.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sounekatlogo.sounekat.R
import com.sounekatlogo.sounekat.databinding.ActivityHomepageBinding
import com.sounekatlogo.sounekat.ui.ui.category.CategoryActivity
import com.sounekatlogo.sounekat.ui.ui.category.CategoryAdapter
import com.sounekatlogo.sounekat.ui.ui.category.CategoryModel
import com.sounekatlogo.sounekat.ui.ui.category.CategoryViewModel
import com.sounekatlogo.sounekat.ui.ui.gallery.GalleryActivity


class HomepageActivity : AppCompatActivity() {

    private var _binding: ActivityHomepageBinding? = null
    private val binding get() = _binding!!
    private var adapter: CategoryAdapter? = null
    private var isAdmin = false
    private var categoryList = ArrayList<CategoryModel>()

    override fun onResume() {
        super.onResume()
        checkIsAdmin()
        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        binding.rvCategory.layoutManager = linearLayoutManager
        adapter = CategoryAdapter("home")
        binding.rvCategory.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE
        viewModel.setCategory()
        viewModel.getCategory().observe(this) { category ->
            if (category.size > 0) {
                categoryList.clear()
                categoryList.addAll(category)
                binding.noData.visibility = View.GONE
                adapter!!.setData(category)
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        populateDrawerHeader()
        populateDrawerItem()

        Glide.with(this)
            .load(R.drawable.logo)
            .into(binding.logo)


        /// klik menu icon / ikon menu
        binding.menu.setOnClickListener {
            val navDrawer = binding.drawerLayout
            if (!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(GravityCompat.START) else navDrawer.closeDrawer(
                GravityCompat.END
            )
        }

        binding.loginBtn.setOnClickListener {
            if(!isAdmin) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                showLogoutDialog("btn")
            }
        }

    }

    private fun populateDrawerHeader() {
        val navView = binding.navView
        val hView = navView.getHeaderView(0)

        val image = hView.findViewById<ImageView>(R.id.imageView)
        Glide.with(this)
            .load(R.drawable.sounekat)
            .into(image)
    }

    private fun populateDrawerItem() {
        // Klik navigasi pada drawer
        binding.navView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.category -> {
                    startActivity(Intent(this, CategoryActivity::class.java))
                }
                R.id.image -> {
                    if(categoryList.size > 0) {
                        val intent = Intent(this, GalleryActivity::class.java)
                        intent.putExtra(GalleryActivity.CATEGORY, categoryList)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Anda harus membuat setidaknya 1 kategori foto!", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.logout -> {
                    showLogoutDialog("drawer")
                }
            }
            true
        }
    }

    private fun showLogoutDialog(option: String) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah anda yakin ingin keluar apliaksi ?")
            .setIcon(R.drawable.ic_baseline_exit_to_app_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                // sign out dari firebase autentikasi
                FirebaseAuth.getInstance().signOut()

                binding.menu.visibility = View.GONE
                binding.adminMenuTxt.visibility = View.GONE
                isAdmin = false
                dialogInterface.dismiss()
                if(option == "drawer") {
                    startActivity(Intent(this, HomepageActivity::class.java))
                    finish()
                }

            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun checkIsAdmin() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            binding.adminMenuTxt.visibility = View.VISIBLE
            binding.menu.visibility = View.VISIBLE
            isAdmin = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}