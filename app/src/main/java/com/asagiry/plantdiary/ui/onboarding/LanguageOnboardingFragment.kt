package com.asagiry.plantdiary.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.asagiry.plantdiary.PlantDiaryApp
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.databinding.FragmentLanguageOnboardingBinding
import com.asagiry.plantdiary.ui.common.playEntranceMotion

class LanguageOnboardingFragment : Fragment() {
    private var _binding: FragmentLanguageOnboardingBinding? = null
    private val binding get() = _binding!!
    private var selectedLanguageTag: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLanguageOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseRussianButton.setOnClickListener {
            updateSelection("ru")
        }
        binding.chooseEnglishButton.setOnClickListener {
            updateSelection("en")
        }
        binding.continueButton.setOnClickListener {
            confirmSelection()
        }

        binding.onboardingContent.playEntranceMotion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateSelection(languageTag: String) {
        selectedLanguageTag = languageTag
        binding.continueButton.isEnabled = true
        binding.chooseRussianButton.isEnabled = languageTag != "ru"
        binding.chooseEnglishButton.isEnabled = languageTag != "en"
    }

    private fun confirmSelection() {
        val languageTag = selectedLanguageTag ?: return
        val app = requireActivity().application as PlantDiaryApp
        app.saveLanguage(languageTag)
        findNavController().navigate(
            R.id.action_languageOnboardingFragment_to_plantsFragment,
            null,
            navOptions {
                popUpTo(R.id.languageOnboardingFragment) { inclusive = true }
                launchSingleTop = true
            },
        )
        app.applyLanguage(languageTag)
    }
}
