package com.housemanagement.controllers

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.housemanagement.R
import com.housemanagement.adapters.AdvertisementAdminViewAdapter
import com.housemanagement.databinding.FragmentAdvertisementsAdminBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.AdvertisementsAdminFragment
import com.housemanagement.fragments.SelectHouseFragment
import com.housemanagement.interfaces.ApiRequests
import com.housemanagement.models.other.AdvertisementPostToServer
import com.housemanagement.models.other.AdvertisementPutToServer
import com.housemanagement.models.tables.Advertisement
import com.housemanagement.models.tables.House
import com.housemanagement.otherclasses.GetDataFromServer
import com.housemanagement.otherclasses.RetrofitService
import com.housemanagement.otherclasses.ShowErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdvertisementsAdminFragmentController {
    private var binding: FragmentAdvertisementsAdminBinding
    private lateinit var adapter: AdvertisementAdminViewAdapter
    private var fragment: AdvertisementsAdminFragment
    val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var advertisements = ArrayList<Advertisement>()
    private var houses = ArrayList<House>()
    private var db: MainDb
    private lateinit var date: LocalDate
    private var token = ""
    private var editAdvertisement = Advertisement()
    var apiRequests: ApiRequests
    private var viewLifecycleOwner: LifecycleOwner

    constructor(
        binding: FragmentAdvertisementsAdminBinding,
        fragment: AdvertisementsAdminFragment,
        viewLifecycleOwner: LifecycleOwner
    ) {
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
        getData("advertisements")
    }

    private fun getData(list: String) {
        when (list) {
            "advertisements" -> db.getDao().getAdvertisements().asLiveData().observe(viewLifecycleOwner){
                //advertisements = ArrayList(it.toMutableList().sortedByDescending { it.date })
                advertisements = ArrayList(it.toMutableList())
                getData("houses")
            }
            "houses" -> db.getDao().getHouses().asLiveData().observe(viewLifecycleOwner){
                houses = ArrayList(it.toMutableList())
                initFragment()
            }
        }
    }

    private fun initFragment() {
        binding.advertisementsRecyclerView.layoutManager = LinearLayoutManager(fragment.context)
        binding.advertisementsRecyclerView.setHasFixedSize(true)
        adapter = AdvertisementAdminViewAdapter(advertisements, fragment.context, this, houses)
        binding.advertisementsRecyclerView.adapter = adapter
        binding.btnClose.setOnClickListener(View.OnClickListener {
            closeAdvertisement()
        })
        closeAdvertisement()
        binding.btnAdd.setOnClickListener(View.OnClickListener {
            replaceFragment(SelectHouseFragment())
        })
        binding.btnWork.setOnClickListener(View.OnClickListener { sendAdvertisement() })
        binding.btnWDelete.setOnClickListener(View.OnClickListener { deleteAdvertisement() })
        token =
            binding.root.context.getSharedPreferences("HouseManagement", 0).getString("token", "")
                .toString()
    }

    private fun replaceFragment(fragmentItem: Fragment){
        val fragmentManager = fragment.fragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frameLayout, fragmentItem)
        fragmentTransaction?.commit()
    }

    private fun deleteAdvertisement() {
        val description = binding.advertisementDescription.text.toString()
        val advertisement =
            Advertisement(
                editAdvertisement.id,
                description,
                editAdvertisement.date,
                editAdvertisement.idHouse
            )
        apiRequests.deleteAdvertisement("Bearer $token", advertisement.id)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    try {
                        if (response.code() == 204) {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                db.getDao().deleteAdvertisement(advertisement)
                            }
                            Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT)
                                .show()
                        } else ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось удалить объявление",
                            2
                        )
                        closeAdvertisement()
                    } catch (ex: java.lang.Exception) {
                        closeAdvertisement()
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось удалить объявление",
                            2
                        )
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    closeAdvertisement()
                    ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        "Не удалось удалить объявление",
                        2
                    )
                }
            })
    }

    private fun sendAdvertisement() {
        val description = binding.advertisementDescription.text.toString()
            val advertisement =
                AdvertisementPutToServer(
                    editAdvertisement.id,
                    description,
                    editAdvertisement.idHouse,
                    editAdvertisement.date.toString() + "T00:00:00"
                )
            putAdvertisementToServer(advertisement)
    }

    private fun putAdvertisementToServer(advertisement: AdvertisementPutToServer) {
        apiRequests.putAdvertisement("Bearer $token", advertisement.id, advertisement)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    try {
                        val gson = Gson()
                        if (response.code() == 201) {
                            val item = response.body()
                            if (item != null) {
                                val date = LocalDate.parse(
                                    gson.fromJson(
                                        item.asJsonObject.get("date"),
                                        String::class.java
                                    ).removeRange(10, 19)
                                )
                                val id =
                                    gson.fromJson(
                                        item.asJsonObject.get("id"),
                                        Int::class.java
                                    )
                                val idHouse = gson.fromJson(
                                    item.asJsonObject.get("idHouse"),
                                    Int::class.java
                                )
                                val description = gson.fromJson(
                                    item.asJsonObject.get("description"),
                                    String::class.java
                                )
                                val newAdvertisement = Advertisement(
                                    id,
                                    description,
                                    date,
                                    idHouse
                                )
                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().updateAdvertisement(newAdvertisement)
                                }
                                Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        else if (response.code() == 401) ShowErrorMessage.show(binding.root.context, "Ошибка", "Необходимо переавторизоваться", 2)
                        else ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось обновить объявление",
                            2
                        )
                        closeAdvertisement()
                    } catch (ex: java.lang.Exception) {
                        closeAdvertisement()
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось обновить объявление",
                            2
                        )
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    closeAdvertisement()
                    ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        "Не удалось обновить объявление",
                        2
                    )
                }
            })
    }

    private fun closeAdvertisement() {
        binding.shadow.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.advertisements.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.advertisement.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.GONE
        binding.advertisement.visibility = View.GONE
        val c =
            fragment.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        c.hideSoftInputFromWindow(fragment.view?.windowToken, 0)
    }

    fun openAdvertisement(id: Int) {
        binding.shadow.translationZ = 100 * fragment.resources.displayMetrics.density
        binding.advertisements.translationZ = 0F * fragment.resources.displayMetrics.density
        binding.advertisement.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.VISIBLE
        binding.advertisement.visibility = View.VISIBLE
        binding.shadow.isClickable = true
            binding.nameHouseTextView.visibility = View.VISIBLE
            fillAdvertisement(id)
            binding.btnWork.text = "Сохранить объявление"
            binding.btnWDelete.visibility = View.VISIBLE
    }

    private fun fillAdvertisement(id: Int) {
        val advertisement = advertisements.firstOrNull { it.id == id }
        editAdvertisement.id = id
        editAdvertisement.idHouse = advertisement?.idHouse
        editAdvertisement.date = advertisement?.date
        binding.advertisementName.text = "Объявление от " + advertisement?.date?.format(dtf)
        binding.nameHouseTextView.text = "Дом - " + houses.firstOrNull{it.id == advertisement?.idHouse}?.name
        binding.advertisementDescription.setText(advertisement?.description)
    }
}