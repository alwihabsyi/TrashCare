package com.upnvjt.trashcare.data.tacommerce

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.databinding.ListItemProductBinding
import com.upnvjt.trashcare.util.glide
import com.upnvjt.trashcare.util.toPrice

class TaCommerceAdapter(var fromHome: Boolean = false): RecyclerView.Adapter<TaCommerceAdapter.TaCommerceViewHolder>() {

    inner class TaCommerceViewHolder(private val binding: ListItemProductBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Product) {
            binding.apply {
                productName.text = item.name
                productPrice.text = item.price.toPrice()
                ivProduct.glide(item.photoUrl)

                if (fromHome) {
                    val params = (cardView.layoutParams as ViewGroup.MarginLayoutParams)
                    params.marginStart = 0
                    params.bottomMargin = 10
                }
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaCommerceViewHolder {
        return TaCommerceViewHolder(
            ListItemProductBinding.inflate(
                LayoutInflater.from(parent.context), null, false
            )
        )
    }

    override fun onBindViewHolder(holder: TaCommerceViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick: ((Product) -> Unit)? = null
}