package com.example.notes.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.UserData

class UserAdapter(private val userList: ArrayList<UserData>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var onItemClickListener1: OnItemClickListener? = null

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subName = itemView.findViewById<TextView>(R.id.mtitle)
        val teacherName = itemView.findViewById<TextView>(R.id.mSubtitle)
        val classAttended = itemView.findViewById<TextView>(R.id.attended)
        val totalClasses = itemView.findViewById<TextView>(R.id.total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.notes_layout, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setOnItemClickListener(onItemClickListener1: OnItemClickListener) {
        this.onItemClickListener1 = onItemClickListener1
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newUser = userList[position]
        holder.subName.text = newUser.subName
        holder.teacherName.text = newUser.teacherName
        holder.classAttended.text = newUser.classAttended.toString()
        holder.totalClasses.text = newUser.totalClasses.toString()

        holder.itemView.setOnClickListener {
            onItemClickListener1?.onClick(position)
        }
    }
}
