package com.project.cafehub.model

data class FriendshipRequest(
    var requesterId:String,
    var addresseeId:String,
    var sendTime:String?,
    var isValid: Boolean?,
    var isAccepted: Boolean?,
    var requesterName:String?,
    var requesterSurname:String?,
    var requesterPhotoUrl:String?
) {
}