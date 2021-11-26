package it.sapienza.macc_project.ui.preferred

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.sapienza.macc_project.databinding.FragmentPreferredBinding

class PreferredFragment : Fragment() {

    private lateinit var preferredViewModel: PreferredViewModel
    private var _binding: FragmentPreferredBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        preferredViewModel =
                ViewModelProvider(this).get(PreferredViewModel::class.java)

        _binding = FragmentPreferredBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        preferredViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}