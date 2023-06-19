package com.housemanagement.controllers

import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.housemanagement.R
import com.housemanagement.adapters.CounterViewAdapter
import com.housemanagement.databinding.FragmentIndicationsBinding
import com.housemanagement.databinding.FragmentStoryIndicationsBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.IndicationsFragment
import com.housemanagement.fragments.StoryIndicationsFragment
import com.housemanagement.models.tables.Counter
import com.housemanagement.models.tables.Flat
import com.housemanagement.models.tables.Indication
import com.housemanagement.models.tables.Payment
import com.housemanagement.models.tables.Service
import java.time.format.DateTimeFormatter

class StoryIndicationsFragmentController {
    private var binding: FragmentStoryIndicationsBinding
    private var fragment: StoryIndicationsFragment
    private var db: MainDb
    private var counters = ArrayList<Counter>()
    private var services = ArrayList<Service>()
    private var flats = ArrayList<Flat>()
    private var indications = ArrayList<Indication>()
    private var viewLifecycleOwner: LifecycleOwner
    constructor(binding: FragmentStoryIndicationsBinding, fragment: StoryIndicationsFragment, viewLifecycleOwner: LifecycleOwner){
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        getData("indications")
    }

    private fun getData(list: String) {
        when (list) {
            "indications" -> db.getDao().getIndications().asLiveData().observe(viewLifecycleOwner) {
                indications = ArrayList(it.toMutableList().sortedByDescending{ it.dateTransfer})
                getData("services")
            }
            "services" -> db.getDao().getServices().asLiveData().observe(viewLifecycleOwner) {
                services = ArrayList(it.toMutableList())
                getData("counters")
            }
            "counters" -> db.getDao().getCounters().asLiveData()
                .observe(viewLifecycleOwner) {
                    counters = ArrayList(it.toMutableList())
                    getData("flats")
                }
            "flats" -> db.getDao().getFlats().asLiveData().observe(viewLifecycleOwner) {
                flats = ArrayList(it.toMutableList())
                initFragment()
            }
        }
    }

    private fun initFragment() {
        binding.buttonBack.setOnClickListener(View.OnClickListener {
            replaceFragment()
        })
        fillListStory()
    }

    private fun fillListStory() {
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        var values = emptyArray<String>()
        for (i in indications.indices){
            val counter = counters.firstOrNull {it.id == indications[i].idCounter}
            values += ( services.firstOrNull {it.id == counter?.idService}?.nameCounter
                    + " №" + counter?.number + ", Квартира №" + flats.firstOrNull{it.id == counter?.idFlat}?.flatNumber + ",\n Дата: " + indications[i].dateTransfer.format(dtf) + ", показание: " + indications[i].value)
        }

        val adapter = ArrayAdapter(fragment.requireContext(), android.R.layout.simple_list_item_1, values)
        binding.listStory.adapter = adapter
    }

    private fun replaceFragment(){
        val fragmentManager = fragment.fragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frameLayout, IndicationsFragment())
        fragmentTransaction?.commit()
    }
}