package com.sounekatlogo.sounekat.ui.ui.gallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.sounekatlogo.sounekat.databinding.ActivityGalleryBinding
import com.sounekatlogo.sounekat.ui.ui.category.CategoryModel

class GalleryActivity : AppCompatActivity() {

    private var _binding : ActivityGalleryBinding? = null
    private val binding get() = _binding!!
    private var categoryList = ArrayList<CategoryModel>()
    private var horizontalAdapter : GalleryHorizontalAdapter?  =null
    private var rectangleAdapter : GalleryRectangleAdapter? = null
    private var category = ""

    override fun onResume() {
        super.onResume()

        category = intent.getStringExtra(OPTION) ?: "all"
        categoryList = intent.getParcelableArrayListExtra(CATEGORY)!!

        initRecyclerViewHorizontal()
        initViewModelHorizontal()

        initRecyclerViewVertical()
        initViewModelVertical()
    }

    private fun initRecyclerViewHorizontal() {
        val linearLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvHorizontal.layoutManager = linearLayoutManager
        rectangleAdapter = GalleryRectangleAdapter(categoryList)
        binding.rvHorizontal.adapter = rectangleAdapter
    }

    private fun initViewModelHorizontal() {
        val viewModel = ViewModelProvider(this)[GalleryRectangleViewModel::class.java]

        binding.horizontalProgressBar.visibility = View.VISIBLE
        if(category != "all") {
            viewModel.setImageByCategory(category)
        } else {
            viewModel.setImage()
        }
        viewModel.getImage().observe(this) { photoList ->
            if (photoList.size > 0) {
                binding.noDataHorizontal.visibility = View.GONE
                rectangleAdapter!!.setData(photoList)
            } else {
                binding.noDataHorizontal.visibility = View.VISIBLE
            }
            binding.horizontalProgressBar.visibility = View.GONE
        }
    }

    private fun initRecyclerViewVertical() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        binding.rvVertical.layoutManager = linearLayoutManager
        horizontalAdapter = GalleryHorizontalAdapter(categoryList)
        binding.rvVertical.adapter = horizontalAdapter
    }

    private fun initViewModelVertical() {
        val viewModel = ViewModelProvider(this)[GalleryHorizontalViewModel::class.java]

        binding.verticalProgressBar.visibility = View.VISIBLE
        if(category != "all") {
            viewModel.setImageByCategory(category)
        } else {
            viewModel.setImage()
        }
        viewModel.getImage().observe(this) { photoList ->
            if (photoList.size > 0) {
                binding.noDataVertical.visibility = View.GONE
                horizontalAdapter!!.setData(photoList)
            } else {
                binding.noDataVertical.visibility = View.VISIBLE
            }
            binding.verticalProgressBar.visibility = View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvVertical.isNestedScrollingEnabled = false
        checkIsAdmin()



        binding.addCategory.setOnClickListener {
            val intent = Intent(this, GalleryAddEditActivity::class.java)
            intent.putExtra(GalleryAddEditActivity.OPTION, "add")
            intent.putExtra(GalleryAddEditActivity.CATEGORY, categoryList)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkIsAdmin() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            binding.addCategory.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val CATEGORY = "category"
        const val OPTION = "option"
    }
}