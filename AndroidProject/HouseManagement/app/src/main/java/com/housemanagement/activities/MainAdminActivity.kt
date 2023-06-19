package com.housemanagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.housemanagement.R
import com.housemanagement.databinding.ActivityMainAdminBinding
import com.housemanagement.fragments.*

class MainAdminActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.menu.setGroupCheckable(0, false, true)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.advertisementsAdmin -> {
                    replaceFragment(AdvertisementsAdminFragment())
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                }
                R.id.complaintsAdmin -> {
                    replaceFragment(ComplaintsAdminFragment())
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                }
                R.id.accountAdmin -> {
                    replaceFragment(AccountAdminFragment())
                    binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
                }
            }
            return@setOnItemSelectedListener true
        }
    }
    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}