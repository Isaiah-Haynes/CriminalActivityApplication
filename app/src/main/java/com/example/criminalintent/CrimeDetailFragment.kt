package com.example.criminalintent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.util.Date

private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeDetailFragment : Fragment() {

    private var _binding : FragmentCrimeDetailBinding? = null
    private val args: CrimeDetailFragmentArgs by navArgs()
    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels{
        CrimeDetailViewModelFactory(args.crimeId)
    }

    private val binding
        get() = checkNotNull(_binding){
            "Binding is null. Can you see the view?"
        }

    private val selectSuspect =
        registerForActivityResult(ActivityResultContracts.PickContact()){
            uri: Uri? ->
            // TODO handle for result
            uri?.let{parseContactSelection(it)}
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            crimeTitle.doOnTextChanged{ text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeSuspect.setOnClickListener {
                selectSuspect.launch(null)
            }

            crimeSolved.setOnCheckedChangeListener{ _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }

        }
//---------------------------- HOMEWORK FIVE ----------------------------
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                crimeDetailViewModel.crime.collect{crime ->
                    crime?.let { removeCrime(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                crimeDetailViewModel.crime.collect{crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ){requestKey, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }
    } // end of onViewCreated

    private fun updateUi(crime: Crime){
        binding.apply {
            if(crimeTitle.text.toString() != crime.title){
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeDate.setOnClickListener {
                findNavController().navigate(CrimeDetailFragmentDirections.selectDate(crime.date))
            }
            crimeSolved.isChecked = crime.isSolved

            crimeReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                }
//                startActivity(reportIntent)

                val chooseIntent = Intent.createChooser(
                    reportIntent,
                    getString(R.string.send_report)
                )
                startActivity(chooseIntent)
            }

            crimeSuspect.text = crime.suspect.ifEmpty {
                getString(R.string.crime_suspect_text)
            }

        }
    }

    private fun parseContactSelection(contractUri: Uri){
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val queryCursor = requireActivity().contentResolver.query(
            contractUri,queryFields, null, null, null
        )

        queryCursor?.use{cursor ->
            if(cursor.moveToFirst()){
                val suspect = cursor.getString(0)
                crimeDetailViewModel.updateCrime {oldCrime ->
                    oldCrime.copy(suspect = suspect)
                }
            }
        }
    }


    //---------------------------- HOMEWORK FIVE ----------------------------
    private fun removeCrime(crime: Crime){
        binding.apply {
            crimeDelete.setOnClickListener {
                crimeDetailViewModel.deleteCrime(crime)

                findNavController().navigateUp()
            }
        }
    }

    private fun getCrimeReport(crime : Crime) : String {
        val solvedString = if(crime.isSolved){
            getString(R.string.crime_report_solved)
        }else {
            getString(R.string.crime_report_unsolved)
        }

        val suspectText = if(crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()

        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspectText
        )
    }

}