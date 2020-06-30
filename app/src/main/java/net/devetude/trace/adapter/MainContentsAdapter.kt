package net.devetude.trace.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.devetude.trace.model.MainContentsType

class MainContentsAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment =
        MainContentsType.of(position).fragmentCreator.create()

    override fun getCount(): Int = MainContentsType.values().size
}
