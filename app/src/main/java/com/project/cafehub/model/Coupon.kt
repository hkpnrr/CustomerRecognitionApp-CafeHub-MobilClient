package com.project.cafehub.model

data class Coupon(
    var userId:String,
    var cafeId:String,
    var couponAvailable:Boolean,
    var couponCode:String,
    var couponCount:Long,
    var cafeName:String?,
    var cafePhotoUrl:String?
) {
}