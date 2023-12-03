package com.example.notes.view

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        val moreOp=itemView.findViewById<ImageView>(R.id.more_op)
        init{
            moreOp.setOnClickListener {
                showPopup(it,userList[absoluteAdapterPosition])
            }
        }
    }

    private fun showPopup(view: View, userData: UserData) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.deleteSubject -> {
                    deleteSubject(view.context,userData)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun deleteSubject(context: Context, subject: UserData) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        FirebaseFirestore.getInstance()
            .collection("Subjects")
            .document(firebaseUser?.uid ?: "")
            .collection("mySubjects")
            .whereEqualTo("subName", subject.subName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    doc.reference.delete()
                }
                Toast.makeText(context, "Subject Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error Deleting Subject", Toast.LENGTH_SHORT).show()
            }
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
