package com.app.todotoday.firestoreDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {

    // Firebase - Firestore

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getPost(dateT:String): LiveData<MutableList<TasksFS>> {
        val listLiveData = MutableLiveData<MutableList<TasksFS>>()
        val listTaskData = mutableListOf<TasksFS>()
        firebaseFirestore.collection("users").document(prefs.getEmail())
            .collection("Dashboards").get().addOnSuccessListener {
                for (document in it){
                    firebaseFirestore.collection("users").document(prefs.getEmail())
                        .collection("Dashboards").document(document.id)
                        .collection("Tasks").whereEqualTo("Date",dateT).get().addOnSuccessListener {task ->
                            for (t in task){
                                val tittle = t.getString("Tittle")?:""
                                val description = t.getString("Description")?:""
                                val date = t.getString("Date")?:""
                                val time = t.getString("Time")?:""
                                val priority = t.getString("Priority")?:""
                                val creation = t.getString("Creation")?:""
                                val firestoreId = t.id
                                val taskFS = TasksFS(firestoreId, tittle, description,date, time, priority, creation)
                                listTaskData.add(taskFS)
                            }
                            listLiveData.value = listTaskData
                        }
                }
            }
        return listLiveData
    }
    

    fun getPostList(): LiveData<MutableList<DashboardsFS>> {
        val mutableData = MutableLiveData<MutableList<DashboardsFS>>()
        firebaseFirestore.collection("users").document(prefs.getEmail())
            .collection("Dashboards").get().addOnSuccessListener {
                val listData = mutableListOf<DashboardsFS>()
                for (document in it){
                    val tittle = document.getString("Tittle")?:""
                    val description = document.getString("Description")?:""
                    val firestoreId = document.id
                    val dashboard = DashboardsFS(firestoreId, tittle, description)
                    listData.add(dashboard)
                }
                mutableData.value = listData
            }
        return mutableData
    }


    fun getTaskList(dashboardId: String): LiveData<MutableList<TasksFS>> {
        val mutableData = MutableLiveData<MutableList<TasksFS>>()
        firebaseFirestore.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(dashboardId)
            .collection("Tasks").get().addOnSuccessListener {
                val listData = mutableListOf<TasksFS>()
                for (document in it){
                    val tittle = document.getString("Tittle")?:""
                    val description = document.getString("Description")?:""
                    val date = document.getString("Date")?:""
                    val time = document.getString("Time")?:""
                    val priority = document.getString("Priority")?:""
                    val creation = document.getString("Creation")?:""
                    val firestoreId = document.id
                    val task = TasksFS(firestoreId, tittle, description,date, time, priority, creation)
                    listData.add(task)
                }
                mutableData.value = listData
            }
        return mutableData
    }

    fun getNoteList(dashboardId: String): LiveData<MutableList<NotesFS>> {
        val mutableData = MutableLiveData<MutableList<NotesFS>>()
        firebaseFirestore.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(dashboardId)
            .collection("Notes").get().addOnSuccessListener {
                val listData = mutableListOf<NotesFS>()
                for (document in it){
                    val tittle = document.getString("Tittle")?:""
                    val description = document.getString("Description")?:""
                    val date = document.getString("Date")?:""
                    val time = document.getString("Time")?:""
                    val priority = document.getString("Priority")?:""
                    val creation = document.getString("Creation")?:""
                    val firestoreId = document.id
                    val note = NotesFS(firestoreId, tittle, description,date, time, priority, creation)
                    listData.add(note)
                }
                mutableData.value = listData
            }
        return mutableData
    }

    fun getRecurringTasksList(dashboardId: String): LiveData<MutableList<RecurringTasksFS>> {
        val mutableData = MutableLiveData<MutableList<RecurringTasksFS>>()
        firebaseFirestore.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(dashboardId)
            .collection("RecurringTasks").get().addOnSuccessListener {
                val listData = mutableListOf<RecurringTasksFS>()
                for (document in it){
                    val tittle = document.getString("Tittle")?:""
                    val description = document.getString("Description")?:""
                    val time = document.getString("Time")?:""
                    val priority = document.getString("Priority")?:""
                    val creation = document.getString("Creation")?:""
                    val monday = document.getBoolean("Monday")?:true
                    val tuesday = document.getBoolean("Tuesday")?:true
                    val wednesday = document.getBoolean("Wednesday")?:true
                    val thursday = document.getBoolean("Thursday")?:true
                    val friday = document.getBoolean("Friday")?:true
                    val saturday = document.getBoolean("Saturday")?:true
                    val sunday = document.getBoolean("Sunday")?:true
                    val repetition = document.getLong("Repetition")?:0
                    val firestoreId = document.id
                    val recurringTask = RecurringTasksFS(firestoreId, tittle, description, monday, tuesday, wednesday, thursday, friday, saturday, sunday,
                        repetition,  time, priority, creation)
                    listData.add(recurringTask)
                }
                mutableData.value = listData
            }
        return mutableData
    }
}