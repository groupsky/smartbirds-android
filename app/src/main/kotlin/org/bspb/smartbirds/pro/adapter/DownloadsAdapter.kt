package org.bspb.smartbirds.pro.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.DownloadsItem
import org.bspb.smartbirds.pro.utils.inflate

class DownloadsAdapter(private val downloads: List<DownloadsItem>) : RecyclerView.Adapter<DownloadsAdapter.DownloadsViewHolder>() {

    class DownloadsViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer, View.OnClickListener {
        private var downloadItem: DownloadsItem? = null

        // TODO Replace with LayoutContainer auto binding when become stable
        private var labelView: TextView = containerView.findViewById(R.id.itemLabel)

        init {
            containerView.setOnClickListener(this)
        }


        fun bindItem(downloadItem: DownloadsItem) {
            this.downloadItem = downloadItem
            labelView.text = downloadItem.title?.get("en")
        }

        override fun onClick(v: View?) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(downloadItem?.url)
            v?.context?.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsViewHolder {
        val inflatedView = parent.inflate(R.layout.item_download, false)
        return DownloadsViewHolder(inflatedView)
    }

    override fun getItemCount() = downloads.size

    override fun onBindViewHolder(holder: DownloadsViewHolder, position: Int) {
        holder.bindItem(downloads[position])
    }
}