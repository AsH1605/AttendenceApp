package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.model.UserData
import com.example.notes.view.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class notesactivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private val userList= ArrayList<UserData>()
    private lateinit var userAdapter: UserAdapter
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notesactivity)

        supportActionBar?.title = "All Subjects"
        addsBtn=findViewById(R.id.createnotefab)
        recv=findViewById(R.id.recyclerview)
        userAdapter=UserAdapter(userList)
        recv.layoutManager= LinearLayoutManager(this)
        recv.adapter=userAdapter
        firebaseAuth=FirebaseAuth.getInstance()
        firebaseFirestore=FirebaseFirestore.getInstance()
        firebaseUser= FirebaseAuth.getInstance().currentUser!!
        addsBtn.setOnClickListener { addInfo()
            Log.d("TAG", "onCreate: ${userList.joinToString { it.toString().plus(", ") }}")}
         getData()
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
                startActivity(Intent(this,MainActivity::class.java))
                true
            }
            else -> false
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addInfo() {
        val inflater= LayoutInflater.from(this)
        val v=inflater.inflate(R.layout.add_item,null)
        val subName=v.findViewById<EditText>(R.id.subName)
        val teacherName=v.findViewById<EditText>(R.id.teacherName)
        val addDialog= AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val subNames=subName.text.toString()
            val tNames=teacherName.text.toString()

//            userList.add(UserData("Subject Name: $subNames", "Teacher's Name: $tNames"))
//            userAdapter.notifyDataSetChanged()
            val documentReference = FirebaseFirestore.getInstance()
                .collection("Subjects")
                .document(firebaseUser.uid)
                .collection("mySubjects")
                .document()

            val userdata=UserData(subNames,tNames)

            documentReference.set(userdata).addOnSuccessListener{
                Toast.makeText(this,"Subject Added Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, notesactivity::class.java))
            }
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel", Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }

    private fun getData(){
        recv.visibility= View.GONE

        val documentReference = FirebaseFirestore.getInstance()
            .collection("Subjects")
            .document(firebaseUser.uid)
            .collection("mySubjects")
        documentReference.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TAG", "Error fetching data", error)
                Toast.makeText(this, "Error fetching Data", Toast.LENGTH_SHORT).show()
            }
            userList.clear()
            if(snapshot!=null){
                for(document in snapshot.documents){
                    val sub=document?.toObject(UserData::class.java)
                    Log.d("TAG", "getData: $document")
                    if(sub!=null){
                        userList.add(sub)
                    }
                }
                userAdapter.notifyDataSetChanged()
                recv.visibility= View.VISIBLE
            }
        }
    }
}