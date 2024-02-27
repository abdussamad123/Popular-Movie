package com.popularmovie.appcomponent.util

data class BaseErrorResponse(
    var code: Int,
    var message: String = "Something went wrong!",
    var success: Boolean,
    var data: Any
)
