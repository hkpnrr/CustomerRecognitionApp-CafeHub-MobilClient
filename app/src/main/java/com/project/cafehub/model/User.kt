package com.project.cafehub.model
import java.io.Serializable
import java.util.Date

data class User(
    var id: String? = null,
    var name:String? = null,
    var surname:String? = null,
    var email:String? = null,
    var birthdate:String? = null,
    var photoUrl:String?=null) : Serializable {
}
