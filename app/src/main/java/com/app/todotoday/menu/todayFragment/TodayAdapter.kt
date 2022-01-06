package com.app.todotoday.menu.todayFragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.todotoday.R
import com.app.todotoday.databinding.ItemTaskListBinding
import com.app.todotoday.firestoreDB.TasksFS

class TodayAdapter : RecyclerView.Adapter<TodayAdapter.ViewHolder>() {


    lateinit var binding: ItemTaskListBinding
    private var taskList: MutableList<TasksFS> = ArrayList()


    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = ItemTaskListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindFB(task: TasksFS){
            binding.TimeTask.text = "${task.date} - ${task.time}"
            binding.TittleTask.text = task.tittle
            binding.NoteTask.text = task.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindFB(taskList[position])
    }

    override fun getItemCount(): Int {
        return taskList.size
    }


    fun setDataFB(task: ArrayList<TasksFS>){
        this.taskList = task
        notifyDataSetChanged()
    }
}