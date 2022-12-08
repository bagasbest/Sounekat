package com.sounekatlogo.sounekat.ui.ui.category

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sounekatlogo.sounekat.databinding.ItemCategoryBinding
import com.sounekatlogo.sounekat.ui.ui.gallery.GalleryActivity

class CategoryAdapter(private val screen: String) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private val categoryList = ArrayList<CategoryModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<CategoryModel>) {
        categoryList.clear()
        categoryList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding : ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: CategoryModel) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                category.text = model.category

                cv.setOnClickListener {
                    if(screen == "home") {
                        val intent = Intent(itemView.context, GalleryActivity::class.java)
                        intent.putExtra(GalleryActivity.OPTION, model.category)
                        intent.putExtra(GalleryActivity.CATEGORY, categoryList)
                        itemView.context.startActivity(intent)
                    } else {
                        val intent = Intent(itemView.context, CategoryAddEditActivity::class.java)
                        intent.putExtra(CategoryAddEditActivity.EXTRA_DATA, model)
                        intent.putExtra(CategoryAddEditActivity.OPTION, "edit")
                        itemView.context.startActivity(intent)
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size
}