package com.example.notes.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.UserData

class UserAdapter(val c:Context,val userList: ArrayList<UserData>):RecyclerView.Adapter<UserAdapter.userViewHolder>() {

    inner class userViewHolder(val v: View):RecyclerView.ViewHolder(v){
        val subName=v.findViewById<TextView>(R.id.mtitle)
        val teacherName=v.findViewById<TextView>(R.id.mSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val v=inflater.inflate(R.layout.notes_layout,parent,false)
        return userViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val newList=userList[position]
        holder.subName.text=newList.subName
        holder.teacherName.text=newList.teacherName
    }
}