package com.raihan.scarletmacaw

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.raihan.scarletmacaw.adapter.UserAdapter
import com.raihan.scarletmacaw.databinding.ActivityMainBinding
import com.raihan.scarletmacaw.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var database:FirebaseDatabase?=null
    private var users:ArrayList<User>?=null
    private var userAdapter:UserAdapter?=null
    private var dialog:ProgressDialog?=null
    private var user:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Getting Data...")
        dialog!!.setCancelable(false)

        database = FirebaseDatabase.getInstance()
        users = ArrayList<User>()
        userAdapter = UserAdapter(this@MainActivity,users!!)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        binding.recyclerView.adapter = userAdapter
        database!!.reference.child("users")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    users!!.clear()
                    for(snapShot1 in snapshot.children){
                        val user:User? = snapShot1.getValue(User::class.java)
                        if(!user!!.uid.equals(FirebaseAuth.getInstance().uid)){
                            users!!.add(user)
                        }
                    }
                    userAdapter!!.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    override fun onResume() {
        super.onResume()

        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!).setValue("Offline")
    }
}