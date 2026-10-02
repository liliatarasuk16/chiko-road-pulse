package com.NqXkLmR.vJpTzF.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.di.AppViewModelFactory
import com.NqXkLmR.vJpTzF.core.navigation.Navigator
import com.NqXkLmR.vJpTzF.databinding.FragmentSplashBinding
import com.NqXkLmR.vJpTzF.presentation.menu.MenuFragment
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var binding: FragmentSplashBinding? = null
    private val viewModel: SplashViewModel by viewModels { AppViewModelFactory() }
    private val animator = SplashAnimator()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentSplashBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        animator.playStripeLoop(
            listOf(views.splashStripeOne, views.splashStripeTwo, views.splashStripeThree)
        )
        animator.playBadgeEntrance(views.splashBadgeFrame)
        animator.playBadgePulse(views.splashBadgeFrame)
        animator.playTitleEntrance(
            views.splashTitleTop,
            views.splashTitleBottom,
            views.splashSubtitle,
            views.splashLoading
        )
        animator.playRuleReveal(views.splashRule)
        observeState()
        viewModel.startTimer()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: SplashUiState) {
        val views = binding ?: return
        views.splashProgress.visibility = if (state.isLoading) View.VISIBLE else View.INVISIBLE
        if (state.navigateToMenu) {
            viewModel.onNavigationHandled()
            goToMenu()
        }
    }

    private fun goToMenu() {
        if (!isAdded) {
            return
        }
        try {
            Navigator(parentFragmentManager, R.id.fragment_container)
                .setRoot(MenuFragment(), MenuFragment.TAG)
        } catch (e: Exception) {
        }
    }

    override fun onDestroyView() {
        animator.cancel()
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "splash"
    }
}
