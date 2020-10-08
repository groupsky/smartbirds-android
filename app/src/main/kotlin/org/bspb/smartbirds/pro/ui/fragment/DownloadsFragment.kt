package org.bspb.smartbirds.pro.ui.fragment

import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_downloads.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.adapter.DownloadsAdapter
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.backend.dto.BGAtlasCell
import org.bspb.smartbirds.pro.backend.dto.DownloadsItem
import org.bspb.smartbirds.pro.backend.dto.DownloadsResponse
import org.bspb.smartbirds.pro.prefs.DownloadsPrefs_
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.utils.debugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@EFragment(R.layout.fragment_downloads)
open class DownloadsFragment : Fragment() {

    @Bean
    protected lateinit var backend: Backend

    @Pref
    protected lateinit var prefs: DownloadsPrefs_

    @AfterViews
    fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        emptyView.visibility = View.GONE
        downloadsListView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        downloadsListView.addItemDecoration(dividerItemDecoration)

        val cachedDownloads = prefs.downloads().get()
        if (!TextUtils.isEmpty(cachedDownloads)) {
            val listType = object : TypeToken<List<DownloadsItem?>?>() {}.type
            val downloads = SBGsonParser.createParser().fromJson<List<DownloadsItem>>(cachedDownloads, listType)
            downloads?.apply { downloadsListView.adapter = DownloadsAdapter(this) }
        }

        fetchDownloads()
    }

    private fun fetchDownloads() {
        var call = backend.api().getDownloads("https://dl.dropbox.com/s/zbarvlxrybwwv0w/downloads.json?dl=0")
        call.enqueue(object : Callback<DownloadsResponse> {
            override fun onResponse(call: Call<DownloadsResponse>, response: Response<DownloadsResponse>) {
                response.apply {
                    if (isSuccessful) {
                        body()?.downloads?.apply {
                            downloadsListView.adapter = DownloadsAdapter(this)
                            prefs.downloads().put(SBGsonParser.createParser().toJson(this))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<DownloadsResponse>, t: Throwable) {
                t.printStackTrace()
                debugLog(t.message)
            }
        })
    }

}