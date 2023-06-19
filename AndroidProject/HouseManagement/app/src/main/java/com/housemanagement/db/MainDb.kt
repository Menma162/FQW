package com.housemanagement.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.housemanagement.models.tables.Advertisement
import com.housemanagement.models.tables.Complaint
import com.housemanagement.models.tables.Counter
import com.housemanagement.models.tables.Flat
import com.housemanagement.models.tables.FlatOwner
import com.housemanagement.models.tables.House
import com.housemanagement.models.tables.Indication
import com.housemanagement.models.tables.Payment
import com.housemanagement.models.tables.Photo
import com.housemanagement.models.tables.Service
import com.housemanagement.models.tables.SettingsService
import com.housemanagement.models.tables.User

@Database (entities = [Advertisement::class, Complaint::class, Counter::class,
                      Flat::class, FlatOwner::class, Indication::class,
                      Payment::class, Service::class, SettingsService::class,
                      User::class, House::class, Photo::class], version = 23)
abstract class MainDb : RoomDatabase() {
    abstract fun getDao(): Dao
    companion object{
        fun getDb(context: Context) : MainDb{
            return Room.
            databaseBuilder(context.applicationContext, MainDb::class.java,"housemanagement.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}