package com.upnvjt.trashcare.ui.main.tacycle.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus
import com.upnvjt.trashcare.data.tacycle.TacycleModel
import com.upnvjt.trashcare.data.tacycle.getCycleStatus
import com.upnvjt.trashcare.databinding.ItemTacycleLayoutBinding

class TacycleCartAdapter: RecyclerView.Adapter<TacycleCartAdapter.TacycleCartViewHolder>() {

    inner class TacycleCartViewHolder(private val binding: ItemTacycleLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TacycleModel) {
            binding.apply {
                tvIdPesanan.text = item.orderId
                tvStatusPesanan.text = when (getCycleStatus(item.statusOrder)) {
                    is TaCycleStatus.OnGoing -> "On Process"
                    else -> "Done"
                }
                tvAlamatUser.text = item.lokasiPengambilan.namaJalan
                tvWaktuPengambilan.text = item.waktuPengambilan

                var text = ""
                item.jenisSampah.forEach {
                    text += if (item.jenisSampah.last() == it) {
                        it
                    } else {
                        "$it, "
                    }
                }

                tvJenisSampah.text = "Jenis Sampah: $text"
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<TacycleModel>() {
        override fun areItemsTheSame(oldItem: TacycleModel, newItem: TacycleModel): Boolean {
            return oldItem.jenisSampah == newItem.jenisSampah
        }

        override fun areContentsTheSame(oldItem: TacycleModel, newItem: TacycleModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TacycleCartViewHolder {
        return TacycleCartViewHolder(
            ItemTacycleLayoutBinding.inflate(
                LayoutInflater.from(parent.context), null, false
            )
        )
    }

    override fun onBindViewHolder(holder: TacycleCartViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size
}