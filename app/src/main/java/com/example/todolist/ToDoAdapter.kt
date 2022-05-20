package com.example.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

class ToDoAdapter(context: Context, toDoList: MutableList<ToDoModel>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = toDoList
    private var updateAndDelete: UpdateAndDelete = context as UpdateAndDelete

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val UID: String = itemList.get(position).UID as String
        val itemTextData = itemList.get(position).itemDataText as String
        val done: Boolean = itemList.get(position).done as Boolean

        val view: View
        val viewHolder: ListViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.row_itemlayout, parent, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ListViewHolder
        }

        viewHolder.textLabel.text = itemTextData
        viewHolder.isDone.isChecked = done

        viewHolder.isDone.setOnClickListener {
            updateAndDelete.modifyItem(UID, !done)
        }

        viewHolder.isDelete.setOnClickListener {
            updateAndDelete.onItemDelete(UID)
        }


        return view
    }

    private class ListViewHolder(row: View?) {
        val textLabel: TextView = row!!.findViewById(R.id.item_textView) as TextView
        val isDone: CheckBox = row!!.findViewById(R.id.checkbox) as CheckBox
        val isDelete: ImageButton = row!!.findViewById(R.id.close) as ImageButton

    }
}