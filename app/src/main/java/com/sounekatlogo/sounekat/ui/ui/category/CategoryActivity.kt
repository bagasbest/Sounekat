package com.sounekatlogo.sounekat.ui.ui.category

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sounekatlogo.sounekat.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {



    private var _binding : ActivityCategoryBinding? = null
    private val binding get() = _binding!!
    private var adapter : CategoryAdapter? = null

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addCategory.setOnClickListener {
            val intent = Intent(this, CategoryAddEditActivity::class.java)
            intent.putExtra(CategoryAddEditActivity.OPTION, "add")
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        binding.rvCategory.layoutManager = linearLayoutManager
        adapter = CategoryAdapter("category")
        binding.rvCategory.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE
        viewModel.setCategory()
        viewModel.getCategory().observe(this) { category ->
            if (category.size > 0) {
                binding.noData.visibility = View.GONE
                adapter!!.setData(category)
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}