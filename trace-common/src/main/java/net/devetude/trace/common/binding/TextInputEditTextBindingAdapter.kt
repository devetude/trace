package net.devetude.trace.common.binding

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import net.devetude.trace.common.binding.sam.OnTextChangedListener

@BindingAdapter(value = ["onTextChanged"])
fun TextInputEditText.onTextChanged(listener: OnTextChangedListener) =
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            listener.onTextChanged(s.toString())
    })
