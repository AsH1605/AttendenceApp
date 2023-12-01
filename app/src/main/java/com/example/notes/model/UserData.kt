package com.example.notes.model

import java.io.Serializable

data class UserData(
    val subName:String="",
    val teacherName:String="",
    val classAttended:Int=0,
    val totalClasses:Int=0,
    val percent:Int=0
)