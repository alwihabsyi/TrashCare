package com.upnvjt.trashcare.data.tacommerce

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.databinding.ItemTacycleLayoutBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CommerceHistoryAdapter :
    RecyclerView.Adapter<CommerceHistoryAdapter.CommerceHistoryViewHolder>() {

    inner class CommerceHistoryViewHolder(private val binding: ItemTacycleLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Orders) {
            binding.apply {
                tvIdPesanan.text = "ID: ${item.orderId}"
                tvStatusPesanan.text = when (getOrderStatus(item.orderStatus)) {
                    is OrderStatus.OnGoing -> "On Process"
                    is OrderStatus.Declined -> "Declined"
                    else -> "Done"
                }
                tvAlamatUser.text = item.address.namaJalan
                tvWaktuPengambilan.text = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.ENGLISH
                ).format(item.date)

                var text = ""
                item.products.forEach {
                    text += if (item.products.last() == it) {
                        it.product.name
                    } else {
                        "${it.product.name}, "
                    }
                }

                tvJenisSampah.text = "Produk: $text"
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Orders>() {
        override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommerceHistoryViewHolder {
        return CommerceHistoryViewHolder(
            ItemTacycleLayoutBinding.inflate(
                LayoutInflater.from(parent.context), null, false
            )
        )
    }

    override fun onBindViewHolder(holder: CommerceHistoryViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size
}