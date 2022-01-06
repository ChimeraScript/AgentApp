package com.app.todotoday.firestoreDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FireStoreViewModel:ViewModel() {

    private val repository = FirestoreRepository()

    fun fetchDashboardTasks(date:String):LiveData<MutableList<TasksFS>>{
        val mutableData = MutableLiveData<MutableList<TasksFS>>()
        repository.getPost(date).observeForever{
            mutableData.value=it
        }
        return mutableData
    }

    fun fetchDashboardData():LiveData<MutableList<DashboardsFS>>{
        val mutableData = MutableLiveData<MutableList<DashboardsFS>>()
        repository.getPostList().observeForever{
            mutableData.value=it
        }
        return mutableData
    }

    fun fetchTaskData(dashboard: String):LiveData<MutableList<TasksFS>>{
        val mutableData = MutableLiveData<MutableList<TasksFS>>()
        repository.getTaskList(dashboard).observeForever{
            mutableData.value=it
        }
        return mutableData
    }

    fun fetchNoteData(dashboard: String):LiveData<MutableList<NotesFS>>{
        val mutableData = MutableLiveData<MutableList<NotesFS>>()
        repository.getNoteList(dashboard).observeForever{
            mutableData.value=it
        }
        return mutableData
    }

    fun fetchRecurringTaskData(dashboard: String):LiveData<MutableList<RecurringTasksFS>>{
        val mutableData = MutableLiveData<MutableList<RecurringTasksFS>>()
        repository.getRecurringTasksList(dashboard).observeForever{
            mutableData.value=it
        }
        return mutableData
    }
}