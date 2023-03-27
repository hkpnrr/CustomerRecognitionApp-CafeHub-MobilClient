package com.project.cafehub.model

import java.io.Serializable
/*
data class Cafe(val id: String? = null,
                val name: String? = null,
                val email: String? = null,
                val address: String? = null,
                val imageUrl: String? = null) : Serializable {
}
*/

data class Cafe(
    var id:String?=null,
    var name: String? = null,
    var address: String? = null,
    var imageUrl: String? = null) : Serializable {
}
