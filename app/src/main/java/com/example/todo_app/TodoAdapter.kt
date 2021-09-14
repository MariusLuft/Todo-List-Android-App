package com.example.todo_app
import android.content.ContentValues
import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoAdapter
    (
    private val Todos: MutableList<Todo>,
    private val Context: Context
    ): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>()
{
    // own class to list todos
    class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    val storageManager = StorageManager(Context)

    // mandatory methods because inheriting from RecyclerView.Adapter
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
            // is the item checked initially ?
            initialStrikeThrough(tvTodoTitle, cbDone.isChecked)
            // add listener
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTodoTitle, isChecked)
                currTodo.IsChecked = !currTodo.IsChecked
            }
        }
    }

    private fun initialStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean) {
        // strikes through the text if is checked
        if(isChecked){
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else{
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
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
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }

        // update IsChecked in SQL Lite
        val db = storageManager.writableDatabase
        // New value for one column
        val values = ContentValues().apply {
            put(DbContract.Todos.COLUMN_NAME_TITLE, tvTodoTitle.text.toString())
            put(DbContract.Todos.COLUMN_NAME_ISCHECKED, if(isChecked) 1 else 0)
        }

        // Which row to update, based on the title
        val selection = "${DbContract.Todos.COLUMN_NAME_TITLE} LIKE ?"
        val selectionArgs = arrayOf(tvTodoTitle.text.toString())
        val count = db.update(
            DbContract.Todos.TABLE_NAME,
            values,
            selection,
            selectionArgs)
    }

    fun addTodo(todo: Todo){
        Todos.add(todo)
        // tell adapter that we inserted an item
        notifyItemInserted(Todos.size - 1)
    }

    fun addTodoToStorage(todo: Todo) {
        // adds to storage
        // Gets the data repository in write mode
        val db = storageManager.writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(DbContract.Todos.COLUMN_NAME_TITLE, todo.Title)
            put(DbContract.Todos.COLUMN_NAME_ISCHECKED, if (todo.IsChecked) 1 else 0)
        }
        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(DbContract.Todos.TABLE_NAME, null, values)
    }

    fun readTodosFromStorage() {
        val db = storageManager.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection =
            arrayOf(BaseColumns._ID, DbContract.Todos.COLUMN_NAME_TITLE, DbContract.Todos.COLUMN_NAME_ISCHECKED)

        // search query
        val cursor = db.query(
            DbContract.Todos.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // group the rows
            null,                   // filter by row groups
            null               // sort
        )

        with(cursor) {
            while (moveToNext()) {
                val todoTitle = getString(getColumnIndexOrThrow(DbContract.Todos.COLUMN_NAME_TITLE))
                val checkedFlag = getInt(getColumnIndexOrThrow(DbContract.Todos.COLUMN_NAME_ISCHECKED))
                val isChecked = checkedFlag == 1
                val newTodo = Todo(todoTitle, isChecked)
                addTodo(newTodo)
            }
        }
        cursor.close()
    }


    fun deleteDoneTodos(){
        Todos.removeAll { todo ->
            todo.IsChecked
        }
        // notify the adapter
        notifyDataSetChanged()

        // delete done Todos in SQL Lite
        // Gets the data repository in write mode
        val db = storageManager.writableDatabase
        // Define 'where' part of query.
        val selection = "${DbContract.Todos.COLUMN_NAME_ISCHECKED} LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf("1")
        // Issue SQL statement.
        val deletedRows = db.delete(DbContract.Todos.TABLE_NAME, selection, selectionArgs)
    }
}