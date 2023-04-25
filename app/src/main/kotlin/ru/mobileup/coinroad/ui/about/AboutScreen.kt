package ru.mobileup.coinroad.ui.about

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.appbar.AppBarLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.BuildConfig
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenAboutBinding
import ru.mobileup.coinroad.ui.about.AboutViewModel.Companion.LANDING_PAGE_URL
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.util.ui.doOnClick
import java.time.Year
import kotlin.math.abs

class AboutScreen : BaseScreen<AboutViewModel>(R.layout.screen_about) {

    private val binding by viewBinding(ScreenAboutBinding::bind)
    override val vm: AboutViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { vm.navigateBack() }
        appInfo.text = getString(R.string.about_app_info, BuildConfig.VERSION_NAME)
        credits.text = getString(R.string.about_credits, Year.now().value)

        with(bottomView.primaryButton) {
            setText(R.string.about_mobileup)
            doOnClick { vm.onMobileUpClicked() }
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_24_out, 0)
        }

        setupWebView()

        customWebView.loadUrl(LANDING_PAGE_URL)

        appbarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(layout: AppBarLayout, state: State) {
            }
        })
    }

    private fun setupWebView() = with(binding) {
        customWebView.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView, url: String) {
                    super.onPageFinished(webView, url)
                    customWebView.isVisible = true
                }
            }

            webChromeClient = WebChromeClient()

            with(settings) {
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                javaScriptEnabled = true
                allowContentAccess = true
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(true)
            }
        }
    }

    abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {
        enum class State { EXPANDED, COLLAPSED, IDLE }

        private var mState = State.IDLE

        abstract fun onStateChanged(layout: AppBarLayout, state: State)

        override fun onOffsetChanged(layout: AppBarLayout, scrollOffset: Int) = when {
            scrollOffset == 0 -> {
                if (mState != State.EXPANDED) onStateChanged(layout, State.EXPANDED)
                mState = State.EXPANDED
            }
            abs(scrollOffset) >= layout.totalScrollRange -> {
                if (mState != State.COLLAPSED) onStateChanged(layout, State.COLLAPSED)
                mState = State.COLLAPSED
            }
            else -> {
                if (mState != State.IDLE) onStateChanged(layout, State.IDLE)
                mState = State.IDLE
            }
        }
    }
}