package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.models.ToDoModel
import com.example.myapplication.models.UserModel
import org.json.JSONArray
import java.util.ArrayList

class ToDo : AppCompatActivity() {

    private var usersList : MutableList<UserModel> = ArrayList<UserModel>()
    val url = "https://jsonplaceholder.typicode.com/todos"

    lateinit var listView: ListView
    var arrayList: ArrayList<ToDoModel> = ArrayList()
    var adapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todoactivity)

        val sharedData = this.getSharedPreferences("com.example.myapplication.shared",0)
        val index = sharedData.getInt("index", 0)

        val postbtn = findViewById<Button>(R.id.postButton)
        val userBtn = findViewById<Button>(R.id.usersButton)

        postbtn.setOnClickListener(){
            val intent = Intent(this, Posts::class.java)
            startActivity(intent)
            onPause()
        }

        userBtn.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            onPause()
        }

        listView = findViewById<ListView>(R.id.todoList)

        val queue = Volley.newRequestQueue(this)
        val reques = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                val data = response.toString()
                var jArray = JSONArray(data)
                for (i in 0..jArray.length() - 1) {
                    var jobject = jArray.getJSONObject(i)
                    val todo = ToDoModel()
                    todo.userId = jobject.getInt("userId")
                    todo.name = jobject.getString("title")
                    todo.complete = jobject.getBoolean("completed")

                    if (todo.userId == index) {
                        arrayList.add(todo)
                    }
                }
                    adapter = ToDo.MyAdapter(this, arrayList)
                    listView.adapter = adapter

            }, Response.ErrorListener { })
        queue.add(reques)
    }

    fun setRecord(index: Int){
        val sharedData = this.getSharedPreferences("com.example.myapplication.shared",0)
        val edit = sharedData.edit()
        edit.putInt("index", index+1)
        edit.apply()
    }

    class MyAdapter(private val context: Context, private val arrayList: ArrayList<ToDoModel>) : BaseAdapter() {
        private lateinit var title: TextView
        private lateinit var completed: TextView
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
            convertView = LayoutInflater.from(context).inflate(R.layout.rowtodo, parent, false)

            title = convertView.findViewById(R.id.title)
            title.text = arrayList[position].name
            completed = convertView.findViewById(R.id.completed)
            completed.text = arrayList[position].complete.toString()
            return convertView
        }
    }
}
