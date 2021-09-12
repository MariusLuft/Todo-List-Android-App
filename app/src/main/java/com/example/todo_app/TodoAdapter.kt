package com.example.todo_app
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.icu.text.CaseMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoAdapter
    (
    private val Todos: MutableList<Todo>
    ): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>()
{
    // own class to list todos
    class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    // mendatory methods because inheriting from RecyclerView.Adapter

    // inflates an XML layout to kotlin code
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_todo,
                parent,
                false
            )
        )
    }

    // binds data to the views
    // sets what text and if checked
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        var currTodo = Todos[position]
        holder.itemView.apply{
            tvTodoTitle.text = currTodo.Title
            cbDone.isChecked = currTodo.IsChecked
            toggleStrikeThrough(tvTodoTitle, cbDone.isChecked)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTodoTitle, isChecked)
                currTodo.IsChecked = !currTodo.IsChecked
            }
        }
    }

    // returns amount of items in list
    override fun getItemCount(): Int {
        return Todos.size
    }

    // strikes through text if checked
    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean){
        // strikes through the text if is checked
        if(isChecked){
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else{
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG
        }
    }

    fun addTodo(todo: Todo){
        Todos.add(todo)
        // tell adapter that we inserted an item
        notifyItemInserted(Todos.size - 1)
    }

    fun deleteDoneTodos(){
        Todos.removeAll { todo ->
            todo.IsChecked
        }
        // notify the adapter
        notifyDataSetChanged()
    }
}