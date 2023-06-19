package com.housemanagement.db

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.ZoneId

class DateConverter {
    @TypeConverter
    fun fromText(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToText(date: LocalDate?): String? {
        val zoneId: ZoneId = ZoneId.systemDefault()
        //return date?.atStartOfDay(zoneId)?.toEpochSecond()
        return date?.toString()
    }
}