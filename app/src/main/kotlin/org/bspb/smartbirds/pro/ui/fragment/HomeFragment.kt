package org.bspb.smartbirds.pro.ui.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Html
import android.text.Html.ImageGetter
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.OptionsItem
import org.androidannotations.annotations.OptionsMenu
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.service.ExportService_
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.ui.MonitoringListActivity_
import org.bspb.smartbirds.pro.ui.StatsActivity_

@EFragment(R.layout.fragment_home)
@OptionsMenu(R.menu.menu_main)
open class HomeFragment : Fragment() {

    private var exportDialog: AlertDialog? = null

    @OptionsItem(R.id.menu_export)
    open fun exportBtnClicked() {
        exportDialog = ProgressDialog.show(activity, getString(R.string.export_dialog_title), getString(R.string.export_dialog_text), true)
        ExportService_.intent(activity).prepareForExport().start()
    }

    @OptionsItem(R.id.menu_browse)
    open fun browseBtnClicked() {
        MonitoringListActivity_.intent(this).start()
    }

    @OptionsItem(R.id.menu_help)
    open fun helpBtnClicked() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(getString(R.string.help_url))
        startActivity(intent)
    }

    @OptionsItem(R.id.menu_information)
    open fun infoBtnClicked() {
        val density = resources.displayMetrics.density
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.info_dialog_title))
        val view = TextView(activity)
        view.setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.text = Html.fromHtml(getString(R.string.info_text), ImageGetter { s ->
            var drawable: Drawable? = null
            when (s) {
                "logo_bspb" -> drawable = resources.getDrawable(R.drawable.logo_bspb)
                "logo_mtel" -> drawable = resources.getDrawable(R.drawable.logo_mtel)
                "life_NEW" -> drawable = resources.getDrawable(R.drawable.logo_life)
                "natura2000_NEW" -> drawable = resources.getDrawable(R.drawable.logo_natura_2000)
            }
            if (drawable == null) {
                Reporting.logException(IllegalArgumentException("Unknown image: $s"))
            } else {
                drawable.setBounds(0, 0, (drawable.intrinsicWidth * density).toInt(),
                        (drawable.intrinsicHeight * density).toInt())
            }
            drawable!!
        }, null)
        builder.setView(view)
        builder.create().show()
    }

    @OptionsItem(R.id.menu_statistics)
    open fun showStats() {
        StatsActivity_.intent(activity).start()
    }

}