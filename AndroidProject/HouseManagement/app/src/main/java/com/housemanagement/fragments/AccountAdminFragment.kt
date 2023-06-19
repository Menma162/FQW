package com.housemanagement.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.housemanagement.R
import com.housemanagement.controllers.AccountAdminFragmentController
import com.housemanagement.controllers.AccountFragmentController
import com.housemanagement.databinding.FragmentAccountAdminBinding
import com.housemanagement.databinding.FragmentAccountBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountAdminFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAccountAdminBinding
    private lateinit var accountAdminFragmentController: AccountAdminFragmentController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountAdminBinding.inflate(inflater, container, false)
        accountAdminFragmentController = AccountAdminFragmentController(binding, this, viewLifecycleOwner)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountAdminFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "ПРОФИЛЬ"
    }
}