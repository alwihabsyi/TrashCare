package com.upnvjt.trashcare.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.databinding.ListItemArticleBinding
import com.upnvjt.trashcare.ui.data.Article

class RvArticlesHomeAdapter(private val data: ArrayList<Article>): RecyclerView.Adapter<RvArticlesHomeAdapter.ArticlesHomeViewHolder>() {
    inner class ArticlesHomeViewHolder(val binding: ListItemArticleBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Article) {
            binding.ivArticle.setImageResource(data.img!!)
            binding.titleArticle.text = data.title
            binding.author.text = data.author
            binding.date.text = data.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesHomeViewHolder {
        val binding = ListItemArticleBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ArticlesHomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ArticlesHomeViewHolder, position: Int) {
        holder.bind(data[position])
    }
}