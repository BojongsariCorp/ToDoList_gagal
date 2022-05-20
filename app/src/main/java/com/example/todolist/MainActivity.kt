package com.example.todolist

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() ,UpdateAndDelete{
    lateinit var database: DatabaseReference
    var toDoList:MutableList<ToDoModel>? = null
    lateinit var adapter: ToDoAdapter
    private var listViewItem : ListView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab=findViewById<View>(R.id.fab) as FloatingActionButton
        listViewItem = findViewById<ListView>(R.id.item_listView)


        fab.setOnClickListener { view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText= EditText(this)
            alertDialog.setMessage("add TODO item")
            alertDialog.setTitle("Enter To Do item")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("add"){dialog, i ->
                val todoItemData = ToDoModel.createList()
                todoItemData.itemDataText = textEditText.text.toString()
                todoItemData.done = false

                val newItemData = database.child("todo").push()
                todoItemData.UID = newItemData.key

                newItemData.setValue(todoItemData)



                dialog.dismiss()
                Toast.makeText(this, "item saved", Toast.LENGTH_LONG).show()
            }
            alertDialog.show()
        }

        toDoList= mutableListOf<ToDoModel>()
        adapter= ToDoAdapter(this, toDoList!!)
        listViewItem!!.adapter=adapter
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList!!.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "NO Item Added", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {

        val item=snapshot.children.iterator()

        if (item.hasNext()){

            val toDoIndexedValue=item.next()
            val itemIterator =toDoIndexedValue.children.iterator()

            while (itemIterator.hasNext()){

                val currentItem= itemIterator.next()
                val toDoItemData= ToDoModel.createList()
                val map =currentItem.getValue() as HashMap<String ,Any>

                toDoItemData.UID = currentItem.key
                toDoItemData.done=map.get("done") as Boolean?
                toDoItemData.itemDataText=map.get("itemTextData") as String?
                toDoList!!.add(toDoItemData)
            }
        }

        adapter.notifyDataSetChanged()

    }

    override fun modifyItem(itemUID: String, isDone: Boolean) {
        val itemReference = database.child("ToDo").child(itemUID)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemUID: String) {
        val itemReference = database.child("ToDo").child(itemUID)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}