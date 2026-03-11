package com.asagiry.plantdiary.ui.common

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleWhen")
fun bindVisibleWhen(view: View, visible: Boolean?) {
    view.visibility = if (visible == true) View.VISIBLE else View.GONE
}
