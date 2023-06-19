package com.housemanagement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.housemanagement.R
import com.housemanagement.controllers.AccountFragmentController
import com.housemanagement.controllers.NewsFragmentController
import com.housemanagement.databinding.FragmentAccountBinding
import com.housemanagement.databinding.FragmentHomeBinding

class AccountFragment : Fragment() {
    lateinit var binding: FragmentAccountBinding
    private lateinit var accountFragmentController: AccountFragmentController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val mParam1 = requireArguments().getString(ARG_PARAM1)
            val mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        accountFragmentController = AccountFragmentController(binding, this, viewLifecycleOwner)
        Thread.sleep(200)
        return binding.root
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): AccountFragment {
            val fragment = AccountFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "ПРОФИЛЬ"
    }
}