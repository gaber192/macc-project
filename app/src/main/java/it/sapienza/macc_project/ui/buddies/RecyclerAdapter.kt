package it.sapienza.macc_project.ui.buddies

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import androidx.fragment.app.*
import it.sapienza.macc_project.R
import android.app.Activity

import androidx.fragment.app.Fragment


class RecyclerAdapter(val context: Context, val list:ArrayList<String>,onClickListener: View.OnClickListener) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    var onClickListener = onClickListener

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mon_id: TextView
        var fav: ImageView

        init {
            mon_id = itemView.findViewById(R.id.monument_tv)
            fav = itemView.findViewById(R.id.fav_ib)

            }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.cardview_buddy, viewGroup, false)


        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("Myapp").child("user").child(firebaseAuth.currentUser?.uid.toString()).child("buddies")

        viewHolder.mon_id.text=list[i]
        viewHolder.fav.setOnClickListener { v: View ->
            delete_monument(i)
        }

        viewHolder.itemView.tag= list[i]
        viewHolder.itemView.setOnClickListener{
            onClickListener.onClick(viewHolder.itemView)
        }



    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun delete_monument(i: Int) {
        database.child(list[i]).removeValue()
        list.removeAt(i)
        notifyDataSetChanged()
    }

}




