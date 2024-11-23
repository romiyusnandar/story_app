package com.koaladev.storryapp.data.response

import com.google.gson.annotations.SerializedName

data class SignupResponse (
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)