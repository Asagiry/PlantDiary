package com.asagiry.plantdiary.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.asagiry.plantdiary.PlantDiaryApp
import com.asagiry.plantdiary.databinding.FragmentHelpBinding
import com.asagiry.plantdiary.ui.common.playEntranceMotion
import androidx.fragment.app.Fragment
import java.util.Locale

class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.languageRussianButton.setOnClickListener { switchLanguage("ru") }
        binding.languageEnglishButton.setOnClickListener { switchLanguage("en") }
        updateLanguageButtons()
        binding.helpContent.playEntranceMotion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun switchLanguage(languageTag: String) {
        val app = requireActivity().application as PlantDiaryApp
        app.saveLanguage(languageTag)
        app.applyLanguage(languageTag)
    }

    private fun updateLanguageButtons() {
        val currentTag =
            AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag()?.substringBefore('-')
                ?: Locale.getDefault().language
        binding.languageRussianButton.isEnabled = currentTag != "ru"
        binding.languageEnglishButton.isEnabled = currentTag != "en"
    }
}
