package com.example.todo_app
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter
    (
    private val Todos: MutableList<Todo>
    )
{
    class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}