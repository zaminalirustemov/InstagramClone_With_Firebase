package com.asparagas.instagramclone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.asparagas.instagramclone.R
import com.asparagas.instagramclone.adapter.FeedRecyclerAdapter
import com.asparagas.instagramclone.databinding.ActivityFeedBinding
import com.asparagas.instagramclone.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore

    val postArrayList : ArrayList<Post> = ArrayList()
    var adapter : FeedRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        auth=Firebase.auth
        db=Firebase.firestore

        getDataFromFirestore()

        binding.recyclerView.layoutManager=LinearLayoutManager(this@FeedActivity)

        adapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = adapter
    }



    fun getDataFromFirestore() {

        db.collection("Posts").orderBy("date",com.google.firebase.firestore.Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            } else {

                if (snapshot != null && !snapshot.isEmpty) {
                        postArrayList.clear()

                        val documents = snapshot.documents
                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val useremail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post = Post(useremail,comment, downloadUrl)
                            postArrayList.add(post)
                        }
                    adapter!!.notifyDataSetChanged()
                }

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feed_medu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.menuAddPost){
            val intent=Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)
        }
        if (item.itemId== R.id.menuSignOut){
            auth.signOut()
            val intent=Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}