package com.sounekatlogo.sounekat.ui.ui.gallery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryRectangleModel(
    var uid : String? = null,
    var title : String? = null,
    var description : String? = null,
    var category : String? = null,
    var type : String? = null,
    var image : String? = null,
) : Parcelable