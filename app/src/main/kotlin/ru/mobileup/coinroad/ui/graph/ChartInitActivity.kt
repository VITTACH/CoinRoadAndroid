package ru.mobileup.coinroad.ui.graph

import android.os.Bundle
import android.view.WindowManager.LayoutParams.*
import android.widget.FrameLayout.LayoutParams
import androidx.lifecycle.LifecycleOwner
import com.androidplot.util.PixelUtils
import me.aartikov.sesame.property.PropertyObserver
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.ui.BaseActivity
import ru.mobileup.coinroad.util.ui.waitForLayout

class ChartInitActivity : BaseActivity(), PropertyObserver {

    companion object {
        lateinit var plot: CustomChartView
    }

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = this

    private val vm: ChartInitViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chart_init)

        window.addFlags(FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCH_MODAL or FLAG_NOT_TOUCHABLE)

        plot = findViewById(R.id.plotView)

        vm.onChartHeightInited bind {
            val chartHeight = PixelUtils.dpToPix(it.toFloat())
            plot.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, chartHeight.toInt())
            plot.waitForLayout {
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}