package com.example.movie.presentation.ui.profile.editProfile

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movie.R
import com.example.movie.data.model.remote.Account
import com.example.movie.databinding.FragmentEditProfileBinding
import com.example.movie.db.BaseFragment
import com.example.movie.presentation.ui.profile.myProfile.ProfileViewModel
import com.example.movie.utils.LocaleHelper
import com.example.movie.utils.loadImage
import com.shashank.sony.fancytoastlib.FancyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {

    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val editProfileViewModel by viewModels<EditProfileViewModel>()
    private lateinit var countryAdapter: ArrayAdapter<String>
    private lateinit var genderAdapter: ArrayAdapter<String>
    private var selectedCountry: String? = null
    private var selectedGender: String? = null
    private var selectedLanguage: String? = null
    private var currentLanguage: String? = null
    private var profileImageUrl: String? = null
    private var profileEmail: String? = ""
    private var initialAccount: Account? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStaticSpinners()
        observeData()
        editProfileViewModel.getEmailFromPreferences()
        profileViewModel.getDataFromFireStore()
        setUpClicks()
        selectProfileImage()
        setupLanguageSpinner()
    }

    private fun initStaticSpinners() {
        val countries = resources.getStringArray(R.array.countries_array)
        countryAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, countries).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerCountry.adapter = countryAdapter
        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCountry = countryAdapter.getItem(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val genders = resources.getStringArray(R.array.gender_array)
        genderAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, genders).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerGender.adapter = genderAdapter
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGender = genderAdapter.getItem(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observeData() {
        profileViewModel.data.observe(viewLifecycleOwner) { account ->
            binding.EditTextEditFullName.setText(account.fullName)
            binding.EditTextEditNickname.setText(account.nickName)
            account.image?.let {
                binding.imageViewProfile.loadImage(it)
                profileImageUrl = it
            }
            account.country?.let { country ->
                val pos = countryAdapter.getPosition(country)
                if (pos >= 0) binding.spinnerCountry.setSelection(pos)
                selectedCountry = country
            }
            account.gender?.let { gender ->
                val pos = genderAdapter.getPosition(gender)
                if (pos >= 0) binding.spinnerGender.setSelection(pos)
                selectedGender = gender
            }

            initialAccount = account
        }

        editProfileViewModel.email.observe(viewLifecycleOwner) { profileEmail = it }
    }

    private fun selectProfileImage() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                binding.imageViewProfile.setImageURI(it)
                profileImageUrl = it.toString()
            }
        }
        binding.buttonEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setUpClicks() {
        binding.imageViewArrowEditToHome.setOnClickListener { findNavController().popBackStack() }

        binding.buttonUpdate.setOnClickListener {
            val fullName = binding.EditTextEditFullName.text.toString().trim()
            val nickName = binding.EditTextEditNickname.text.toString().trim()

            if (fullName.isBlank() || nickName.isBlank()) {
                showFancyToast("Please fill all required fields.", FancyToast.WARNING)
                return@setOnClickListener
            }

            val updatedAccount = Account(
                fullName = fullName,
                nickName = nickName,
                email = profileEmail,
                country = selectedCountry ?: countryAdapter.getItem(0),
                gender = selectedGender ?: genderAdapter.getItem(0),
                image = profileImageUrl
            )

            if (updatedAccount != initialAccount) {
                editProfileViewModel.updateFireStore(updatedAccount)
                showFancyToast("Updated successfully!", FancyToast.SUCCESS)
            }

            if (selectedLanguage != currentLanguage) {
                LocaleHelper.saveLanguage(requireContext(), selectedLanguage ?: "en")
                LocaleHelper.setLocale(requireContext(), selectedLanguage ?: "en")
                requireActivity().recreate()
            }
        }
    }

    private fun setupLanguageSpinner() {
        val languages = listOf("English", "Azərbaycan")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        currentLanguage = LocaleHelper.getSavedLanguage(requireContext())
        selectedLanguage = currentLanguage

        val selectedIndex = if (currentLanguage == "az") 1 else 0
        binding.spinnerLanguage.setSelection(selectedIndex)

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedLanguage = if (position == 1) "az" else "en"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
