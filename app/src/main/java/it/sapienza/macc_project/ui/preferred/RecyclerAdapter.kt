package it.sapienza.macc_project.ui.preferred

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.sapienza.macc_project.R
import android.widget.Toast
import kotlin.coroutines.coroutineContext


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val Monument = arrayListOf<String>("Colosseo", "Ara Pacis","AAAAA","BBBBB","CCCCCCC","DDDDDDDDD","EEEEEEEE")

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
            .inflate(R.layout.cardview, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.mon_id.text = Monument[i]
        viewHolder.fav.setOnClickListener { v: View ->
            Toast.makeText(viewHolder.fav.context,"Deleted Monument: "+Monument[i],Toast.LENGTH_SHORT).show()
            delete_monument(i)
        }

    }

    override fun getItemCount(): Int {
        return Monument.size
    }

    fun delete_monument(i: Int) {
        Monument.removeAt(i)
        //to do: delete the item from firebase db
        notifyDataSetChanged()
    }

}




