package com.sounekatlogo.sounekat.ui.ui.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryModel(
    var uid: String? = null,
    var category: String? = null,
    var image: String? = null,
) : Parcelable