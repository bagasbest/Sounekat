package com.sounekatlogo.sounekat.ui.ui.gallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class GalleryRectangleViewModel : ViewModel() {

    private val photoList = MutableLiveData<ArrayList<GalleryRectangleModel>>()
    private val listItems = ArrayList<GalleryRectangleModel>()
    private val TAG = GalleryRectangleViewModel::class.java.simpleName

    fun setImage() {
        listItems.clear()

        try {
            FirebaseFirestore
                .getInstance()
                .collection("image")
                .whereEqualTo("type", "Persegi")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = GalleryRectangleModel()
                            model.uid = "" + document["uid"]
                            model.title = "" + document["title"]
                            model.description = "" + document["description"]
                            model.category = "" + document["category"]
                            model.type = "" + document["type"]
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

    fun getImage(): LiveData<ArrayList<GalleryRectangleModel>> {
        return photoList
    }

    fun setImageByCategory(category: String) {
        listItems.clear()

        try {
            FirebaseFirestore
                .getInstance()
                .collection("image")
                .whereEqualTo("category", category)
                .whereEqualTo("type", "Persegi")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = GalleryRectangleModel()
                            model.uid = "" + document["uid"]
                            model.title = "" + document["title"]
                            model.description = "" + document["description"]
                            model.category = "" + document["category"]
                            model.type = "" + document["type"]
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

}