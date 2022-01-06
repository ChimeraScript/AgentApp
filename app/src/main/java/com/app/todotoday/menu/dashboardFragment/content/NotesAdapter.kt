package com.app.todotoday.menu.dashboardFragment.content

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.todotoday.R
import com.app.todotoday.databinding.ItemTaskListBinding
import com.app.todotoday.firestoreDB.NotesFS

class NotesAdapter(dashboardId:String,dashboardName:String): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    lateinit var binding: ItemTaskListBinding
    private var noteList: MutableList<NotesFS> = ArrayList()
    val id = dashboardId
    val name = dashboardName

    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val binding = ItemTaskListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindFB(note: NotesFS, dashboardId: String, dashboardName: String){
            binding.TimeTask.text = "${note.date} - ${note.time}"
            binding.TittleTask.text = note.tittle
            binding.NoteTask.text = note.description
            binding.Task.setOnClickListener {
                val action = InDashboardFragmentDirections.actionInDashboardFragmentToUpdateNoteFragment(note,dashboardId,dashboardName)
                Navigation.findNavController(itemView).navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindFB(noteList[position],id,name)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    fun setData(note: MutableList<NotesFS>){
        this.noteList = note
        notifyDataSetChanged()
    }
}