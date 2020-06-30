package net.devetude.trace.common.binding

import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.devetude.trace.common.binding.sam.OnItemSelectedListener

@BindingAdapter(value = ["onItemSelected"])
fun BottomNavigationView.onItemSelected(listener: OnItemSelectedListener) =
    setOnNavigationItemSelectedListener {
        listener.onItemSelected(it.itemId)
        return@setOnNavigationItemSelectedListener true
    }
