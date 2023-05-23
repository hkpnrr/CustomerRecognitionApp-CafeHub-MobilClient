package com.project.cafehub.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Order(
    var id:String,
    var cafeId:String,
    var cafeName:String?,
    var serverDate:Timestamp?,
    var time:String?,
    var date:String?,
    var userId:String,
    var cost:String,
    var isRated:Boolean

): Serializable {
}