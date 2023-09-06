package com.upnvjt.trashcare.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.databinding.FragmentHomeBinding
import com.upnvjt.trashcare.ui.data.Article
import com.upnvjt.trashcare.ui.data.ListProductDummy
import com.upnvjt.trashcare.ui.data.Product
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    lateinit var rvListProduct: RecyclerView
    lateinit var rvArticles: RecyclerView
    private lateinit var bannerViewPager: ViewPager2
    lateinit var rvListProductAdapter: RvListProductAdapter
    lateinit var rvArticlesHomeAdapter: RvArticlesHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //rvListProduct Start
        rvListProduct = binding.rvListProduct
        var data = ArrayList<Product>()
        data.addAll(ListProductDummy.list)
        rvListProductAdapter = RvListProductAdapter(data)

        rvListProduct.layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        rvListProduct.adapter = rvListProductAdapter

        // rvArticles Start
        rvArticles = binding.rvArticles
        var dataArticles = ArrayList<Article>()
        repeat(10) {
            dataArticles.add(Article(
                "Trash Schedule Draws Complaints Over Service, Missed Pickups",
                R.drawable.product_1,
                "20-10-2023",
                "The Indonesian government has launched a program that will pay thousands of traditional fishers to collect plastic trash from the sea. The four-week initiative is part of wider efforts to cut marine plastic waste by 70% by 2025.",
                "Yusan Pr."
            ))
        }
        val images = listOf(
            R.drawable.poster_three,
            R.drawable.poster_one,
            R.drawable.poster_two
        )

        bannerViewPager = binding.viewPager

        val bannerAdapter = BannerAdapter(images)
        bannerViewPager.adapter = bannerAdapter

        val indicator3: CircleIndicator3 = view.findViewById(R.id.circleIndicator3)
        indicator3.setViewPager(bannerViewPager)

        rvArticlesHomeAdapter = RvArticlesHomeAdapter(dataArticles)
        rvArticles.layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        rvArticles.adapter = rvArticlesHomeAdapter

    }
}