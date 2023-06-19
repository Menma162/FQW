package com.housemanagement.controllers

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.housemanagement.R
import com.housemanagement.activities.LoginActivity
import com.housemanagement.databinding.FragmentAccountAdminBinding
import com.housemanagement.db.MainDb
import com.housemanagement.fragments.*
import com.housemanagement.interfaces.ApiRequests
import com.housemanagement.models.other.UserLogin
import com.housemanagement.models.other.UserToServer
import com.housemanagement.models.tables.FlatOwner
import com.housemanagement.models.tables.User
import com.housemanagement.otherclasses.RetrofitService
import com.housemanagement.otherclasses.ShowErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class AccountAdminFragmentController {
    private var binding: FragmentAccountAdminBinding
    private var fragment: AccountAdminFragment
    private var viewLifecycleOwner: LifecycleOwner
    private val db: MainDb
    private var user = User()
    var apiRequests: ApiRequests
    private var token = ""
    constructor(binding: FragmentAccountAdminBinding, fragment: AccountAdminFragment, viewLifecycleOwner: LifecycleOwner){
        this.binding = binding
        this.fragment = fragment
        this.viewLifecycleOwner = viewLifecycleOwner
        apiRequests = RetrofitService().retrofit.create(ApiRequests::class.java)
        db = MainDb.getDb(binding.root.context)
        db.getDao().getUsers().asLiveData().observe(viewLifecycleOwner){
            if(it.toMutableList().isNotEmpty()) user = it.toMutableList().first()
            initFragment()
        }
    }

    private fun initFragment() {
        if (user.email != null) binding.email.text = user.email
        binding.exit.setOnClickListener(View.OnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                db.clearAllTables()
            }
            binding.root.context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE).edit().clear().commit()
            binding.root.context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE).edit().clear().apply()
            //fragment.activity?.startActivity(Intent(fragment.context, LoginActivity::class.java))
            exitProcess(0)
        })
        binding.editEmail.setOnClickListener(View.OnClickListener {
            openEditEmail()
        })
        binding.editPassword.setOnClickListener(View.OnClickListener {
            openEditPassword()
        })
        binding.btnClose.setOnClickListener(View.OnClickListener {
            closeLayout()
        })
        closeLayout()
        binding.btnEdit.setOnClickListener(View.OnClickListener { editProfile() })
        token =
            binding.root.context.getSharedPreferences("HouseManagement", 0).getString("token", "")
                .toString()
    }

    private fun editProfile() {
        if(binding.emailLayout.visibility == View.VISIBLE){
            editEmail()
        }
        else {
            editPassword()
        }
    }

    private fun editPassword() {
        val password = binding.editTextPassword.text.toString()
        val repassword = binding.editTextRePassword.text.toString()
        if(password != repassword) ShowErrorMessage.show(
            binding.root.context,
            "Ошибка",
            "Пароли не совпадают",
            2
        )
        else if (password.length < 8) ShowErrorMessage.show(
            binding.root.context,
            "Ошибка",
            "Длина пароля должна быть не меньше 8",
            2
        )
        else{
            val userToServer = UserToServer(user.id, user.email, password, user.role, user.idFlatOwner)
            apiRequests.putUserPassword("Bearer $token", userToServer.id, userToServer)
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
                                    val newUser = gson.fromJson(item, User::class.java)
                                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                        db.getDao().updateUser(newUser)
                                    }
                                    binding.root.context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE).edit().putString("password", newUser.password).apply()
                                    Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else if (response.code() == 401) ShowErrorMessage.show(binding.root.context, "Ошибка", "Необходимо переавторизоваться", 2)
                            else ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить пароль", 2)
                            closeLayout()
                        } catch (ex: java.lang.Exception) {
                            closeLayout()
                            ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить пароль", 2)
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        closeLayout()
                        ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить пароль", 2)
                    }
                })

            loginUser(user.email, user.password)
            binding.root.context.getSharedPreferences("HouseManagement", 0).edit().putString("token", user.token).apply()
        }
    }

    private fun loginUser(email: String, pass: String) {
        val loginUser = UserLogin(email, pass)
        apiRequests.login(loginUser).enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                try {
                    val result = response.body()?.get("token")
                    val gson = Gson()
                    if (response.code() == 200) {
                        token = gson.fromJson(result, String::class.java)
                        val newUser = user
                        newUser.token = token
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            db.getDao().updateUser(user)
                        }
                    }
                } catch (ex: java.lang.Exception) {
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }
        })
    }

    private fun editEmail() {
        val email = binding.editTextEmail.text.toString()
        val userToServer = UserToServer(user.id, email, user.password, user.role, user.idFlatOwner)
        if(!isEmailValid(email)) ShowErrorMessage.show(
            binding.root.context,
            "Ошибка",
            "Некорректный ввод поля email",
            2
        ) else {
            apiRequests.putUserLogin("Bearer $token", userToServer.id, userToServer)
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
                                    val newUser = gson.fromJson(item, User::class.java)
                                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                        db.getDao().updateUser(newUser)
                                    }
                                    binding.root.context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE).edit().putString("username", newUser.email).apply()
                                    Toast.makeText(binding.root.context, "Успешно", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else if (response.code() == 401) ShowErrorMessage.show(binding.root.context, "Ошибка", "Необходимо переавторизоваться", 2)
                            else if (response.code() == 500 && response.errorBody() != null) {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                ShowErrorMessage.show(binding.root.context, "Ошибка", jObjError.getString("message"), 2)
                            }
                            else ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить email", 2)
                            closeLayout()
                        } catch (ex: java.lang.Exception) {
                            closeLayout()
                            ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить email", 2)
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        closeLayout()
                        ShowErrorMessage.show(binding.root.context, "Ошибка", "Не удалось обновить email", 2)
                    }
                })
        }
    }

    private fun openEditPassword() {
        binding.shadow.translationZ = 100 * fragment.resources.displayMetrics.density
        binding.profile.translationZ = 0F * fragment.resources.displayMetrics.density
        binding.editProfileLayout.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.VISIBLE
        binding.editProfileLayout.visibility = View.VISIBLE
        binding.shadow.isClickable = true
        binding.emailLayout.visibility = View.GONE
        binding.passwordLayout.visibility = View.VISIBLE
    }

    private fun openEditEmail() {
        binding.shadow.translationZ = 100 * fragment.resources.displayMetrics.density
        binding.profile.translationZ = 0F * fragment.resources.displayMetrics.density
        binding.editProfileLayout.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.VISIBLE
        binding.editProfileLayout.visibility = View.VISIBLE
        binding.shadow.isClickable = true
        binding.emailLayout.visibility = View.VISIBLE
        binding.passwordLayout.visibility = View.GONE
        binding.editTextEmail.setText(user.email)
    }

    private fun closeLayout() {
        binding.shadow.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.profile.translationZ = 150 * fragment.resources.displayMetrics.density
        binding.editProfileLayout.translationZ = 0 * fragment.resources.displayMetrics.density
        binding.shadow.visibility = View.GONE
        binding.editProfileLayout.visibility = View.GONE
        val c =
            fragment.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        c.hideSoftInputFromWindow(fragment.view?.windowToken, 0)
    }

    fun isEmailValid(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }
}