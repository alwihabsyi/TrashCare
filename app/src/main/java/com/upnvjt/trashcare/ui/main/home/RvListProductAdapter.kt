package com.upnvjt.trashcare.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.databinding.ListItemProductBinding
import com.upnvjt.trashcare.ui.data.Product

class RvListProductAdapter(private val data: ArrayList<Product>): RecyclerView.Adapter<RvListProductAdapter.ListProductViewHolder>() {
    inner class ListProductViewHolder(val binding: ListItemProductBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Product) {
            binding.ivProduct.setImageResource(data.img!!)
            binding.productName.text = data.name
            binding.productPrice.text = "Rp${data.price.toString()}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListProductViewHolder {
        val binding = ListItemProductBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ListProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ListProductViewHolder, position: Int) {
        holder.bind(data[position])
    }
}