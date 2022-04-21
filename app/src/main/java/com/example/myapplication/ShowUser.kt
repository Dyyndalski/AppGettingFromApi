package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.models.PostModel
import org.json.JSONArray
import java.util.ArrayList

class ShowUser : AppCompatActivity() {

    val url = "https://jsonplaceholder.typicode.com/users"
    lateinit var listView: ListView
    var arrayList: ArrayList<PostModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.posts)

        val sharedData = this.getSharedPreferences("com.example.myapplication.shared",0)
        val index = sharedData.getInt("index", 0)

        Log.e("d", index.toString())

        listView = findViewById<ListView>(R.id.postList)

        val toDoBtn = findViewById<Button>(R.id.todoButton)
        toDoBtn.setOnClickListener(){
            val intent = Intent(this, ToDo::class.java)
            startActivity(intent)
        }

        val userBtn = findViewById<Button>(R.id.usersButton)
        userBtn.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            onPause()
        }

        val queue = Volley.newRequestQueue(this)
        val reques = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                val data = response.toString()
                var jArray = JSONArray(data)
                for (i in 0..jArray.length() - 1) {
                    var jobject = jArray.getJSONObject(i)
                    val post = PostModel()
                    post.title = jobject.getString("title")
                    post.body = jobject.getString("body")
                    post.userId = jobject.getInt("userId")

                    if(post.userId == index)
                        arrayList.add(post)
                }

            }, Response.ErrorListener { })
        queue.add(reques)
    }

    fun setRecord(index: Int){
        val sharedData = this.getSharedPreferences("com.example.myapplication.shared",0)
        val edit = sharedData.edit()
        edit.putInt("postIndex", index+1)
        edit.apply()
    }
}
