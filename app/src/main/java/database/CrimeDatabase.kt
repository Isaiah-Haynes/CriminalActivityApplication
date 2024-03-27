package database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.criminalintent.Crime

@Database (entities = [Crime::class], version = 1)

abstract class CrimeDatabase : RoomDatabase() {

    abstract fun  crimeDao() : CrimeDao
}