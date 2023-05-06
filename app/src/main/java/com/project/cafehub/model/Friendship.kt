package com.project.cafehub.model

data class Friendship(
    var firstUserId:String,
    var secondUserId:String,
    var time:String,
    var friendName:String?,
    var friendSurname:String?,
    var friendPhotoUrl:String?
) {
}