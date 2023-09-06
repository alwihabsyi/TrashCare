package com.upnvjt.trashcare.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.R

class BannerAdapter(
    private val images: List<Int>
): RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
        return BannerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val currentImage = images[position]
        val poster = holder.itemView.findViewById<ImageView>(R.id.ivPoster)
        poster.setImageResource(currentImage)
    }
}