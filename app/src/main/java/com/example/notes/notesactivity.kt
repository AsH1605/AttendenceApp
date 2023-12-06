package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.model.UserData
import com.example.notes.view.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class notesactivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private val userList = ArrayList<UserData>()
    private lateinit var userAdapter: UserAdapter
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notesactivity)

        supportActionBar?.title = "Subject Attendance Tracker"
        addsBtn = findViewById(R.id.createnotefab)
        recv = findViewById(R.id.recyclerview)
        userAdapter = UserAdapter(userList)
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = userAdapter
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        addsBtn.setOnClickListener {
            addInfo()
        }
        getData()
        userAdapter.setOnItemClickListener(object : UserAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                val clickedItem = userList[position]
                updateinfo(clickedItem.subName, clickedItem.classAttended, clickedItem.totalClasses)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        firebaseAuth = FirebaseAuth.getInstance()

        when (item.itemId) {
            R.id.logout -> {
                firebaseAuth.signOut()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.delete->{
                val inflater = LayoutInflater.from(this)
                val v = inflater.inflate(R.layout.delete_account, null)
                val addDialog = AlertDialog.Builder(this)
                addDialog.setView(v)

                addDialog.setPositiveButton("Ok") { dialog, _ ->
                    firebaseUser.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Deleted Account", Toast.LENGTH_SHORT).show()
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Could not Delete Account", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                addDialog.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
                }
                addDialog.create()
                addDialog.show()
            }

            else -> false
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addInfo() {
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.add_item, null)
        val subName = v.findViewById<EditText>(R.id.subName)
        val teacherName = v.findViewById<EditText>(R.id.teacherName)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val subNames = subName.text.toString()
            val tNames = teacherName.text.toString()
            if (subNames.isNullOrBlank()) {
                Toast.makeText(this, "Subject name is Required", Toast.LENGTH_SHORT).show()
            }else {
                val documentReference = FirebaseFirestore.getInstance()
                    .collection("Subjects")
                    .document(firebaseUser.uid)
                    .collection("mySubjects")
                    .document()

                val userdata = UserData(subNames, tNames)

                documentReference.set(userdata).addOnSuccessListener {
                    Toast.makeText(this, "Subject Added Successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }

    private fun getData() {
        recv.visibility = View.GONE

        val documentReference = FirebaseFirestore.getInstance()
            .collection("Subjects")
            .document(firebaseUser.uid)
            .collection("mySubjects")
        documentReference.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(this, "Error Fetching Data", Toast.LENGTH_SHORT).show()
            }
            userList.clear()
            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val sub = document?.toObject(UserData::class.java)
                    if (sub != null) {
                        userList.add(sub)
                    }
                }
                userAdapter.notifyDataSetChanged()
                recv.visibility = View.VISIBLE
            }
        }
    }

    private fun updateinfo(sub: String, atten: Int, total: Int) {
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.sub_update, null)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        var daten = v.findViewById<TextView>(R.id.displayAttended)
        var dtotal = v.findViewById<TextView>(R.id.displayTotal)

        daten?.text = atten.toString()
        dtotal?.text = total.toString()

        val btnAddAtten = v.findViewById<Button>(R.id.btnAddAttended)
        val btnSubAtten = v.findViewById<Button>(R.id.btnSubAttended)
        val btnAddTotal = v.findViewById<Button>(R.id.btnAddTotal)
        val btnSubTotal = v.findViewById<Button>(R.id.btnSubTotal)

        var attenNoPosi = 0
        var totalNoPosi = 0
        var attenNoNeg = 0
        var totalNoNeg = 0

        btnAddAtten.setOnClickListener {
            attenNoPosi++
            daten?.text = (atten + attenNoPosi - attenNoNeg).toString()
            dtotal?.text = (total + totalNoPosi - totalNoNeg+attenNoPosi ).toString()
        }
        btnSubAtten.setOnClickListener {
            attenNoNeg++
            daten?.text = (atten + attenNoPosi - attenNoNeg).toString()
        }
        btnAddTotal.setOnClickListener {
            totalNoPosi++
            dtotal?.text = (total + totalNoPosi - totalNoNeg+attenNoPosi).toString()
        }
        btnSubTotal.setOnClickListener {
            totalNoNeg++
            dtotal?.text = (total + totalNoPosi - totalNoNeg+attenNoPosi).toString()
        }
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val documentReference = FirebaseFirestore.getInstance()
                .collection("Subjects")
                .document(firebaseUser.uid)
                .collection("mySubjects")
                .whereEqualTo("subName", sub)
            val updatedAtten = atten + attenNoPosi - attenNoNeg
            val updatedTotal = total + totalNoPosi - totalNoNeg + attenNoPosi
            if(updatedTotal>=updatedAtten) {
                documentReference.get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {

                        document.reference.update("classAttended", updatedAtten)
                        document.reference.update("totalClasses", updatedTotal)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error Updating", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                dialog.dismiss()
            }else {
                Toast.makeText(this, "Total Classes Cannot be Less than Attended Classes", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }
}

