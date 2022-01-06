package com.app.todotoday.firestoreDB

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "DashBoardsFS")
data class DashboardsFS (
    @PrimaryKey @ColumnInfo(name = "id") val id : String,
    @ColumnInfo(name = "tittle") val tittle: String,
    @ColumnInfo(name = "description") val description: String,
): Parcelable



@Parcelize
@Entity(tableName = "TasksFS")
data class TasksFS (
    @PrimaryKey @ColumnInfo(name = "id") val id : String = "",
    @ColumnInfo(name = "Tittle") val tittle: String = "",
    @ColumnInfo(name = "Description") val description: String = "",
    @ColumnInfo(name = "Date") val date: String = "",
    @ColumnInfo(name = "Hour") val time: String = "",
    @ColumnInfo(name = "Priority") val priority: String = "",
    @ColumnInfo(name = "creation")val Creation: String
    ): Parcelable


@Parcelize
@Entity(tableName = "NotesFS")
data class NotesFS (
    @PrimaryKey @ColumnInfo(name = "id") val id : String = "",
    @ColumnInfo(name = "tittle") val tittle: String,
    @ColumnInfo(name = "Description") val description: String,
    @ColumnInfo(name = "Date") val date: String,
    @ColumnInfo(name = "Hour") val time: String,
    @ColumnInfo(name = "Priority") val priority: String = "",
    @ColumnInfo(name = "Creation")val Creation: String
): Parcelable


@Parcelize
@Entity(tableName = "RecurringTasksFS")
data class RecurringTasksFS(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = "",
    @ColumnInfo(name = "Tittle") val tittle: String,
    @ColumnInfo(name = "Description") val description: String,
    @ColumnInfo(name = "Monday") val monday: Boolean,
    @ColumnInfo(name = "Tuesday") val tuesday: Boolean,
    @ColumnInfo(name = "Wednesday") val wednesday: Boolean,
    @ColumnInfo(name = "Thursday") val thursday: Boolean,
    @ColumnInfo(name = "Friday") val friday: Boolean,
    @ColumnInfo(name = "Saturday") val saturday: Boolean,
    @ColumnInfo(name = "Sunday") val sunday: Boolean,
    @ColumnInfo(name = "Repetition") val repetition: Long,
    @ColumnInfo(name = "Hour") val time: String,
    @ColumnInfo(name = "Priority") val priority: String = "",
    @ColumnInfo(name = "Creation") val Creation: String
): Parcelable