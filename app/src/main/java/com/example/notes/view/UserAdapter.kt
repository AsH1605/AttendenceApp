package com.example.notes.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.UserData
import java.util.Random

class UserAdapter(private val userList: ArrayList<UserData>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var onItemClickListener1: OnItemClickListener? = null

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subName = itemView.findViewById<TextView>(R.id.mtitle)
        val teacherName = itemView.findViewById<TextView>(R.id.mSubtitle)
        val classAttended = itemView.findViewById<TextView>(R.id.attended)
        val totalClasses = itemView.findViewById<TextView>(R.id.total)
        val percent=itemView.findViewById<TextView>(R.id.percentage)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
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
        val classAttended = newUser.classAttended
        val totalClasses = newUser.totalClasses
        val percentage = if (totalClasses != 0) {
            (classAttended * 100 / totalClasses).toString() + "%"
        } else {
            0
        }
        holder.percent.text=percentage.toString()

        holder.itemView.setOnClickListener {
            onItemClickListener1?.onClick(position)
        }
        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(getRandomColor(),null))
    }

    private fun getRandomColor(): Int {
        val colorCode: List<Int> = listOf(
            R.color.pink,
            R.color.blue,
            R.color.green,
            R.color.yellow,
            R.color.purple,
            R.color.brown,
            R.color.gray,
            R.color.orange)
        val random = Random()
        val number = random.nextInt(colorCode.size)
        return colorCode[number]
    }
}
