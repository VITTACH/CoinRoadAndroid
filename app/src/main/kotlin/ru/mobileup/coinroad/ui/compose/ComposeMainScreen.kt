package ru.mobileup.coinroad.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnLifecycleDestroyed
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.ui.compose.core.ui.ComponentFactory
import ru.mobileup.coinroad.ui.compose.core.ui.theme.AppTheme
import ru.mobileup.coinroad.ui.compose.main.createMainComponent
import ru.mobileup.coinroad.ui.compose.main.ui.MainUi
import ru.mobileup.coinroad.ui.base.BaseScreen

class ComposeMainScreen : BaseScreen<ComposeMainViewModel>(contentLayoutId = 0) {

    override val vm: ComposeMainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = with(requireActivity()) {
        val factory = ComponentFactory(getKoin(), activity = this)
        lifecycle.asEssentyLifecycle().doOnDestroy { factory.closeScope() }

        val mainComponent = factory.createMainComponent(
            DefaultComponentContext(lifecycle, onBackPressedDispatcher = onBackPressedDispatcher)
        )

        return ComposeView(this).apply {
            setViewCompositionStrategy(DisposeOnLifecycleDestroyed(viewLifecycleOwner))

            setContent {
                AppTheme {
                    MainUi(mainComponent)
                }
            }
        }
    }

    override fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}