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
import com.example.myapplication.models.PostModel
import com.example.myapplication.models.UserModel
import org.json.JSONArray
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var usersList : MutableList<UserModel> = ArrayList<UserModel>()
    val url = "https://jsonplaceholder.typicode.com/users"

    lateinit var listView: ListView
    var arrayList: ArrayList<UserModel> = ArrayList()
    var adapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById<ListView>(R.id.userList)


        val listOfUsers = findViewById<ListView>(R.id.userList)

        var arrayAdapter: ArrayAdapter<*>
        var users = ArrayList<PostModel>()

        val queue = Volley.newRequestQueue(this)
        val reques = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                val data = response.toString()
                var jArray = JSONArray(data)
                for (i in 0..jArray.length() - 1) {
                    var jobject = jArray.getJSONObject(i)
                    val user = UserModel()
                    user.id = jobject.getInt("id")
                    user.name = jobject.getString("name")
                    user.email = jobject.getString("email")
                    arrayList.add(user)
                }
                val arr = IntArray(jArray.length()) { 0 }

                val reques2 =
                    StringRequest(Request.Method.GET, "https://jsonplaceholder.typicode.com/posts",
                        Response.Listener { response ->
                            val data = response.toString()
                            var jArray = JSONArray(data)
                            var count = 0
                            for (i in 0..jArray.length() - 1) {
                                var jobject = jArray.getJSONObject(i)
                                val index = jobject.getInt("userId")
                                arr[index - 1]++
                            }
                            for (i in 0..arrayList.size - 1) {
                                arrayList[i].postNumber = arr[i]
//                                Log.e("XD", arrayList[i].name.toString())
//                                Log.e("XD", arrayList[i].postNumber.toString())
                            }

                            val arr = IntArray(jArray.length()) { 0 }
                            val arrComplete = IntArray(jArray.length()) { 0 }
                            val reques3 = StringRequest(Request.Method.GET,
                                "https://jsonplaceholder.typicode.com/todos",
                                Response.Listener { response ->
                                    val data = response.toString()
                                    var jArray = JSONArray(data)
                                    var count = 0
                                    for (i in 0..jArray.length() - 1) {
                                        var jobject = jArray.getJSONObject(i)
                                        val index = jobject.getInt("userId")
                                        val status = jobject.getBoolean("completed")
                                        arr[index - 1]++
                                        if (status)
                                            arrComplete[index - 1]++
                                    }
                                    for (i in 0..arrayList.size - 1) {
                                        arrayList[i].AlltoDoNumber = arr[i]
                                        arrayList[i].ComtoDoNumber = arrComplete[i]
                                    }
                                    adapter = MyAdapter(this, arrayList)
                                    listView.adapter = adapter
                                },
                                Response.ErrorListener { })
                            queue.add(reques3)
                        }, Response.ErrorListener { })
                queue.add(reques2)
            }, Response.ErrorListener { })
        queue.add(reques)

        listView.setOnItemClickListener { parent, _, position, _ ->
            parent.getItemAtPosition(position)
            setRecord(position)
            val intent = Intent(this, Posts::class.java)
            startActivity(intent)
        }
    }

    fun setRecord(index: Int){
        val sharedData = this.getSharedPreferences("com.example.myapplication.shared",0)
        val edit = sharedData.edit()
        edit.putInt("index", index+1)
        edit.apply()
    }

    class MyAdapter(private val context: Context, private val arrayList: ArrayList<UserModel>) : BaseAdapter() {
        private lateinit var nameUser: TextView
        private lateinit var email: TextView
        private lateinit var toDoNumber: TextView
        private lateinit var postsNumber: TextView
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
            convertView = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
            nameUser = convertView.findViewById(R.id.serialNumber)
            nameUser.text = arrayList[position].name
            email = convertView.findViewById(R.id.email)
            email.text = arrayList[position].email
            postsNumber= convertView.findViewById(R.id.postButton)
            postsNumber.text = "Posts: " + arrayList[position].postNumber.toString()
            toDoNumber = convertView.findViewById(R.id.toDo)
            toDoNumber.text = "To do: " + arrayList[position].ComtoDoNumber.toString() + "/" + arrayList[position].AlltoDoNumber.toString()
            return convertView
        }
    }
}
