package com.housemanagement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.housemanagement.R
import com.housemanagement.controllers.IndicationsFragmentController
import com.housemanagement.controllers.PaymentsFragmentController
import com.housemanagement.databinding.FragmentIndicationsBinding
import com.housemanagement.databinding.FragmentPaymentsBinding

class PaymentsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    lateinit var binding: FragmentPaymentsBinding
    private lateinit var paymentsFragmentController: PaymentsFragmentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentsBinding.inflate(inflater, container, false)
        paymentsFragmentController = PaymentsFragmentController(binding, this, viewLifecycleOwner)
        return binding.root
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): PaymentsFragment {
            val fragment = PaymentsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Начисления"
    }
}