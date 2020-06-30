package net.devetude.trace.common.extension

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.naver.maps.map.MapFragment

fun FragmentManager.findMapFragmentById(@IdRes idRes: Int): MapFragment? =
    findFragmentById(idRes) as? MapFragment
