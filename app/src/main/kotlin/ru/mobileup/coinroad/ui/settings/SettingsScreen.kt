package ru.mobileup.coinroad.ui.settings

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_PRIVACY_SETTINGS
import android.provider.Settings.ACTION_SETTINGS
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.androidplot.util.PixelUtils
import com.jraska.falcon.Falcon
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.showAlignBottom
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenSettingsBinding
import ru.mobileup.coinroad.domain.common.Periods
import ru.mobileup.coinroad.domain.common.Settings.Companion.MAX_CHART_HEIGHT
import ru.mobileup.coinroad.domain.common.Settings.Companion.MIN_CHART_HEIGHT
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.ui.graph.ChartInitActivity
import ru.mobileup.coinroad.ui.graph.CustomChartView
import ru.mobileup.coinroad.ui.graph.toPreviewGraphItem
import ru.mobileup.coinroad.ui.graph_creating.PreviewContent
import ru.mobileup.coinroad.util.formatTime
import ru.mobileup.coinroad.util.helper.updateSystemUI
import ru.mobileup.coinroad.util.startWork
import ru.mobileup.coinroad.util.system.dataOrNull
import ru.mobileup.coinroad.util.ui.CubicBezierInterpolator
import ru.mobileup.coinroad.util.ui.createShowCases
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.screenLocationSafe
import kotlin.math.max


class SettingsScreen : BaseScreen<SettingsViewModel>(R.layout.screen_settings) {

    companion object {
        private const val SWITCHER_ANIMATE_DURATION = 300L
        private const val SAMSUNG_MANUFACTURER = "samsung"
    }

    lateinit var layoutResult: ActivityResultLauncher<Intent>

    private val binding by viewBinding(ScreenSettingsBinding::bind)
    override val vm: SettingsViewModel by viewModel()

    private val changePeriodShowCase by lazy {
        requireActivity().createShowCases(
            titleText = resources.getText(R.string.showcase_period_change),
            actionText = resources.getText(R.string.showcase_got_it),
            orientation = ArrowOrientation.TOP,
            arrowPosition = 0.1f,
            emsText = 10,
            preferenceName = "change_period_couching",
        )
    }

    private val notificationsShowCase by lazy {
        requireActivity().createShowCases(
            titleText = resources.getText(R.string.showcase_notifications),
            actionText = resources.getText(R.string.showcase_got_it),
            orientation = ArrowOrientation.BOTTOM,
            arrowPosition = 0.1f,
            emsText = 10,
            preferenceName = "notifications_couching",
            imageRes = R.drawable.lock_screen_showcase
        ) { showChangePeriodShowcase() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                cancelNotificationChannel()
                workManager.startWork()
            }
        }

        initViews()
    }

    private fun initViews() = with(binding) {

        layoutAlertSensitiveItem.titleText.setText(R.string.settings_alert_sensitive)

        with(layoutAlertSensitiveItem.spinner) {
            adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.alertSensitives)
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    vm.onAlertSensitiveChanged(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        with(layoutPeriodItem) {
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(view: SeekBar?, value: Int, user: Boolean) {
                    vm.updatePeriod = Periods.getPeriod(value)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    vm.onUpdatePeriodChanged(Periods.getPeriod(seekBar.progress))
                }
            })
        }

        with(layoutHeightItem) {
            seekBar.max = MAX_CHART_HEIGHT - MIN_CHART_HEIGHT
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(view: SeekBar?, value: Int, user: Boolean) {
                    vm.chartHeight = value + MIN_CHART_HEIGHT
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    vm.onChartHeightChanged(seekBar.progress + MIN_CHART_HEIGHT)
                }
            })
        }

        vm::previewGraphContent bind { content ->
            if (content is PreviewContent.PreviewGraph) {
                graphPlot.updatePlot(CustomChartView.PREVIEW_ID, content.graph.toPreviewGraphItem())
            }
        }

        vm::settingsState bind {
            it.dataOrNull?.run {
                layoutAlertSensitiveItem.spinner.setSelection(alertSensitive)
                layoutHeightItem.seekBar.progress = chartHeight - MIN_CHART_HEIGHT

                with(layoutPeriodItem) {
                    this.seekBar.progress = Periods.values().map { it.value }.indexOf(updatePeriod)
                }

                with(layoutThemeItem) {
                    vm.screenshot?.let {
                        vm.screenshot = null
                        switchView.screenLocationSafe { x, y -> startCircularAnimation(it, x, y) }
                        updateSystemUI(isNightMode)
                    }

                    with(switchView) {
                        isChecked = isNightMode
                        jumpDrawablesToCurrentState()
                        setOnCheckedChangeListener { _, isChecked ->
                            postDelayed({ onThemeChanging(isChecked) }, SWITCHER_ANIMATE_DURATION)
                        }
                        root.doOnClick { isChecked = !isNightMode }
                    }

                    val themes = resources.getStringArray(R.array.applicationThemes)
                    titleText.text = getString(
                        R.string.settings_app_theme,
                        if (isNightMode) themes[0] else themes[1]
                    )
                }
            }
        }

        vm::updatePeriod bind {
            layoutPeriodItem.titleText.text = getString(R.string.settings_period, formatTime(it))
        }

        vm::chartHeight bind {
            layoutHeightItem.titleText.text = getString(R.string.settings_height, it.toString())
            graphView.layoutParams = (graphView.layoutParams as LayoutParams).apply {
                height = PixelUtils.dpToPix(it.toFloat()).toInt()
            }
        }

        showNotificationsShowcase()

        toolbar.setNavigationOnClickListener { vm.navigateBack() }

        with(bottomView) {
            primaryButton.setText(R.string.home_about)
            primaryButton.doOnClick { vm.onAboutClicked() }
        }

        notificationSettings.doOnClick { root.context.openSysNotificationSettings() }

        vm.onRestartWm bind { restartWm() }

        graphPlot.setOnTouchListener { _, _ -> true }
    }

    private fun updateSystemUI(isNightMode: Boolean) {
        requireActivity().updateSystemUI(isNightMode)
        restartWm()
    }

    private fun restartWm() {
        layoutResult.launch(Intent(requireContext(), ChartInitActivity::class.java))
    }

    private fun onThemeChanging(isChecked: Boolean) {
        val activity = activity ?: return
        val windowBitmap = Falcon.takeScreenshotBitmap(activity)
        val statusBarHeight = getBarHeight("status_bar_height")
        val navBarHeight = getBarHeight("navigation_bar_height")
        vm.screenshot = Bitmap.createBitmap(
            windowBitmap,
            0,
            statusBarHeight,
            windowBitmap.width,
            windowBitmap.height - statusBarHeight - navBarHeight,
            null,
            true
        )
        vm.onThemeChanged(isChecked)
    }

    private fun startCircularAnimation(bitmap: Bitmap, centerX: Int, centerY: Int) = with(binding) {
        contentView.setImageBitmap(bitmap)
        contentView.scaleType = ImageView.ScaleType.MATRIX
        contentView.visibility = View.VISIBLE

        // Final radius is approximated here
        val endRadius = max(bitmap.height, bitmap.width).toFloat()
        ViewAnimationUtils.createCircularReveal(contentView, centerX, centerY, endRadius, 0f)
            .apply {
                duration = 1000
                interpolator = CubicBezierInterpolator.EASE_IN_OUT_QUAD
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator?) {
                        contentView.setImageDrawable(null)
                        contentView.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationStart(animation: Animator?) {}
                })
            }
            .start()
    }

    private fun getBarHeight(name: String): Int {
        var result = 0
        val height = resources.getIdentifier(name, "dimen", "android")
        if (height > 0) {
            result = resources.getDimensionPixelSize(height)
        }
        return result
    }

    private fun Context.openSysNotificationSettings() {
        startActivity(Intent().apply {
            action = if (Build.MANUFACTURER == SAMSUNG_MANUFACTURER) {
                ACTION_SETTINGS
            } else ACTION_PRIVACY_SETTINGS
        })
    }

    private fun showChangePeriodShowcase() = with(binding.layoutPeriodItem) {
        seekBar.showAlignBottom(
            balloon = changePeriodShowCase,
            xOff = 0,
            yOff = -resources.getDimension(R.dimen.medium_gap).toInt()
        )
    }

    private fun showNotificationsShowcase() = with(binding.notificationSettings) {
        showAlignBottom(
            balloon = notificationsShowCase,
            xOff = 0,
            yOff = resources.getDimension(R.dimen.big_gap).toInt()
        )
    }
}