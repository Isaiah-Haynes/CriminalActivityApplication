package com.example.criminalintent

import android.content.Context
import androidx.room.Room
import database.CrimeDatabase
import database.migration_1_2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    )
//        .createFromAsset(DATABASE_NAME)
        .addMigrations(migration_1_2)
        .build()

    /*suspend*/ fun getCrimes() : Flow<List<Crime>> = database.crimeDao().getCrimes()

    suspend fun getCrime(id:UUID) : Crime = database.crimeDao().getCrime(id)

    /*suspend*/ fun updateCrime(crime: Crime){
        coroutineScope.launch {
            database.crimeDao().updateCrime(crime)
        }

    }

    suspend fun addCrime(crime: Crime){
        database.crimeDao().addCrime(crime)
    }

    //---------------------------- HOMEWORK FIVE ----------------------------
    fun deleteCrime(crime: Crime){
        coroutineScope.launch {
            database.crimeDao().deleteCrime(crime)
        }

    }

    companion object{
        private var INSTANCE : CrimeRepository? = null

        fun initialize(context : Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            } // making a singleton
        }

        fun get(): CrimeRepository{
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

}