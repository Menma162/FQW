package com.housemanagement.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.housemanagement.R
import com.housemanagement.controllers.AddAdvertisementFragmentController
import com.housemanagement.controllers.AdvertisementsAdminFragmentController
import com.housemanagement.databinding.FragmentAddAdvertisementBinding
import com.housemanagement.databinding.FragmentAdvertisementsAdminBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class AddAdvertisementFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAddAdvertisementBinding
    private lateinit var addAdvertisementFragmentController: AddAdvertisementFragmentController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

     fun instOf(idHouses: ArrayList<Int>): AddAdvertisementFragment {
        val args = Bundle()
        args.putIntegerArrayList("idHouses", idHouses)
        this.arguments = args
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAdvertisementBinding.inflate(inflater, container, false)
        addAdvertisementFragmentController = AddAdvertisementFragmentController(binding, this, viewLifecycleOwner, this.requireArguments())
        return binding.root
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddAdvertisementFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "ОБЪЯВЛЕНИЯ"
    }
}