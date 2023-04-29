package com.project.cafehub.model

import java.io.Serializable

data class Cafe(
    var id:String?=null,
    var name: String? = null,
    var address: String? = null,
    var imageUrl: String? = null,
var latitude:Double?=null,
var longitude:Double?=null) : Serializable {
}
