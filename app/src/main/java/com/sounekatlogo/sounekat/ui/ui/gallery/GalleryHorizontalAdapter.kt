package com.sounekatlogo.sounekat.ui.ui.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sounekatlogo.sounekat.databinding.ItemHorizontalBinding
import com.sounekatlogo.sounekat.ui.ui.category.CategoryModel

class GalleryHorizontalAdapter(private val categoryList: ArrayList<CategoryModel>) : RecyclerView.Adapter<GalleryHorizontalAdapter.ViewHolder>() {

    private val galleryList = ArrayList<GalleryHorizontalModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<GalleryHorizontalModel>) {
        galleryList.clear()
        galleryList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding : ItemHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: GalleryHorizontalModel) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                title.text = model.title


                cv.setOnClickListener {
                    val photoDetailModel = GalleryDetailModel()
                    photoDetailModel.image = model.image
                    photoDetailModel.type = model.type
                    photoDetailModel.uid = model.uid
                    photoDetailModel.title = model.title
                    photoDetailModel.category = model.category
                    photoDetailModel.description = model.description

                    val intent = Intent(itemView.context, GalleryDetailActivity::class.java)
                    intent.putExtra(GalleryDetailActivity.EXTRA_DATA, photoDetailModel)
                    intent.putExtra(GalleryDetailActivity.CATEGORY, categoryList)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(galleryList[position])
    }

    override fun getItemCount(): Int = galleryList.size
}