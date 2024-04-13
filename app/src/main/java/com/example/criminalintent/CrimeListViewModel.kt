package com.example.criminalintent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//private const val TAG = "CrimeListViewModel"
class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())

//    val crimes = mutableListOf<Crime>()
//    val crimes = crimeRepository.getCrimes()
    val crimes: StateFlow<List<Crime>>
    get() = _crimes.asStateFlow()
    init {

        viewModelScope.launch {
            crimeRepository.getCrimes().collect{
                _crimes.value = it
            }
        }
    }

    suspend fun addCrime(crime: Crime){
        crimeRepository.addCrime(crime)
    }

//    suspend fun deleteCrime(crime: Crime){
//        crimeRepository.deleteCrime(crime)
//    }

}