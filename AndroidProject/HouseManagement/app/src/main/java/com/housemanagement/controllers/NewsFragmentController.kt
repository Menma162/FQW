package com.housemanagement.controllers

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.housemanagement.adapters.NewViewAdapter
import com.housemanagement.databinding.FragmentHomeBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.NewsFragment
import com.housemanagement.models.other.New
import com.housemanagement.models.tables.*
import com.housemanagement.otherclasses.DataRepository
import com.housemanagement.otherclasses.GetDataFromServer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewsFragmentController {
    private var binding: FragmentHomeBinding
    private lateinit var adapter: NewViewAdapter
    private var fragment: NewsFragment
    private var viewLifecycleOwner: LifecycleOwner
    private var db: MainDb
    private var indications = ArrayList<Indication>()
    private var counters = ArrayList<Counter>()
    private var payments = ArrayList<Payment>()
    private var services = ArrayList<Service>()
    private var advertisements = ArrayList<Advertisement>()
    private var settingsServices = ArrayList<SettingsService>()
    private var dataRepository: DataRepository

    constructor(
        binding: FragmentHomeBinding,
        fragment: NewsFragment,
        viewLifecycleOwner: LifecycleOwner
    ) {
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        dataRepository = DataRepository(db.getDao())
        getData("payments")
    }

    private fun getData(list: String) {
        when (list) {
            "payments" -> dataRepository.listPayments().observe(viewLifecycleOwner) {
                payments = ArrayList(it.toMutableList())
                getData("services")
            }
            "services" -> dataRepository.listServices().observe(viewLifecycleOwner) {
                services = ArrayList(it.toMutableList())
                getData("settingsServices")
            }
            "settingsServices" -> dataRepository.listSettingsServices()
                .observe(viewLifecycleOwner) {
                    settingsServices = ArrayList(it.toMutableList())
                    getData("counters")
                }
            "counters" -> dataRepository.listCounters().observe(viewLifecycleOwner) {
                counters = ArrayList(it.toMutableList())
                getData("indications")
            }
            "indications" -> dataRepository.listIndications().observe(viewLifecycleOwner) {
                indications = ArrayList(it.toMutableList())
                getData("advertisements")
            }
            "advertisements" -> dataRepository.listAdvertisements()
                .observe(viewLifecycleOwner) { list ->
                    //advertisements = ArrayList(list.toMutableList().sortedByDescending{ it.date})
                    advertisements = ArrayList(list.toMutableList())
                    initFragment()
                }
        }
    }

    private fun initFragment() {
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(fragment.context)
        binding.newsRecyclerView.setHasFixedSize(true)
        adapter = NewViewAdapter(getNews(), fragment.context)
        binding.newsRecyclerView.adapter = adapter
    }

    fun getNews(): ArrayList<New> {
        val news = ArrayList<New>()

        GetDataFromServer.getDateNow(binding.root.context)
        val dateNow = LocalDate.parse(
            binding.root.context.getSharedPreferences("my_storage", Context.MODE_PRIVATE)
                .getString("dateNow", LocalDate.now().toString())
        )
        var textReminderIndication = "Внимание, необходимо передать показания по услугам: "
        var textReminderPayment = "Внимание, необходимо оплатить начисления по услугам: "

        var haveNoIndications = false
        var haveNoPayments = false

        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        var index: Int = 0

        for (service in services) {
            val settings = settingsServices.firstOrNull { it.id == service.id }
            //-начало проверки показаний
            if (settings?.isHaveCounter == true && dateNow.dayOfMonth >= settings.startDateTransfer && dateNow.dayOfMonth <= settings.endDateTransfer) {
                val countersService = counters.filter { it.idService == service.id }
                for (counter in countersService) {
                    val indication = indications.firstOrNull {
                        (it.dateTransfer.year == dateNow.year) and (it.dateTransfer.monthValue == dateNow.monthValue) and it.idCounter.equals(
                            counter.id
                        )
                    }
                    if (indication == null) {
                        textReminderIndication += service.nameService + ", "
                        haveNoIndications = true
                    }
                }
            }
        }
        if (haveNoIndications) {
            index++
            textReminderIndication.removeRange(
                textReminderIndication.length - 2,
                textReminderIndication.length
            )
            textReminderIndication += " до истечения сроков!"
            news.add(
                New(
                    index,
                    "Напоминание о показаниях",
                    textReminderIndication
                )
            )
        }
        //-конец проверки показаний


        for (advertisement in advertisements) {
            index++
            news.add(
                New(
                    index,
                    "Объявление от " + advertisement.date,
                    advertisement.description
                )
            )
        }
        return news
    }
}