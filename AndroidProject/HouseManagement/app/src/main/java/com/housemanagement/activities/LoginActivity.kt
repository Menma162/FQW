package com.housemanagement.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.housemanagement.databinding.ActivityLoginBinding
import com.housemanagement.db.MainDb
import com.housemanagement.interfaces.ApiRequests
import com.housemanagement.models.other.UserLogin
import com.housemanagement.models.tables.*
import com.housemanagement.otherclasses.GetDataFromServer
import com.housemanagement.otherclasses.RetrofitService
import com.housemanagement.otherclasses.ShowErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var settings: SharedPreferences.Editor

    lateinit var token: String
    lateinit var user: User
    lateinit var db: MainDb

    lateinit var retrofitService: RetrofitService
    lateinit var userApi: ApiRequests
    var count = 0

    var usernameLogin = ""
    var passwordLogin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = MainDb.getDb(binding.root.context)
        lifecycleScope.launch(Dispatchers.IO) {
            db.clearAllTables()
        }
        user = User()
        sharedPreferences = applicationContext.getSharedPreferences("HouseManagement", MODE_PRIVATE)
        settings = sharedPreferences.edit()
        retrofitService = RetrofitService()
        userApi = retrofitService.retrofit.create(ApiRequests::class.java)
        binding.editTextLogin.setText("")
        binding.editTextPassword.setText("")
        binding.errorTextView.visibility = View.GONE
        checkUser()
        binding.buttonLogin.setOnClickListener(View.OnClickListener {
            btnLogin()
        })
    }

    private fun btnLogin() {
        usernameLogin = binding.editTextLogin.text.toString()
        passwordLogin = binding.editTextPassword.text.toString()

        count = 0
        loginUser(usernameLogin, passwordLogin)
        db.getDao().getUsers().asLiveData().observe(this) {
            if (it.toMutableList().isNotEmpty()) user = it.toMutableList().first()
            nextStep()
        }
    }

    private fun nextStep() {
        if (user.token != null && user.token != "")
            getUser(usernameLogin, passwordLogin)
        db.getDao().getUsers().asLiveData().observe(this) {
            if (it.toMutableList().isNotEmpty()) user = it.toMutableList().first()
            nextStep2()
        }
    }

    private fun nextStep2() {
        if (user.email != null && user.email != "") {
            count++
            if (count == 1) {
                settings.putString("token", user.token)
                settings.putString("username", user.email)
                settings.putString("password", user.password)
                settings.putString("role", user.role)
                if (user.role.equals("FlatOwner"))
                    settings.putInt("idFlatOwner", user.idFlatOwner)
                settings.commit()
                settings.apply()
                GetDataFromServer.get(
                    user,
                    binding.root.context,
                    this@LoginActivity,
                    this
                )
            }
        }
    }

    private fun checkUser() {
        count = 0
        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")
        if(username != null && password != null && username != "" && password != ""){
            usernameLogin = username
            passwordLogin = password
            clearSettings()
            loginUser(username, password)
            db.getDao().getUsers().asLiveData().observe(this) {
                if (it.toMutableList().isNotEmpty()) user = it.toMutableList().first()
                nextStep()
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        //val loginUser = UserLogin(binding.editTextLogin.text.toString(), binding.editTextPassword.text.toString())
        val loginUser = UserLogin(email, pass)
        userApi.login(loginUser).enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                try {
                    val result = response.body()?.get("token")
                    val gson = Gson()
                    if (response.code() == 200) {
                        token = gson.fromJson(result, String::class.java)
                        val user = User("id", null, null, null, null, token)
                        lifecycleScope.launch(Dispatchers.IO) {
                            db.getDao().insertUser(user)
                        }
                    } else if (response.code() == 401) {
                        binding.errorTextView.visibility =
                            View.VISIBLE
                        clearSettings()
                    } else {
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Код ошибки: " + response.code(),
                            1
                        )
                        clearSettings()
                    }
                } catch (ex: java.lang.Exception) {
                    if (response.code() != 200)
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Ошибка обработки полученных данных с сервера",
                            1
                        )
                    else ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        "Код ошибки: " + response.code(),
                        1
                    )
                    clearSettings()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                ShowErrorMessage.show(
                    binding.root.context,
                    "Ошибка",
                    "Невозможно получить данные с сервера",
                    1
                )
                clearSettings()
            }
        })
    }

    private fun getUser(username: String, pass: String) {
        userApi.getUser("Bearer $token", username)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    try {
                        val gson = Gson()
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null) {
                                val id = gson.fromJson(
                                    result.asJsonObject.get("id"),
                                    String::class.java
                                )
                                val role = gson.fromJson(
                                    result.asJsonObject.get("role"),
                                    String::class.java
                                )


//                                val itemIdHouses =
//                                    result.getAsJsonArray("idHouses")
//                                val idHouses = ArrayList<Int>()
//
//                                for (item in itemIdHouses) {
//                                    idHouses.add(gson.fromJson(item, Int::class.java))
//                                }

                                val idFlatOwner = gson.fromJson(
                                    result.asJsonObject.get("idFlatOwner"),
                                    Int::class.java
                                )
                                val newUser =
                                    User(id, username, pass, role, idFlatOwner, token)
                                lifecycleScope.launch(Dispatchers.IO) {
                                    db.getDao().deleteUser(user)
                                    try {
                                        db.getDao().insertUser(newUser)
                                    } catch (ex: Exception) {

                                    }
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        ShowErrorMessage.show(
                            binding.root.context,
                            "Ошибка",
                            "Невозможно получить данные с сервера",
                            1
                        )
                        clearSettings()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    ShowErrorMessage.show(
                        binding.root.context,
                        "Ошибка",
                        "Невозможно получить данные с сервера",
                        1
                    )
                    clearSettings()
                }
            })
    }

    fun clearSettings() {
        settings.clear().apply()
        lifecycleScope.launch(Dispatchers.IO) {
            db.getDao().deleteUser(user)
        }
    }


    override fun onBackPressed() {
        return
    }
}