package com.housemanagement.controllers

import android.R.attr.order
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
import com.housemanagement.adapters.ComplaintViewAdapter
import com.housemanagement.databinding.FragmentComplaintsBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.ComplaintsFragment
import com.housemanagement.interfaces.ApiRequests
import com.housemanagement.models.other.ComplaintToServer
import com.housemanagement.models.tables.Complaint
import com.housemanagement.models.tables.Flat
import com.housemanagement.otherclasses.GetDataFromServer
import com.housemanagement.otherclasses.RetrofitService
import com.housemanagement.otherclasses.ShowErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ComplaintsFragmentController {
    private var binding: FragmentComplaintsBinding
    private lateinit var adapter: ComplaintViewAdapter
    private var fragment: ComplaintsFragment
    private var complaints = ArrayList<Complaint>()
    private var flats = ArrayList<Flat>()
    private var db: MainDb
    private var viewLifecycleOwner: LifecycleOwner
    var apiRequests: ApiRequests
    private lateinit var date: LocalDate
    private var token = ""
    private var idHouse = 0
    private var editComplaint = Complaint()
    val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    var file: File? = null
    var delete = false

    constructor(
        binding: FragmentComplaintsBinding,
        fragment: ComplaintsFragment,
        viewLifecycleOwner: LifecycleOwner
    ) {
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        db = MainDb.getDb(binding.root.context)
        getData("complaints")
        apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
    }

    private fun getData(list: String) {
        when (list) {
            "complaints" -> db.getDao().getComplaints().asLiveData().observe(viewLifecycleOwner) {
                //complaints = ArrayList(it.toMutableList().sortedByDescending { it.date })
                complaints = ArrayList(it.toMutableList())
                getData("flats")
            }
            "flats" -> db.getDao().getFlats().asLiveData().observe(viewLifecycleOwner) {
                flats = ArrayList(it.toMutableList())
                initFragment()
            }
        }
    }

    private fun initFragment() {
        binding.complaintsRecyclerView.layoutManager = LinearLayoutManager(fragment.context)
        binding.complaintsRecyclerView.setHasFixedSize(true)
        adapter = ComplaintViewAdapter(complaints, fragment.context, this, null, flats, null)
        binding.complaintsRecyclerView.adapter = adapter
        binding.btnClose.setOnClickListener(View.OnClickListener {
            closeComplaint()
        })
        closeComplaint()
        binding.btnAdd.setOnClickListener(View.OnClickListener { openComplaint(null) })
        binding.btnComplaint.setOnClickListener(View.OnClickListener { sendComplaint() })
        binding.btnDeleteComplaint.setOnClickListener(View.OnClickListener { deleteComplaint() })
        binding.btnDeletePhotoComplaint.setOnClickListener(View.OnClickListener {  })
        binding.btnDeletePhotoComplaint.setOnClickListener(View.OnClickListener {
            delete = true
            file = null
            binding.photoComplaint.visibility = View.GONE
        })
        token =
            binding.root.context.getSharedPreferences("HouseManagement", 0).getString("token", "")
                .toString()
        idHouse =
            binding.root.context.getSharedPreferences("HouseManagement", 0).getInt("house", 0)

    }

    private fun sendComplaint() {
        val status = "Отправлена"
        var idFlat = 0
        //var photoBitmap = binding.photoComplaint.drawable.toBitmapOrNull()
        if (flats.size < 2) idFlat = flats[0].id
        else idFlat = flats[binding.numberFlatSpinner.selectedItemPosition].id
        val description = binding.descriptionComplaint.text.toString()
        if (editComplaint.id == null || editComplaint.id == 0) {
            val complaint =
                ComplaintToServer(
                    0,
                    idFlat,
                    status,
                    description,
                    null,
                    date.toString() + "T00:00:00"
                )
            postComplaintToServer(complaint)
        } else {
            var photo = editComplaint.photo
            if(delete) photo = null
            val complaint =
                ComplaintToServer(
                    editComplaint.id,
                    idFlat,
                    status,
                    description,
                    photo,
                    editComplaint.date.toString() + "T00:00:00"
                )
            if(delete && editComplaint.photo != null)
            {
                apiRequests.deletePhotoComplaint("Bearer $token", complaint.id, photo)
                    .enqueue(object : Callback<JsonObject> {
                        override fun onResponse(
                            call: Call<JsonObject>,
                            response: Response<JsonObject>
                        ) {
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        }

                    })
            }
            putComplaintToServer(complaint)
        }
    }

    private fun postComplaintToServer(complaint: ComplaintToServer) {
        apiRequests.sendComplaint("Bearer $token", complaint)
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
                                val newPhoto = gson.fromJson(
                                    item.asJsonObject.get("photo"),
                                    String::class.java
                                )
                                val newComplaint = Complaint(
                                    id, idFlat, status,
                                    description,
                                    newPhoto,
                                    date
                                )
                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertComplaint(newComplaint)
                                }

                                Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT)
                                    .show()

                                if(file != null)
                                {
                                    val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                                    val body =  MultipartBody.Part.createFormData("photo", file?.name, requestFile)
                                    putPhoto(body, newComplaint.id)
                                }
                            }
                        } else if (response.code() == 401) ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Необходимо переавторизоваться",
                            2
                        )
                        else ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось отправить заявку",
                            2
                        )
                        closeComplaint()
                    } catch (ex: java.lang.Exception) {
                        closeComplaint()
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось отправить заявку",
                            2
                        )
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    closeComplaint()
                    ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        "Не удалось отправить заявку",
                        2
                    )
                }
            })
    }

    private fun putPhoto(file: MultipartBody.Part, id: Int)
    {
        apiRequests.putPhoto("Bearer $token", id, file)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        t.message.toString(),
                        2
                    )
                }
            })
    }

    private fun putComplaintToServer(complaint: ComplaintToServer) {
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
                                Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT)
                                    .show()
                                if((file != null && delete) || file != null)
                                {
                                    val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                                    val body =  MultipartBody.Part.createFormData("photo", file?.name, requestFile)
                                    putPhoto(body, newComplaint.id)
                                }
                            }
                        } else if (response.code() == 401) ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Необходимо переавторизоваться",
                            2
                        )
                        else ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось обновить заявку",
                            2
                        )
                        closeComplaint()
                    } catch (ex: java.lang.Exception) {
                        closeComplaint()
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Не удалось обновить заявку",
                            2
                        )
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    closeComplaint()
                    ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        "Не удалось обновить заявку",
                        2
                    )
                }
            })
    }

    private fun closeComplaint() {
        binding.shadow.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.complaints.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.complaint.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.GONE
        binding.complaint.visibility = View.GONE
        val c =
            fragment.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        c.hideSoftInputFromWindow(fragment.view?.windowToken, 0)
    }

    fun openComplaint(id: Int?) {
        file = null
        binding.shadow.translationZ = 100 * fragment.resources.displayMetrics.density
        binding.complaints.translationZ = 0F * fragment.resources.displayMetrics.density
        binding.complaint.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.VISIBLE
        binding.complaint.visibility = View.VISIBLE
        binding.shadow.isClickable = true

        val numbers = ArrayList<String>()
        for (flat in flats) {
            numbers.add(flat.flatNumber.toString())
        }

        if (numbers.size < 2) {
            binding.numberFlatTextView.setText("Квартира №" + numbers[0])
            binding.numberFlatSpinner.visibility = View.GONE
        } else {
            binding.numberFlatSpinner.adapter = ArrayAdapter(
                fragment.requireContext(),
                android.R.layout.simple_list_item_1,
                numbers
            )
        }
        if (id != null) {
            fillComplaint(id)
            binding.btnDeleteComplaint.visibility = View.VISIBLE
        } else {
            binding.btnDeleteComplaint.visibility = View.GONE
            editComplaint.id = null
            binding.btnComplaint.text = "Отправить заявку"
            GetDataFromServer.getDateNow(binding.root.context)
            date = LocalDate.parse(
                binding.root.context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE)
                    .getString("dateNow", LocalDate.now().toString())
            )
            binding.dateComplaint.text = "Заявка от " + date.format(dtf)
            binding.descriptionComplaint.setText("")
            binding.photoComplaint.visibility = View.GONE
        }
    }

    private fun deleteComplaint() {
        val status = "Отправлена"
        val complaint = Complaint(
            editComplaint.id,
            editComplaint.idFlat,
            status,
            editComplaint.description,
            editComplaint.photo,
            editComplaint.date
        )
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

    private fun fillComplaint(id: Int) {
        val complaint = complaints.firstOrNull { it.id == id }
        editComplaint.id = id
        editComplaint.photo = complaint?.photo
        editComplaint.date = complaint?.date
        editComplaint.idFlat = complaint?.idFlat
        binding.dateComplaint.text = "Заявка от " + complaint?.date?.format(dtf)
        binding.descriptionComplaint.setText(complaint?.description)

        binding.numberFlatSpinner.setSelection(flats.indexOf(flats.firstOrNull { it.id == complaint?.idFlat }))
        if (complaint?.photo == null) binding.photoComplaint.visibility = View.GONE
        else binding.photoComplaint.visibility = View.VISIBLE
        binding.btnComplaint.text = "Сохранить заявку"

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

    fun getPhoto(selectedImage: String) {
        file = File(selectedImage)
        delete = false
    }
}
