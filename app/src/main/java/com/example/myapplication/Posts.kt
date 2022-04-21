package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.models.PostModel
import org.json.JSONArray
import java.util.ArrayList

class Posts : AppCompatActivity() {

    val url = "https://jsonplaceholder.typicode.com/posts"
    lateinit var listView: ListView
    var arrayList: ArrayList<PostModel> = ArrayList()
    var adapter: MyAdapter? = null

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

        var arrayAdapter: ArrayAdapter<*>
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

                    adapter = Posts.MyAdapter(this, arrayList)
                    listView.adapter = adapter

                }, Response.ErrorListener { })
            queue.add(reques)

        listView.setOnItemClickListener { parent, _, position, _ ->
            parent.getItemAtPosition(position)
            setRecord(position)
            val intent = Intent(this, Comments::class.java)
            startActivity(intent)
        }
    }

    fun setRecord(index: Int){
        val sharedData = this.getSharedPreferences("com.example.myapplication.shared",0)
        val edit = sharedData.edit()
        edit.putInt("postIndex", index+1)
        edit.apply()
    }

    class MyAdapter(private val context: Context, private val arrayList: java.util.ArrayList<PostModel>) : BaseAdapter() {
        private lateinit var titlee: TextView
        private lateinit var body: TextView
        override fun getCount(): Int {
            return arrayList.size
        }
        override fun getItem(position: Int): Any {
            return position
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var convertView = convertView
            convertView = LayoutInflater.from(context).inflate(R.layout.postrow, parent, false)

            titlee = convertView.findViewById(R.id.title1)
            titlee.text = arrayList[position].title
            body = convertView.findViewById(R.id.body)
            body.text = arrayList[position].body
            return convertView
        }
    }

}
