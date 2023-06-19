package com.housemanagement.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.housemanagement.R
import com.housemanagement.databinding.ActivityMainBinding
import com.housemanagement.fragments.*


class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    companion object {
        @JvmStatic lateinit var contextOfApplication: Context
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.menu.setGroupCheckable(0, false, true)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(NewsFragment())
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                }
                R.id.payments -> {
                    replaceFragment(PaymentsFragment())
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                }
                R.id.indications -> {
                    replaceFragment(IndicationsFragment())
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                }
                R.id.complaints -> {
                    replaceFragment(ComplaintsFragment())
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                }
                R.id.account -> {
                    replaceFragment(AccountFragment())
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                }
            }
            return@setOnItemSelectedListener true
        }
        contextOfApplication = applicationContext
    }

    fun replaceFragment(fragment:Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //call super
        super.onActivityResult(requestCode, resultCode, data)
    }
}