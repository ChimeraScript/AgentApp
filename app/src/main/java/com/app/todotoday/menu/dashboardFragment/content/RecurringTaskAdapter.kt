package com.app.todotoday.menu.dashboardFragment.content

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.todotoday.R
import com.app.todotoday.databinding.ItemTaskListBinding
import com.app.todotoday.firestoreDB.RecurringTasksFS

class RecurringTaskAdapter(dashboardId:String,dashboardName:String): RecyclerView.Adapter<RecurringTaskAdapter.ViewHolder>() {

    lateinit var binding: ItemTaskListBinding
    private var recurringTaskList: MutableList<RecurringTasksFS> = ArrayList()
    val id = dashboardId
    val name = dashboardName

    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = ItemTaskListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindFB(recurringTask: RecurringTasksFS, dashboardId: String, dashboardName: String){
            // binding.TimeTask.text = "${recurringTask.date} - ${recurringTask.time}"
            binding.TittleTask.text = recurringTask.tittle
            binding.NoteTask.text = recurringTask.description
            binding.Task.setOnClickListener {
                val action = InDashboardFragmentDirections
                    .actionInDashboardFragmentToUpdateRecurringTaskFragment(recurringTask,dashboardId,dashboardName)
                Navigation.findNavController(itemView).navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindFB(recurringTaskList[position],id,name)
    }

    override fun getItemCount(): Int {
        return recurringTaskList.size
    }

    fun setData(recurringTask: MutableList<RecurringTasksFS>){
        this.recurringTaskList = recurringTask
        notifyDataSetChanged()
    }
}