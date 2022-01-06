package com.app.todotoday.menu.dashboardFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.todotoday.R
import com.app.todotoday.databinding.ItemTaskBinding
import com.app.todotoday.firestoreDB.DashboardsFS

class DashboardAdapter:RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {


    lateinit var binding: ItemTaskBinding
    private var dashboardFBList: MutableList<DashboardsFS> = ArrayList()


    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = ItemTaskBinding.bind(itemView)

        fun bindFB(Dashboard: DashboardsFS){
            binding.TittleTask.text = Dashboard.tittle
            binding.NoteTask.text = Dashboard.description
            binding.editBtn.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToUpdateDashboardFireBaseFragment(Dashboard)
                Navigation.findNavController(itemView).navigate(action)
            }
            binding.Task.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToInDashboardFragment(Dashboard.id,Dashboard.tittle)
                Navigation.findNavController(itemView).navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindFB(dashboardFBList[position])

    }

    override fun getItemCount(): Int {
       return dashboardFBList.size
    }


    fun setDataFB(Dashboard: ArrayList<DashboardsFS>){
        this.dashboardFBList = Dashboard
        notifyDataSetChanged()
    }
}