package com.housemanagement.controllers

import android.R.attr.value
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.housemanagement.R
import com.housemanagement.databinding.FragmentSelectHouseBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.AddAdvertisementFragment
import com.housemanagement.fragments.AdvertisementsAdminFragment
import com.housemanagement.fragments.SelectHouseFragment
import com.housemanagement.models.tables.House
import java.time.format.DateTimeFormatter


class SelectHouseFragmentController {
    private var binding: FragmentSelectHouseBinding
    private var fragment: SelectHouseFragment
    private var db: MainDb
    private var houses = ArrayList<House>()
    private var viewLifecycleOwner: LifecycleOwner
    private lateinit var idHouses: ArrayList<Int>
    constructor(binding: FragmentSelectHouseBinding, fragment: SelectHouseFragment, viewLifecycleOwner: LifecycleOwner){
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        db.getDao().getHouses().asLiveData().observe(viewLifecycleOwner) {
            houses = ArrayList(it.toMutableList())
            initFragment()
        }
    }

    private fun initFragment() {
        binding.buttonBack.setOnClickListener(View.OnClickListener {
            replaceFragment(AdvertisementsAdminFragment())
        })
        binding.btnThen.setOnClickListener(View.OnClickListener {
            idHouses = ArrayList<Int>()
            for (i in 0 until houses.count()) {
                if (binding.listSelect.isItemChecked(i)) idHouses.add(houses[i].id)
            }
            if(idHouses.isEmpty()) binding.message.visibility = View.VISIBLE
            else
            {
                val fragment = AddAdvertisementFragment()
                fragment.instOf(idHouses)
                replaceFragment(fragment)
            }
        })
        binding.message.visibility = View.GONE
        binding.listSelect.choiceMode = ListView.CHOICE_MODE_MULTIPLE;
        fillList()
    }

    private fun fillList() {
        var values = emptyArray<String>()
        for (house in houses){
            values += (house.name)
        }
        val adapter = ArrayAdapter(fragment.requireContext(), android.R.layout.simple_list_item_multiple_choice, values)
        binding.listSelect.adapter = adapter
    }

    private fun replaceFragment(fragmentItem: Fragment){
        val fragmentManager = fragment.fragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frameLayout, fragmentItem)
        fragmentTransaction?.commit()
    }
}