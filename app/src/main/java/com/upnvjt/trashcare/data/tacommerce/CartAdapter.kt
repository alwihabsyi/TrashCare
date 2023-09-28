package com.upnvjt.trashcare.data.tacommerce

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.databinding.ListProductCartBinding
import com.upnvjt.trashcare.util.glide
import com.upnvjt.trashcare.util.toPrice

class CartAdapter : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var allSelected: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    fun allSelected(b: Boolean) {
        allSelected = b
        notifyDataSetChanged()
    }

    inner class CartViewHolder(val binding: ListProductCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Cart) {
            binding.apply {
                tvProductTitle.text = item.product.name
                tvPriceProduct.text = item.product.price.toPrice()
                ivProduct.glide(item.product.photoUrl)
                tvAmount.text = item.quantity.toString()

                if (allSelected) {
                    checkbox.isChecked = true
                    checkbox.isEnabled = false
                } else {
                    checkbox.isChecked = false
                    checkbox.isEnabled = true
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.quantity == newItem.quantity
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            ListProductCartBinding.inflate(
                LayoutInflater.from(parent.context), null, false
            )
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)

        check?.invoke(item, holder.binding.checkbox)

        holder.binding.btnIncreaseItem.setOnClickListener {
            onPlusClick?.invoke(item)
        }
        holder.binding.btnDecreaseItem.setOnClickListener {
            onMinusClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onPlusClick: ((Cart) -> Unit)? = null
    var onMinusClick: ((Cart) -> Unit)? = null
    var check: ((Cart, CheckBox) -> Unit)? = null
}