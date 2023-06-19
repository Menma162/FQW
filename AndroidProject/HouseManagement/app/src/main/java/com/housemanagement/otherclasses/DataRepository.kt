package com.housemanagement.otherclasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.housemanagement.db.Dao
import com.housemanagement.models.tables.Advertisement
import com.housemanagement.models.tables.Counter
import com.housemanagement.models.tables.Indication
import com.housemanagement.models.tables.Payment
import com.housemanagement.models.tables.Service
import com.housemanagement.models.tables.SettingsService

class DataRepository {
    var dao: Dao
    constructor(dao: Dao){
        this.dao = dao
    }
    fun listAdvertisements(): LiveData<List<Advertisement>> {return dao.getAdvertisements().asLiveData()}
    fun listIndications(): LiveData<List<Indication>> {return dao.getIndications().asLiveData()}
    fun listServices(): LiveData<List<Service>> {return dao.getServices().asLiveData()}
    fun listPayments(): LiveData<List<Payment>> {return dao.getPayments().asLiveData()}
    fun listCounters(): LiveData<List<Counter>> {return dao.getCounters().asLiveData()}
    fun listSettingsServices(): LiveData<List<SettingsService>> {return dao.getSettingsServices().asLiveData()}
}