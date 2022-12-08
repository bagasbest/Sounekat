package com.sounekatlogo.sounekat.ui.ui.category

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModel : ViewModel() {

    private val photoList = MutableLiveData<ArrayList<CategoryModel>>()
    private val listItems = ArrayList<CategoryModel>()
    private val TAG = CategoryViewModel::class.java.simpleName

    fun setCategory() {
        listItems.clear()

        try {
            FirebaseFirestore
                .getInstance()
                .collection("category")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = CategoryModel()
                            model.uid = "" + document["uid"]
                            model.category = "" + document["category"]
                            model.image = "" + document["image"]

                            listItems.add(model)
                        }
                        photoList.postValue(listItems)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getCategory(): LiveData<ArrayList<CategoryModel>> {
        return photoList
    }

}