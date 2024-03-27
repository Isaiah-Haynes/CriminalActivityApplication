package com.example.criminalintent

import android.content.Context

class CrimeRepository private constructor(context: Context) {

    companion object{
        private var INSTANCE : CrimeRepository? = null

        fun initialize(context : Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            } // making a singleton
        }
    }

}