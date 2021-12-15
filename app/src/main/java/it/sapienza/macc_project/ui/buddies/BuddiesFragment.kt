package it.sapienza.macc_project.ui.buddies

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.sapienza.macc_project.R
import it.sapienza.macc_project.databinding.FragmentBuddiesBinding
import it.sapienza.macc_project.ui.info.InfoFragment
import kotlinx.android.synthetic.main.fragment_buddies.*


class BuddiesFragment : Fragment(),View.OnClickListener {

    private var _binding: FragmentBuddiesBinding? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var buddy : String = ""
    private var buddy_name : String = ""
    private lateinit var tv : EditText
    private lateinit var btn : Button
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var mydb: DatabaseReference
    lateinit var Buddies: ArrayList<String>



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentBuddiesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Buddies= ArrayList()

        layoutManager = LinearLayoutManager(requireContext())
        adapter = RecyclerAdapter(requireContext(),Buddies,this)



        tv = binding.buddiesTv
        btn = binding.addBtn
        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("Myapp").child("user")
        mydb=database.child(firebaseAuth.currentUser?.uid.toString())
        tv.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                Log.d("BUDDY",buddy)
                return@OnKeyListener true
            }
            false
        })
        btn.setOnClickListener(View.OnClickListener { v ->

            buddy= tv.text.toString()
            tv.text.clear()
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {

                    if (p0!!.exists()) {
                        Buddies.clear()
                        for (h in p0.children) {
                            if(buddy.equals(h.child("email").value)){
                                if(!buddy.equals(firebaseAuth.currentUser!!.email.toString())) {
                                    buddy_name=h.child("name").value.toString()
                                    buddy_name.replace(".","")
                                    Buddies?.add(buddy_name)
                                    database.child(firebaseAuth.currentUser?.uid.toString()).child("buddies").child(buddy_name).child("email").setValue(buddy)
                                    Toast.makeText(requireContext(),"Added buddy",Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(requireContext(),"Cannot add as a buddy yourself",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            })

        })
        return root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        mydb.child("buddies").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                Buddies.clear()
                for (h in snapshot.children){
                    Buddies?.add(h.key!!)
                }
                recycler_view2.apply {
                    // set a LinearLayoutManager to handle Android
                    // RecyclerView behavior
                    layoutManager = LinearLayoutManager(requireContext())
                    // set the custom adapter to the RecyclerView
                    adapter = RecyclerAdapter(requireContext(),Buddies,this@BuddiesFragment)

                }
            } else {
                Log.d("TAG", task.exception!!.message!!) //Don't ignore potential errors!
            }}


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        var position=v?.tag as String
        Log.d("TAG6",position)
        val bundle = bundleOf("name" to position)
        view?.findNavController()?.navigate(R.id.action_nav_buddies_to_nav_buddy,bundle)
    }

}




