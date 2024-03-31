package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criminalintent.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.launch

class CrimeListFragment : Fragment() {
    private val crimeListViewModel : CrimeListViewModel by viewModels()

    private var _binding : FragmentCrimeListBinding? = null
    private lateinit var crime : Crime
    private val binding
        get() = checkNotNull(_binding){
            "Binding is null. Can you see the view?"
        }

//    private var job: Job? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
//    }

//    override fun onStart() {
//        super.onStart()
//        job = viewLifecycleOwner.lifecycleScope.launch {
//            val crimes = crimeListViewModel.loadCrimes()
//            binding.crimeRecyclerView.adapter = CrimeListAdapter(crimes)
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        job?.cancel()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
//        val crimes = crimeListViewModel.crimes
//        val adapter = CrimeListAdapter(crimes)
//        binding.crimeRecyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                crimeListViewModel.crimes.collect{ crimes->
                    binding.crimeRecyclerView.adapter = CrimeListAdapter(crimes)
                }

            }
        }
    }

}