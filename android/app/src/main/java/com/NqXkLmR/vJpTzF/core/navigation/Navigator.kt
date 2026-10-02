package com.NqXkLmR.vJpTzF.core.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.NqXkLmR.vJpTzF.R

class Navigator(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerId: Int
) {

    fun setRoot(fragment: Fragment, tag: String) {
        if (fragmentManager.isStateSaved) {
            return
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, fragment, tag)
            .commit()
    }

    fun push(fragment: Fragment, tag: String) {
        if (fragmentManager.isStateSaved) {
            return
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun replaceTopWithFade(fragment: Fragment, tag: String) {
        if (fragmentManager.isStateSaved) {
            return
        }
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun backToRoot() {
        if (fragmentManager.isStateSaved) {
            return
        }
        while (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
    }
}
