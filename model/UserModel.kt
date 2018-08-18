package com.example.junhyeokkwon.babel.model

data class UserModel(var userName: String? = null,
                     var userEmail: String? = null,
                     var userPassword: String? = null,
                     var uid: String? = null,
                     var pushToken: String? = null,
                     var statusComment: String? = null)