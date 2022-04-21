package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.models.CommentModel
import org.json.JSONArray
import java.util.ArrayList

class Comments : AppCompatActivity() {

    val url = "https://jsonplaceholder.typicode.com/comments"
    lateinit var listView: ListView
    var arrayList: ArrayList<CommentModel> = ArrayList()
    var adapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments)

        val sharedData = this.getSharedPreferences("com.example.myapplication.shared",0)
        val postIndex = sharedData.getInt("postIndex", 0)

        Log.e("d", postIndex.toString())

        listView = findViewById<ListView>(R.id.commentsList)

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
                    val comment = CommentModel()
                    comment.name = jobject.getString("name")
                    comment.body = jobject.getString("body")
                    comment.email = jobject.getString("email")
                    comment.postId = jobject.getInt("postId")

                    if(comment.postId == postIndex)
                        arrayList.add(comment)
                }

                adapter = Comments.MyAdapter(this, arrayList)
                listView.adapter = adapter

            }, Response.ErrorListener { })
        queue.add(reques)
    }

    class MyAdapter(private val context: Context, private val arrayList: java.util.ArrayList<CommentModel>) : BaseAdapter() {
        private lateinit var name: TextView
        private lateinit var body: TextView
        private lateinit var email: TextView
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
            convertView = LayoutInflater.from(context).inflate(R.layout.commentrow, parent, false)

            name = convertView.findViewById(R.id.name)
            name.text = arrayList[position].name
            body = convertView.findViewById(R.id.body)
            body.text = arrayList[position].body
            email = convertView.findViewById(R.id.email)
            email.text = arrayList[position].email
            return convertView
        }
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

}
