package com.upnvjt.trashcare.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.databinding.SlideItemContainerBinding

class OnBoardingAdapter: RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    private var onBoardingItems: MutableList<OnBoardingItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding = SlideItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return OnBoardingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return onBoardingItems.size
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        with(holder) {
            with(onBoardingItems[position]) {
                binding.textTitle.text = this.title
                binding.imgSlideIcon.setImageResource(this.image)
            }
        }

    }

    fun setNewItem(newDataSet: List<OnBoardingItem>) {
        onBoardingItems.clear()
        onBoardingItems.addAll(newDataSet)
        this.notifyDataSetChanged()
    }

    inner class OnBoardingViewHolder(val binding: SlideItemContainerBinding):RecyclerView.ViewHolder(binding.root)
}