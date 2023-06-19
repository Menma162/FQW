package com.housemanagement.controllers

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.housemanagement.R
import com.housemanagement.databinding.FragmentAddAdvertisementBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.AddAdvertisementFragment
import com.housemanagement.fragments.AdvertisementsAdminFragment
import com.housemanagement.fragments.SelectHouseFragment
import com.housemanagement.interfaces.ApiRequests
import com.housemanagement.models.other.AdvertisementPostToServer
import com.housemanagement.models.tables.Advertisement
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


class AddAdvertisementFragmentController {
    private var binding: FragmentAddAdvertisementBinding
    private var fragment: AddAdvertisementFragment
    var apiRequests: ApiRequests
    private var token = ""
    private var db: MainDb
    private lateinit var idHouses: ArrayList<Int>
    val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var viewLifecycleOwner: LifecycleOwner
    constructor(binding: FragmentAddAdvertisementBinding, fragment: AddAdvertisementFragment, viewLifecycleOwner: LifecycleOwner, bundle: Bundle){
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
        idHouses = bundle.getIntegerArrayList("idHouses") as ArrayList<Int>
        token = binding.root.context.getSharedPreferences("HouseManagement", 0).getString("token", "")
                .toString()
        initFragment()
    }

    private fun initFragment() {
        binding.buttonBack.setOnClickListener(View.OnClickListener {
            replaceFragment(SelectHouseFragment())
        })
        binding.btnThen.setOnClickListener(View.OnClickListener {
            val advertisement = AdvertisementPostToServer()
            GetDataFromServer.getDateNow(binding.root.context)
            val date = LocalDate.parse(
                binding.root.context.getSharedPreferences("my_storage", Context.MODE_PRIVATE)
                    .getString("dateNow", LocalDate.now().toString()))
            advertisement.date = date.toString()  + "T00:00:00"
            advertisement.description = binding.advertisementDescription.text.toString()
            advertisement.idHouses = idHouses
            postAdvertisementToServer(advertisement)
        })
        GetDataFromServer.getDateNow(binding.root.context)
        val date = LocalDate.parse(
            binding.root.context.getSharedPreferences("my_storage", Context.MODE_PRIVATE)
                .getString("dateNow", LocalDate.now().toString())
        )
        binding.advertisementName.text = "Объявление от " + date.format(dtf)
    }


    private fun postAdvertisementToServer(advertisement: AdvertisementPostToServer) {
        apiRequests.sendAdvertisement("Bearer $token", advertisement)
            .enqueue(object : Callback<JsonArray> {
                override fun onResponse(
                    call: Call<JsonArray>,
                    response: Response<JsonArray>
                ) {
                    try {
                        val gson = Gson()
                        if (response.code() == 201) {
                            val array = response.body()
                            val newAdvertisements = ArrayList<Advertisement>()
                            if (array != null) {
                                for (item in array) {
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
                                    val description = gson.fromJson(
                                        item.asJsonObject.get("description"),
                                        String::class.java
                                    )
                                    val idHouseItem = gson.fromJson(
                                        item.asJsonObject.get("idHouse"),
                                        Int::class.java
                                    )
                                    newAdvertisements.add(
                                        Advertisement(
                                            id,
                                            description,
                                            date,
                                            idHouseItem
                                        )
                                    )
                                }
                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertAdvertisements(
                                        newAdvertisements
                                    )
                                }
                                Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT)
                                    .show()
                                replaceFragment(AdvertisementsAdminFragment())
                            }
                        }
                        else if (response.code() == 401) ShowErrorMessage.show(binding.root.context, "Ошибка", "Необходимо переавторизоваться", 2)
                        else ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось отправить объявление",
                            2
                        )
                    } catch (ex: java.lang.Exception) {
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось отправить объявление",
                            2
                        )
                    }
                }

                override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                    ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        "Не удалось отправить объявление",
                        2
                    )
                }
            })
    }

    private fun replaceFragment(fragmentItem: Fragment){
        val fragmentManager = fragment.fragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frameLayout, fragmentItem)
        fragmentTransaction?.commit()
    }
}