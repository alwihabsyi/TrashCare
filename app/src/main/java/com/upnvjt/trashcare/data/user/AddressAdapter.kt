package com.upnvjt.trashcare.data.user

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.databinding.ItemAddressBinding

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: UserAddress) {
            binding.apply {
                binding.tvAlamatUser.text = item.judulAlamat
                binding.tvKodePos.text = "ID ${item.kodePos}"
                binding.tvDetailAlamat.text =
                    "${item.namaJalan}, ${item.kelurahan}, ${item.kecamatan}, ${item.kota}, ${item.provinsi}"
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<UserAddress>() {
        override fun areItemsTheSame(oldItem: UserAddress, newItem: UserAddress): Boolean {
            return oldItem.judulAlamat == newItem.judulAlamat
        }

        override fun areContentsTheSame(oldItem: UserAddress, newItem: UserAddress): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            ItemAddressBinding.inflate(
                LayoutInflater.from(parent.context), null, false
            )
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick: ((UserAddress) -> Unit)? = null
}