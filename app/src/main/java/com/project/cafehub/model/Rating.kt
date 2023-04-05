package com.project.cafehub.model

import java.util.Date

data class Rating (
    val id: String? = null,
    val comment: String? = null,
    val score: Int? = null,
    val userId: String? = null,
    val cafeId: String? = null,
    var commentDate: Date? = null,
    var passingTime: Long? = null)