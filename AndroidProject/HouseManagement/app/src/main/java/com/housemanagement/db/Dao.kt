package com.housemanagement.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.housemanagement.models.tables.*
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface Dao {
    @Query("SELECT * FROM User")
    fun getUsers() : Flow<List<User>>

    @Query("SELECT * FROM FlatOwner")
    fun getFlatOwners() : Flow<List<FlatOwner>>
    @Query("SELECT * FROM Flat")
    fun getFlats() : Flow<List<Flat>>
    @Query("SELECT * FROM Counter")
    fun getCounters() : Flow<List<Counter>>
    @Query("SELECT * FROM Indication")
    fun getIndications() : Flow<List<Indication>>
    @Query("SELECT * FROM Payment")
    fun getPayments() : Flow<List<Payment>>
    @Query("SELECT * FROM Complaint")
    fun getComplaints() : Flow<List<Complaint>>
    @Query("SELECT * FROM Advertisement")
    fun getAdvertisements() : Flow<List<Advertisement>>
    @Query("SELECT * FROM Service")
    fun getServices() : Flow<List<Service>>
    @Query("SELECT * FROM SettingsService")
    fun getSettingsServices() : Flow<List<SettingsService>>
    @Query("SELECT * FROM House")
    fun getHouses() : Flow<List<House>>


    @Insert
    fun insertUser(user: User)
    @Insert
    fun insertFlatOwner(flatOwner: FlatOwner)
    @Insert
    fun insertFlatOwners(flatOwners: List<FlatOwner>)
    @Insert
    fun insertFlats (flats: List<Flat>)
    @Insert
    fun insertHouses (flats: List<House>)
    @Insert
    fun insertCounters (flats: List<Counter>)
    @Insert
    fun insertIndications (flats: List<Indication>)
    @Insert
    fun insertIndication (indication: Indication)
    @Insert
    fun insertPayments (flats: List<Payment>)
    @Insert
    fun insertComplaints (flats: List<Complaint>)
    @Insert
    fun insertComplaint (complaint: Complaint)
    @Insert
    fun insertAdvertisement (advertisement: Advertisement)
    @Insert
    fun insertAdvertisements (flats: List<Advertisement>)
    @Insert
    fun insertServices (services: List<Service>)
    @Insert
    fun insertSettingsServices (settingsServices: List<SettingsService>)
    @Insert
    fun insertPhotos (photos: List<Photo>)

    @Update
    fun updateUser(user: User)
    @Update
    fun updateIndication(indication: Indication)
    @Update
    fun updateComplaint(complaint: Complaint)
    @Update
    fun updateAdvertisement(advertisement: Advertisement)
    @Update
    fun updateFlatOwner(flatOwner: FlatOwner)

    @Delete
    fun deleteComplaint(complaint: Complaint)
    @Delete
    fun deleteAdvertisement(advertisement: Advertisement)
    @Delete
    fun deleteUser(user: User)
}