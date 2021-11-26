package it.sapienza.macc_project.ui.utilities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.sapienza.macc_project.databinding.FragmentUtilitiesBinding

class UtilitiesFragment : Fragment() {

    private lateinit var utilitiesViewModel: UtilitiesViewModel
    private var _binding: FragmentUtilitiesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        utilitiesViewModel =
                ViewModelProvider(this).get(UtilitiesViewModel::class.java)

        _binding = FragmentUtilitiesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        utilitiesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}