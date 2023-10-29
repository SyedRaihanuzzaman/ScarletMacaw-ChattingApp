package com.raihan.scarletmacaw

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.raihan.scarletmacaw.databinding.ActivitySetupProfileBinding
import com.raihan.scarletmacaw.model.User
import java.util.Date

class SetupProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySetupProfileBinding
    var auth:FirebaseAuth ?=null
    var database:FirebaseDatabase ?=null
    var storage:FirebaseStorage ?= null
    var selectedImage:Uri ?=null
    var dialog:ProgressDialog ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Updating Profile..")
        dialog!!.setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.profileImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,45)


        }

        binding.setupprofileBtn.setOnClickListener {
            val name:String = binding.nameEtxt.text.toString()
            if(name.isEmpty()){
                binding.nameEtxt.setError("Required Field")
            }
            dialog!!.show()
            if(selectedImage!=null){
                val reference = storage!!.reference.child("Profile")
                    .child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener {
                    if(it.isSuccessful){
                        reference.downloadUrl.addOnCompleteListener {
                            val imageUrl = it.toString()
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val name:String = binding.nameEtxt.text.toString()
                            val user = User(uid!!,name,phone!!,imageUrl)

                            database!!.reference
                                .child("users")
                                .child(uid)
                                .setValue(user)
                                .addOnCompleteListener {
                                    dialog!!.dismiss()
                                    val intent = Intent(this,MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }

                    }
                    else{
                        val uid = auth!!.uid
                        val phone = auth!!.currentUser!!.phoneNumber
                        val name:String = binding.nameEtxt.text.toString()
                        val user = User(uid!!,name,phone!!,"NO Image")

                        database!!.reference
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnCompleteListener {
                                dialog!!.dismiss()
                                val intent = Intent(this,MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null){
            if(data.data!=null){
                val uri = data.data
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference
                    .child("Profile")
                    .child(time.toString()+"")
                reference.putFile(uri!!).addOnCompleteListener {
                    if(it.isSuccessful){
                        reference.downloadUrl.addOnCompleteListener {
                            val filePath = it.toString()
                            val obj = HashMap<String,Any>()
                            obj["image"] = filePath
                            database!!.reference
                                .child("users")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj).addOnSuccessListener {  }

                        }
                    }
                }
                binding.profileImg.setImageURI(data.data)
                selectedImage = data.data
            }

        }
    }
}