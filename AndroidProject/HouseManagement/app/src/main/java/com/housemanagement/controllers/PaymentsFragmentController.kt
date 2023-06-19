package com.housemanagement.controllers

import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.housemanagement.R
import com.housemanagement.adapters.PaymentViewAdapter
import com.housemanagement.databinding.FragmentPaymentsBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.PaymentsFragment
import com.housemanagement.models.tables.*

class PaymentsFragmentController {
    private var binding: FragmentPaymentsBinding
    private lateinit var adapter: PaymentViewAdapter
    private var fragment: PaymentsFragment
    private var db: MainDb
    private var payments = ArrayList<Payment>()
    private var services = ArrayList<Service>()
    private var flats = ArrayList<Flat>()
    private var settingsServices = ArrayList<SettingsService>()

    //filters-----------------------
    lateinit var servicesFilters: ArrayList<String>
    private var periodsFilters = ArrayList<String>()
    private var flatsFilters = ArrayList<String>()
    lateinit var flatsFiltersAdapter: ArrayAdapter<String>
    lateinit var servicesFiltersAdapter: ArrayAdapter<String>
    lateinit var periodsFiltersAdapter: ArrayAdapter<String>
    private var viewLifecycleOwner: LifecycleOwner
    //------------------------------
    constructor(binding: FragmentPaymentsBinding, fragment: PaymentsFragment, viewLifecycleOwner: LifecycleOwner){
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        getData("payments")
    }

    private fun getData(list: String) {
        when (list) {
            "payments" -> db.getDao().getPayments().asLiveData().observe(viewLifecycleOwner) {
                payments = ArrayList(it.toMutableList())
                getData("services")
            }
            "services" -> db.getDao().getServices().asLiveData().observe(viewLifecycleOwner) {
                services = ArrayList(it.toMutableList())
                getData("settingsServices")
            }
            "settingsServices" -> db.getDao().getSettingsServices().asLiveData()
                .observe(viewLifecycleOwner) {
                    settingsServices = ArrayList(it.toMutableList())
                    getData("flats")
                }
            "flats" -> db.getDao().getFlats().asLiveData().observe(viewLifecycleOwner) {
                flats = ArrayList(it.toMutableList())
                initFragment()
            }
        }
    }

    private fun initFragment() {
        binding.paymentsRecyclerView.layoutManager = LinearLayoutManager(fragment.context)
        binding.paymentsRecyclerView.setHasFixedSize(true)
        adapter = PaymentViewAdapter(
            payments,
            fragment.context, services, flats, settingsServices
        )
        binding.paymentsRecyclerView.adapter = adapter
        getFilters()
        binding.listViewPeriods.adapter = periodsFiltersAdapter
        binding.listViewFlats.adapter = flatsFiltersAdapter
        binding.spinnerService.adapter = servicesFiltersAdapter
        binding.listViewFlats.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            binding.searchViewFlat.setQuery(selectedItem, true)
        }
        binding.listViewPeriods.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            binding.searchViewPeriod.setQuery(selectedItem, true)
        }
        binding.filters.visibility = View.GONE;
        binding.btnFilterOpen.setOnClickListener(View.OnClickListener {
            binding.filters.visibility = View.VISIBLE;
        })
        binding.searchViewFlat.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchViewFlat.clearFocus()
                if(flatsFilters.contains(query)) flatsFiltersAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                flatsFiltersAdapter.filter.filter(newText)
                return false
            }
        })
        binding.searchViewPeriod.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchViewPeriod.clearFocus()
                if(periodsFilters.contains(query)) periodsFiltersAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                periodsFiltersAdapter.filter.filter(newText)
                return false
            }
        })
        binding.buttonBack.setOnClickListener(View.OnClickListener { binding.filters.visibility = View.GONE; })
        binding.btnFilter.setOnClickListener(View.OnClickListener {
            filterData()
        })
        binding.btnReset.setOnClickListener(View.OnClickListener {
            resetData()
        })
    }

    private fun getFilters() {
        periodsFilters = getPeriods()
        flatsFilters = getNumbersFlat()
        servicesFilters = getNamesServices()
        flatsFiltersAdapter = ArrayAdapter(fragment.requireContext(), android.R.layout.simple_list_item_1, flatsFilters)
        periodsFiltersAdapter = ArrayAdapter(fragment.requireContext(), android.R.layout.simple_list_item_1, periodsFilters)
        servicesFiltersAdapter = ArrayAdapter(fragment.requireContext(), android.R.layout.simple_list_item_1, servicesFilters)
    }

    private fun resetData() {
        adapter = PaymentViewAdapter(payments,fragment.context, services, flats, settingsServices)
        binding.paymentsRecyclerView.setAdapter(adapter)
        binding.filters.visibility = View.GONE;
        binding.searchViewFlat.setQuery("", true)
        binding.searchViewPeriod.setQuery("", true)
        binding.spinnerService.setSelection(0)
    }

    private fun filterData() {
        var id_flat = ""
        var id_service = 0
        val service = servicesFilters.first() { it == binding.spinnerService.selectedItem }
        var newPayments = ArrayList<Payment>()
        if(service != "")
        {
            id_service = services.firstOrNull{it.nameService == service}?.id!!
            newPayments = payments.filter { it.idService == id_service  } as ArrayList<Payment>
        }
        if(binding.searchViewFlat.query.toString() != "")
        {
            id_flat =  flats.firstOrNull {it.flatNumber.toString() == binding.searchViewFlat.query.toString()}?.id.toString()
            newPayments = newPayments.filter { it.idFlat == id_flat.toInt()} as ArrayList<Payment>
            newPayments = newPayments.filter { it.period.contains(binding.searchViewPeriod.query)} as ArrayList<Payment>
        }
        else
        {
            newPayments = newPayments.filter { it.period.contains(binding.searchViewPeriod.query) } as ArrayList<Payment>
        }
        adapter = PaymentViewAdapter(newPayments,fragment.context, services, flats, settingsServices)
        binding.paymentsRecyclerView.adapter = adapter
        binding.filters.visibility = View.GONE
    }

    fun getNumbersFlat(): ArrayList<String>{
        val numbers = ArrayList<String>()
        for(flat in flats){
            numbers.add(flat.flatNumber.toString())
        }
        return numbers
    }

    fun getPeriods(): ArrayList<String> {
        val periods = ArrayList<String>()
        val paymentsItem = payments.distinctBy { it.period }
        for (payment in paymentsItem){
            periods.add(payment.period)
        }
        return  periods
    }

    fun getNamesServices(): ArrayList<String>{
        val names = ArrayList<String>()
        names.add("")
        for(service in services){
            names.add(service.nameService)
        }
        return names
    }
}