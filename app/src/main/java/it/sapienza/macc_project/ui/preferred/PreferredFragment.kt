package it.sapienza.macc_project.ui.preferred

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.sapienza.macc_project.databinding.FragmentPreferredBinding
import kotlinx.android.synthetic.main.fragment_preferred.*
import java.lang.Exception

class PreferredFragment : Fragment() {

    private var _binding: FragmentPreferredBinding? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var Monument: ArrayList<String>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentPreferredBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Monument= ArrayList()

        layoutManager = LinearLayoutManager(requireContext())
        adapter = RecyclerAdapter(requireContext(),Monument)

        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("Myapp").child("user").child(firebaseAuth.currentUser?.uid.toString()).child("monuments_preferred")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.exists()) {
                    Monument.clear()
                    for (h in p0.children) {
                        Monument?.add(h.key!!)
                    }
                    recycler_view.apply {
                        // set a LinearLayoutManager to handle Android
                        // RecyclerView behavior
                        layoutManager = LinearLayoutManager(requireContext())
                        // set the custom adapter to the RecyclerView
                        adapter = RecyclerAdapter(requireContext(),Monument)


                    }
                }
            }
        })

        return root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}



