package com.housemanagement.otherclasses

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.housemanagement.activities.LoginActivity
import com.housemanagement.activities.MainActivity
import com.housemanagement.activities.MainAdminActivity
import com.housemanagement.db.MainDb
import com.housemanagement.interfaces.ApiRequests
import com.housemanagement.models.tables.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class GetDataFromServer {
    companion object {
        lateinit var apiRequests: ApiRequests
        lateinit var user: User
        lateinit var context: Context
        lateinit var activity: LoginActivity
        lateinit var lifecycleOwner: LifecycleOwner
        var flatOwner = FlatOwner()
        var flats = ArrayList<Flat>()
        var counters = ArrayList<Counter>()
        var indications = ArrayList<Indication>()
        var payments = ArrayList<Payment>()
        var houses = ArrayList<House>()
        var complaints = ArrayList<Complaint>()
        var advertisements = ArrayList<Advertisement>()
        var services = ArrayList<Service>()
        var settingsServices = ArrayList<SettingsService>()
        val gson = Gson()
        lateinit var db: MainDb
        //lateinit var username: String

        @JvmStatic
        fun get(
            userFromLogin: User,
            context: Context,
            lifecycleOwner: LifecycleOwner,
            activity: LoginActivity
        ) {
            this.user = userFromLogin
            this.lifecycleOwner = lifecycleOwner
            this.activity = activity
            this.context = context
            apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
            db = MainDb.getDb(context)

            val role = userFromLogin.role
            if (role == "FlatOwner") {
                getFlatOwner(user.idFlatOwner)
                getFlats(user.idFlatOwner)
                getCounters(user.idFlatOwner)
                getIndications(user.idFlatOwner)
                getPayments(user.idFlatOwner)
                getComplaintsByFlatOwner(user.idFlatOwner)
                getAdvertesiments()
                getServices()
                getSettingsServices()
                val i = Intent(Companion.context, MainActivity::class.java)
                Companion.context.startActivity(i)
                activity.finish()
            }
            if (role == "HouseAdmin") {
                getAllAdvertesiments()
                getHouses()
                getFlatOwners()
                getAllFlats()
                getComplaints()
                val i = Intent(Companion.context, MainAdminActivity::class.java)
                Companion.context.startActivity(i)
                activity.finish()
            }
        }

        private fun getFlatOwner(idFlatOwner: Int) {
            apiRequests.getFlatOwnerById("Bearer ${user.token}", idFlatOwner)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        try {
                            val item = response.body()
                            if (item != null && response.isSuccessful) {
                                val id = gson.fromJson(
                                    item.asJsonObject.get("id"),
                                    Int::class.java
                                )
                                val fullName = gson.fromJson(
                                    item.asJsonObject.get("fullName"),
                                    String::class.java
                                )
                                val email = gson.fromJson(
                                    item.asJsonObject.get("email"),
                                    String::class.java
                                )
                                val phoneNumber = gson.fromJson(
                                    item.asJsonObject.get("phoneNumber"),
                                    String::class.java
                                )
                                val idHouse = gson.fromJson(
                                    item.asJsonObject.get("idHouse"),
                                    Int::class.java
                                )
                                flatOwner = FlatOwner(
                                    id,
                                    fullName,
                                    phoneNumber,
                                    email,
                                    idHouse
                                )
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertFlatOwner(flatOwner)
                                }
                            }

                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getFlatOwners() {
            apiRequests.getFlatOwners("Bearer ${user.token}", user.id)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            val flatOwners = ArrayList<FlatOwner>()
                            if (array != null) {
                                for (item in array) {
                                    val id = gson.fromJson(
                                        item.asJsonObject.get("id"),
                                        Int::class.java
                                    )
                                    val fullName = gson.fromJson(
                                        item.asJsonObject.get("fullName"),
                                        String::class.java
                                    )
                                    val email = gson.fromJson(
                                        item.asJsonObject.get("email"),
                                        String::class.java
                                    )
                                    val phoneNumber = gson.fromJson(
                                        item.asJsonObject.get("phoneNumber"),
                                        String::class.java
                                    )
                                    val idHouse = gson.fromJson(
                                        item.asJsonObject.get("idHouse"),
                                        Int::class.java
                                    )
                                    flatOwner = FlatOwner(
                                        id,
                                        fullName,
                                        phoneNumber,
                                        email,
                                        idHouse
                                    )
                                    flatOwners.add(flatOwner)
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertFlatOwners(flatOwners)
                                }
                            }

                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getHouses() {
            apiRequests.getHouses("Bearer ${user.token}", user.id)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            if (array != null) {
                                for (item in array) {
                                    houses.add(gson.fromJson(item, House::class.java))
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertHouses(houses)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                            var d = 0
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                        var d = 0
                    }
                })
        }
        private fun getFlats(idFlatOwner: Int?) {
            apiRequests.getFlatsByIdFlatOwner("Bearer ${user.token}", idFlatOwner)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            if (array != null) {
                                for (item in array) {
                                    flats.add(gson.fromJson(item, Flat::class.java))
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertFlats(flats)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getAllFlats() {
            apiRequests.getFlats("Bearer ${user.token}", user.id)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            if (array != null) {
                                for (item in array) {
                                    flats.add(gson.fromJson(item, Flat::class.java))
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertFlats(flats)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getCounters(idFlatOwner: Int?) {
            apiRequests.getCountersByIdFlatOwnerAndUsed("Bearer ${user.token}", idFlatOwner)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            if (array != null) {
                                for (item in array) {
                                    val dateLastVerification = LocalDate.parse(
                                        gson.fromJson(
                                            item.asJsonObject.get("dateLastVerification"),
                                            String::class.java
                                        ).removeRange(10, 19)
                                    )
                                    val dateNextVerification = LocalDate.parse(
                                        gson.fromJson(
                                            item.asJsonObject.get("dateNextVerification"),
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
                                    val idService = gson.fromJson(
                                        item.asJsonObject.get("idService"),
                                        Int::class.java
                                    )
                                    val idHouse = gson.fromJson(
                                        item.asJsonObject.get("idHouse"),
                                        Int::class.java
                                    )
                                    val iIMDOrGHMD = gson.fromJson(
                                        item.asJsonObject.get("IMDOrGHMD"),
                                        Boolean::class.java
                                    )
                                    val used = gson.fromJson(
                                        item.asJsonObject.get("used"),
                                        Boolean::class.java
                                    )
                                    val number = gson.fromJson(
                                        item.asJsonObject.get("number"),
                                        String::class.java
                                    )
                                    counters.add(
                                        Counter(
                                            id,
                                            number,
                                            used,
                                            idFlat,
                                            idService,
                                            idHouse,
                                            iIMDOrGHMD,
                                            dateLastVerification,
                                            dateNextVerification
                                        )
                                    )
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertCounters(counters)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }

                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getIndications(idFlatOwner: Int?) {
            apiRequests.getIndicationsByIdFlatOwner("Bearer ${user.token}", idFlatOwner)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            if (array != null) {
                                for (item in array) {
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
                                    indications.add(Indication(id, idCounter, date, value))
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertIndications(indications)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getPayments(idFlatOwner: Int?) {
            apiRequests.getPaymentsByIdFlatOwner("Bearer ${user.token}", idFlatOwner)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            if (array != null) {
                                for (item in array) {
                                    payments.add(gson.fromJson(item, Payment::class.java))
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertPayments(payments)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getComplaintsByFlatOwner(idFlatOwner: Int?) {
            apiRequests.getComplaintsByIdFlatOwner("Bearer ${user.token}", idFlatOwner)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
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
                                    complaints.add(
                                        Complaint(
                                            id,
                                            idFlat,
                                            status,
                                            description,
                                            photo,
                                            date
                                        )
                                    )
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertComplaints(complaints)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getAllAdvertesiments() {
            apiRequests.getAdvertisements("Bearer ${user.token}", user.id)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
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
                                    advertisements.add(
                                        Advertisement(
                                            id,
                                            description,
                                            date,
                                            idHouseItem
                                        )
                                    )
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertAdvertisements(advertisements)
                                }
                            }
                        } catch (ex: Exception) {
                            var d = ex.message
                            var sd = 0
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getAdvertesiments() {
            apiRequests.getAdvertisements("Bearer ${user.token}", user.idFlatOwner)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
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
                                    advertisements.add(
                                        Advertisement(
                                            id,
                                            description,
                                            date,
                                            idHouseItem
                                        )
                                    )
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertAdvertisements(advertisements)
                                }
                            }
                        } catch (ex: Exception) {
                            var d = ex.message
                            var sd = 0
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getServices() {
            apiRequests.getServices("Bearer ${user.token}")
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            if (array != null) {
                                for (item in array) {
                                    services.add(gson.fromJson(item, Service::class.java))
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertServices(services)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getSettingsServices() {
            apiRequests.getSettingsServices("Bearer ${user.token}", user.idFlatOwner)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
                            if (array != null) {
                                for (item in array) {
                                    settingsServices.add(
                                        gson.fromJson(
                                            item,
                                            SettingsService::class.java
                                        )
                                    )
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertSettingsServices(settingsServices)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        private fun getComplaints() {
            apiRequests.getComplaints("Bearer ${user.token}", user.id)
                .enqueue(object : Callback<JsonArray> {
                    override fun onResponse(
                        call: Call<JsonArray>,
                        response: Response<JsonArray>
                    ) {
                        try {
                            val array = response.body()
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
                                    complaints.add(
                                        Complaint(
                                            id,
                                            idFlat,
                                            status,
                                            description,
                                            photo,
                                            date
                                        )
                                    )
                                }
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().insertComplaints(complaints)
                                }
                            }
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                        //error()
                    }
                })
        }
        fun getDateNow(contextItem: Context) {
            val token = contextItem.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE)
                .getString("token", "").toString()
            apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
            val settings = contextItem.getSharedPreferences(
                "HouseManagement",
                Context.MODE_PRIVATE
            ).edit()
            apiRequests.getDateNow("Bearer $token")
                .enqueue(object : Callback<JsonPrimitive> {
                    override fun onResponse(
                        call: Call<JsonPrimitive>,
                        response: Response<JsonPrimitive>
                    ) {
                        try {
                            val dateNow = LocalDate.parse(
                                Gson().fromJson(
                                    response.body()?.asJsonPrimitive,
                                    String::class.java
                                ).removeRange(10, 33)
                            )
                            settings.putString("dateNow", dateNow.toString()).apply()
                        } catch (ex: Exception) {
                            //error()
                        }
                    }

                    override fun onFailure(call: Call<JsonPrimitive>, t: Throwable) {
                        //error()
                    }
                })
        }

        @JvmStatic
        fun getStringPhoto(contextItem: Context, id: Int) {
            val token = contextItem.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE)
                .getString("token", "").toString()
            apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
            val settings = contextItem.getSharedPreferences(
                "HouseManagement",
                Context.MODE_PRIVATE
            ).edit()
            apiRequests.getPhoto("Bearer $token", id)
                .enqueue(object : Callback<JsonPrimitive> {
                    override fun onResponse(
                        call: Call<JsonPrimitive>,
                        response: Response<JsonPrimitive>
                    ) {
                        try {
                            val item = response.body()
                            if (item != null && response.isSuccessful) {
                                val base64 =
                                    Gson().fromJson(
                                        response.body()?.asJsonPrimitive,
                                        String::class.java
                                    )
                                settings.putString("photo", base64).apply()
                            }
                        } catch (ex: Exception) {
                            //error()
                            var d = ex.message
                            var ds = 0
                        }
                    }

                    override fun onFailure(call: Call<JsonPrimitive>, t: Throwable) {
                        //error()
                        var d = 0
                    }
                })
        }
    }
}