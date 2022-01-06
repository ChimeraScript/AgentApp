package com.app.todotoday.menu.dashboardFragment.content

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.todotoday.R
import com.app.todotoday.databinding.ItemTaskListBinding
import com.app.todotoday.firestoreDB.TasksFS

class TaskAdapter(dashboardId:String,dashboardName:String): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    lateinit var binding: ItemTaskListBinding
    private var taskList: MutableList<TasksFS> = ArrayList()
    val id = dashboardId
    val name = dashboardName

    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = ItemTaskListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindFB(task: TasksFS, dashboardId: String, dashboardName: String){
            binding.TimeTask.text = "${task.date} - ${task.time}"
            binding.TittleTask.text = task.tittle
            binding.NoteTask.text = task.description
            binding.Task.setOnClickListener {
                val action = InDashboardFragmentDirections.actionInDashboardFragmentToUpdateTaskFragment(task,dashboardId,dashboardName)
                Navigation.findNavController(itemView).navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindFB(taskList[position],id,name)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setData(task: MutableList<TasksFS>){
        this.taskList = task
        notifyDataSetChanged()
    }
}