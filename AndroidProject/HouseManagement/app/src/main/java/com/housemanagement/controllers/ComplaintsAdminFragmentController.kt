package com.housemanagement.controllers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.housemanagement.R
import com.housemanagement.adapters.ComplaintViewAdapter
import com.housemanagement.databinding.FragmentComplaintsAdminBinding
import com.housemanagement.databinding.FragmentComplaintsBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.ComplaintsAdminFragment
import com.housemanagement.fragments.ComplaintsFragment
import com.housemanagement.interfaces.ApiRequests
import com.housemanagement.models.other.ComplaintToServer
import com.housemanagement.models.tables.Advertisement
import com.housemanagement.models.tables.Complaint
import com.housemanagement.models.tables.Flat
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


class ComplaintsAdminFragmentController {
    private var binding: FragmentComplaintsAdminBinding
    private lateinit var adapter: ComplaintViewAdapter
    private var fragment: ComplaintsAdminFragment
    private lateinit var statuses: Array<String>
    private var complaints = ArrayList<Complaint>()
    private var flats = ArrayList<Flat>()
    private var houses = ArrayList<House>()
    private var db: MainDb
    private var viewLifecycleOwner: LifecycleOwner
    var apiRequests: ApiRequests
    private lateinit var date: LocalDate
    private var token = ""
    private var editComplaint = Complaint()
    val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    constructor(binding: FragmentComplaintsAdminBinding, fragment: ComplaintsAdminFragment, viewLifecycleOwner: LifecycleOwner){
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
        getData("complaints")
    }

    private fun getData(list: String) {
        when (list) {
            "complaints" -> db.getDao().getComplaints().asLiveData().observe(viewLifecycleOwner){
                //complaints = ArrayList(it.toMutableList().sortedByDescending{ it.date})
                complaints = ArrayList(it.toMutableList())
                getData("houses")
            }
            "houses" -> db.getDao().getHouses().asLiveData().observe(viewLifecycleOwner){
                houses = ArrayList(it.toMutableList())
                getData("flats")
            }
            "flats" -> db.getDao().getFlats().asLiveData().observe(viewLifecycleOwner){
                flats = ArrayList(it.toMutableList())
                initFragment()
            }
        }
    }

    private fun deleteComplaint() {
        val status = statuses[binding.statusComplaintSpinner.selectedItemPosition]
        val complaint = Complaint(editComplaint.id, editComplaint.idFlat, status, editComplaint.description, editComplaint.photo, editComplaint.date)
        apiRequests.deleteComplaint("Bearer $token", complaint.id)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    try {
                        if (response.code() == 204) {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                db.getDao().deleteComplaint(complaint)
                            }
                            Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT)
                                .show()
                        } else ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось удалить заявку",
                            2
                        )
                        closeComplaint()
                    } catch (ex: java.lang.Exception) {
                        closeComplaint()
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось удалить заявку",
                            2
                        )
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    closeComplaint()
                    ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        "Не удалось удалить заявку",
                        2
                    )
                }
            })
    }

    private fun putComplaint(){
        val status = statuses[binding.statusComplaintSpinner.selectedItemPosition]
        val complaint = ComplaintToServer(editComplaint.id, editComplaint.idFlat, status, editComplaint.description, editComplaint.photo, editComplaint.date.toString() + "T00:00:00")
        apiRequests.putComplaint("Bearer $token", complaint.id, complaint)
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
                                val idFlat = gson.fromJson(
                                    item.asJsonObject.get("idFlat"),
                                    Int::class.java
                                )
                                val status = gson.fromJson(
                                    item.asJsonObject.get("status"),
                                    String::class.java
                                )
                                val description = gson.fromJson(
                                    item.asJsonObject.get("description"),
                                    String::class.java
                                )
                                val photo = gson.fromJson(
                                    item.asJsonObject.get("photo"),
                                    String::class.java
                                )
                                val newComplaint = Complaint(
                                    id, idFlat, status,
                                    description,
                                    photo,
                                    date
                                )
                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().updateComplaint(newComplaint)
                                }
                                Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else if (response.code() == 401) ShowErrorMessage.show(binding.root.context, "Ошибка", "Необходимо переавторизоваться", 2)
                        else ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить заявку", 2)
                        closeComplaint()
                    } catch (ex: java.lang.Exception) {
                        closeComplaint()
                        ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить заявку", 2)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    closeComplaint()
                    ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить заявку", 2)
                }
            })
    }

    private fun initFragment() {
        binding.complaintsRecyclerView.layoutManager = LinearLayoutManager(fragment.context)
        binding.complaintsRecyclerView.setHasFixedSize(true)
        adapter = ComplaintViewAdapter(complaints, fragment.context, null,this, flats, houses)
        binding.complaintsRecyclerView.adapter = adapter
        binding.btnClose.setOnClickListener(View.OnClickListener {
            closeComplaint()
        })
        closeComplaint()
        binding.btnComplaint.setOnClickListener(View.OnClickListener {
            putComplaint()
        })
        binding.btnDeleteComplaint.setOnClickListener(View.OnClickListener {
            deleteComplaint()
        })
        statuses = fragment.resources.getStringArray(R.array.statusesComplaint)
        token =
            binding.root.context.getSharedPreferences("HouseManagement", 0).getString("token", "")
                .toString()
    }

    private fun closeComplaint() {
        binding.shadow.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.complaints.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.complaint.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.GONE
        binding.complaint.visibility = View.GONE
        val c = fragment.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        c.hideSoftInputFromWindow(fragment.view?.windowToken, 0)
    }

    fun openComplaint(id: Int){
        binding.shadow.translationZ = 100 * fragment.resources.displayMetrics.density
        binding.complaints.translationZ = 0F * fragment.resources.displayMetrics.density
        binding.complaint.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.VISIBLE
        binding.complaint.visibility = View.VISIBLE
        binding.shadow.isClickable = true
        fillComplaint(id)
    }

    private fun fillComplaint(id: Int) {
        val complaint = complaints.firstOrNull{it.id == id}
        val flat = flats.firstOrNull{
            it.id == complaint?.idFlat
        }
        binding.dateComplaint.text = "Заявка от " + complaint?.date?.format(dtf)
        binding.descriptionComplaint.setText(complaint?.description)
        binding.nameHouseTextView.text = "Дом - " + houses.firstOrNull{it.id == flat?.idHouse}?.name
        binding.numberFlatTextView.text = "Квартира №" + flat?.flatNumber
        if(complaint?.photo == null) {
            binding.photoTextView.visibility = View.GONE
            binding.photoComplaint.visibility = View.GONE
        }
        else {
            binding.photoTextView.visibility = View.VISIBLE
            binding.photoComplaint.visibility = View.VISIBLE
        }
        binding.statusComplaintSpinner.setSelection(statuses.indexOfFirst { it == complaint?.status })
        editComplaint.id = id
        editComplaint.date = complaint?.date
        editComplaint.photo = complaint?.photo
        editComplaint.idFlat = complaint?.idFlat
        editComplaint.description = complaint?.description


        val token: String = binding.root.context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        val apiRequests = RetrofitService().retrofit.create(
            ApiRequests::class.java
        )
        apiRequests.getPhoto("Bearer $token", id).enqueue(object : Callback<JsonPrimitive?> {
            override fun onResponse(
                call: Call<JsonPrimitive?>,
                response: Response<JsonPrimitive?>
            ) {
                try {
                    val item = response.body()
                    if (item != null && response.isSuccessful) {
                        val base64String = Gson().fromJson(
                            item.asJsonPrimitive,
                            String::class.java
                        )
                        if (base64String != null) {
                            //String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);
                            //photo = base64String
                            val decodedString = Base64.decode(base64String, Base64.DEFAULT)
                            val decodedByte =
                                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                            binding.photoComplaint.setImageBitmap(decodedByte)
                            binding.photoComplaint.visibility = View.VISIBLE
                        }
                    } else binding.photoComplaint.visibility = View.GONE
                } catch (ignored: java.lang.Exception) {
                    val a = ignored.message
                    val sa = 0
                }
            }

            override fun onFailure(call: Call<JsonPrimitive?>, t: Throwable) {}
        })
    }
}