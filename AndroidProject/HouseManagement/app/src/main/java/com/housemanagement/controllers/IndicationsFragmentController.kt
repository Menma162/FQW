package com.housemanagement.controllers

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.housemanagement.R
import com.housemanagement.adapters.CounterViewAdapter
import com.housemanagement.databinding.FragmentIndicationsBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.IndicationsFragment
import com.housemanagement.fragments.StoryIndicationsFragment
import com.housemanagement.interfaces.ApiRequests
import com.housemanagement.models.other.IndicationToServer
import com.housemanagement.models.tables.*
import com.housemanagement.otherclasses.GetDataFromServer
import com.housemanagement.otherclasses.RetrofitService
import com.housemanagement.otherclasses.ShowErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


class IndicationsFragmentController {
    private var binding: FragmentIndicationsBinding
    private lateinit var adapter: CounterViewAdapter
    private var fragment: IndicationsFragment
    private var db: MainDb
    private var counters = ArrayList<Counter>()
    private var indications = ArrayList<Indication>()
    private var flats = ArrayList<Flat>()
    private var services = ArrayList<Service>()
    private var settingsServices = ArrayList<SettingsService>()
    private var viewLifecycleOwner: LifecycleOwner
    var apiRequests: ApiRequests
    private lateinit var date: LocalDate
    private var token = ""
    private var editIndication = Indication()
    constructor(binding: FragmentIndicationsBinding, fragment: IndicationsFragment, viewLifecycleOwner: LifecycleOwner){
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        getData("flats")
        apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
    }

    private fun getData(list: String) {
        when (list) {
            "flats" -> db.getDao().getFlats().asLiveData().observe(viewLifecycleOwner) {
                flats = ArrayList(it.toMutableList())
                getData("services")
            }
            "services" -> db.getDao().getServices().asLiveData().observe(viewLifecycleOwner) {
                services = ArrayList(it.toMutableList())
                getData("settingsServices")
            }
            "settingsServices" -> db.getDao().getSettingsServices().asLiveData()
                .observe(viewLifecycleOwner) {
                    settingsServices = ArrayList(it.toMutableList())
                    getData("counters")
                }
            "counters" -> db.getDao().getCounters().asLiveData().observe(viewLifecycleOwner) {
                counters = ArrayList(it.toMutableList())
                getData("indications")
            }
            "indications" -> db.getDao().getIndications().asLiveData().observe(viewLifecycleOwner) {
                indications = ArrayList(it.toMutableList())
                initFragment()
            }
        }
    }

    private fun initFragment(){
        Thread.sleep(800)
        binding.countersRecyclerView.layoutManager = LinearLayoutManager(fragment.context)
        binding.countersRecyclerView.setHasFixedSize(true)
        GetDataFromServer.getDateNow(binding.root.context)
        val date = LocalDate.parse(binding.root.context.getSharedPreferences("my_storage", Context.MODE_PRIVATE)
            .getString("dateNow", LocalDate.now().toString()))
        adapter = CounterViewAdapter(counters, fragment.context, this, flats, services, settingsServices, date)
        binding.countersRecyclerView.adapter = adapter
        binding.btnClose.setOnClickListener(View.OnClickListener {
            closeIndication()
        })
        closeIndication()
        binding.btnStory.setOnClickListener(View.OnClickListener {
            replaceFragment()
        })
        binding.btnPlus.setOnClickListener(View.OnClickListener {
            plusValueIndication()
        })
        binding.btnMinus.setOnClickListener(View.OnClickListener {
            minusIndicationValue()
        })
        binding.btnTransfer.setOnClickListener(View.OnClickListener {
            if(binding.editTextIndication.text.toString() != "")
                sendIndication()
        })
        token =
            binding.root.context.getSharedPreferences("HouseManagement", 0).getString("token", "")
                .toString()
    }

    private fun sendIndication() {
        val value = binding.editTextIndication.text.toString().toInt()
        if(editIndication.id == null || editIndication.id == 0){
            val indication =
                IndicationToServer(0, editIndication.idCounter, value, date.toString() + "T00:00:00")
            postIndicationToServer(indication)
        }
        else {
            val indication =
                IndicationToServer(editIndication.id, editIndication.idCounter, value, editIndication.dateTransfer.toString() + "T00:00:00")
            putIndicationToServer(indication)
        }
    }

    private fun putIndicationToServer(indication: IndicationToServer) {
        apiRequests.putIndication("Bearer $token", indication.id, indication)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    val gson = Gson()
                    try {
                        if (response.code() == 201) {
                            val item = response.body()
                            if (item != null) {
                                val date = LocalDate.parse(
                                    gson.fromJson(
                                        item.asJsonObject.get("dateTransfer"),
                                        String::class.java
                                    ).removeRange(10, 19)
                                )
                                val id =
                                    gson.fromJson(
                                        item.asJsonObject.get("id"),
                                        Int::class.java
                                    )
                                val idCounter = gson.fromJson(
                                    item.asJsonObject.get("idCounter"),
                                    Int::class.java
                                )
                                val value = gson.fromJson(
                                    item.asJsonObject.get("value"),
                                    Int::class.java
                                )
                                val newIndication = Indication(
                                    id,
                                    idCounter,
                                    date,
                                    value
                                )
                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().updateIndication(newIndication)
                                }
                                Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else if (response.code() == 401) ShowErrorMessage.show(binding.root.context, "Ошибка", "Необходимо переавторизоваться", 2)
                        else if (response.code() == 500 && response.errorBody() != null) {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            ShowErrorMessage.show(binding.root.context, "Ошибка", jObjError.getString("message"), 2)
                        }
                        else ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось изменить показания", 2)
                        closeIndication()
                    } catch (ex: java.lang.Exception) {
                        closeIndication()
                        ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось изменить показания", 2)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    closeIndication()
                    ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось изменить показания", 2)
                }
            })
    }

    private fun postIndicationToServer(indication: IndicationToServer) {
        apiRequests.sendIndication("Bearer $token", indication)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    val gson = Gson()
                    try {
                        if (response.code() == 201) {
                            val item = response.body()
                            if (item != null) {
                                val date = LocalDate.parse(
                                    gson.fromJson(
                                        item.asJsonObject.get("dateTransfer"),
                                        String::class.java
                                    ).removeRange(10, 19)
                                )
                                val id =
                                    gson.fromJson(
                                        item.asJsonObject.get("id"),
                                        Int::class.java
                                    )
                                val idCounter = gson.fromJson(
                                    item.asJsonObject.get("idCounter"),
                                    Int::class.java
                                )
                                val value = gson.fromJson(
                                    item.asJsonObject.get("value"),
                                    Int::class.java
                                )
                                val newIndication = Indication(
                                    id,
                                    idCounter,
                                    date,
                                    value
                                )
                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertIndication(newIndication)
                                }
                                Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else if (response.code() == 401) ShowErrorMessage.show(binding.root.context, "Ошибка", "Необходимо переавторизоваться", 2)
                        else if (response.code() == 500 && response.errorBody() != null) {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            ShowErrorMessage.show(binding.root.context, "Ошибка", jObjError.getString("message"), 2)
                        }
                        else ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось передать показания", 2)
                        closeIndication()
                    } catch (ex: java.lang.Exception) {
                        closeIndication()
                        ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось передать показания", 2)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    closeIndication()
                    ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось передать показания", 2)
                }
            })
    }

    private fun minusIndicationValue() {
        val value = binding.editTextIndication.text.toString().toInt()
        if (value > 0) binding.editTextIndication.setText((value - 1).toString())
    }

    private fun plusValueIndication() {
        val value = binding.editTextIndication.text.toString().toInt()
        if (value < 99999) binding.editTextIndication.setText((value + 1).toString())
    }

    private fun closeIndication() {
        binding.shadow.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.counters.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.indication.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.GONE
        binding.indication.visibility = View.GONE
        val c = fragment.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        c.hideSoftInputFromWindow(fragment.view?.windowToken, 0)
    }

    fun openIndication(id: Int){
        binding.shadow.translationZ = 100 * fragment.resources.displayMetrics.density
        binding.counters.translationZ = 0F * fragment.resources.displayMetrics.density
        binding.indication.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.VISIBLE
        binding.indication.visibility = View.VISIBLE
        binding.shadow.isClickable = true
        fillCounter(id)
    }

    private fun fillCounter(id: Int) {
        val counter = counters.firstOrNull{it.id == id}
        editIndication.idCounter = counter?.id
        binding.counterName.text = services.firstOrNull{it.id == counter?.idService}?.nameCounter
        binding.flatNumber.text = "Квартира №" + flats.firstOrNull{it.id == counter?.idFlat}?.flatNumber
        binding.counterNumber.text = "Счетчик №" + counter?.number

        if(getLastIndication(id) != null) binding.lastIndication.text = "Показание за предыдущий месяц - " + getLastIndication(id)?.value
        else binding.lastIndication.text = "Показание за предыдущий месяц - отсутствует"//

        if(getNowIndication(id) != null){
            val indicationNow = getNowIndication(id)
            editIndication.id = indicationNow?.id
            editIndication.dateTransfer = indicationNow?.dateTransfer
            binding.editTextIndication.setText(indicationNow?.value.toString())
            binding.btnTransfer.text = "Сохранить показание"
        }
        else{
            date = LocalDate.parse(
                binding.root.context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE)
                    .getString("dateNow", LocalDate.now().toString())
            )
            editIndication.id = null
            binding.editTextIndication.setText("0")
            binding.btnTransfer.text = "Передать показание"
        }
    }

    private fun replaceFragment(){
        val fragmentManager = fragment.fragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frameLayout, StoryIndicationsFragment())
        fragmentTransaction?.commit()
    }

    private fun getLastIndication(id: Int): Indication? {
        GetDataFromServer.getDateNow(binding.root.context)
        val date = LocalDate.parse(binding.root.context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE)
            .getString("dateNow", LocalDate.now().toString()))
        var month = date.monthValue - 1
        var year = date.year
        if(date.monthValue == 1) {
            month = 12
            year = date.year - 1}
        val lastIndication = indications.firstOrNull {
            (it.dateTransfer.year == year) and (it.dateTransfer.monthValue == month) and it.idCounter.equals(id)
        }
        return lastIndication
    }
    private fun getNowIndication(id: Int): Indication? {
        GetDataFromServer.getDateNow(binding.root.context)
        val date = LocalDate.parse(binding.root.context.getSharedPreferences("my_storage", Context.MODE_PRIVATE)
            .getString("dateNow", LocalDate.now().toString()))
        val nowIndication = indications.firstOrNull{
            (it.dateTransfer.year == date.year) and (it.dateTransfer.monthValue == date.monthValue) and it.idCounter.equals(id)
        }
        return nowIndication
    }
}