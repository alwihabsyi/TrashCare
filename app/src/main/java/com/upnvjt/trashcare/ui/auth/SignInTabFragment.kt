package com.upnvjt.trashcare.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.databinding.FragmentSigninTabBinding
import com.upnvjt.trashcare.ui.main.MainActivity


class SignInTabFragment : Fragment() {

    private lateinit var binding: FragmentSigninTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSigninTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this.context, MainActivity::class.java))
        }
    }

}