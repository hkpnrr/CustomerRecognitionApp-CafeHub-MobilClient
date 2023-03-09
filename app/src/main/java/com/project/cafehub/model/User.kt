package com.project.cafehub.model
import java.io.Serializable
import java.util.Date

data class User(val id: String? = null,
                val name:String? = null,
                val surname:String? = null,
                val email:String? = null,
                val birthdate:Date? = null) : Serializable {
}
