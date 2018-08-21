package com.example.junhyeokkwon.babel.model

import java.util.*
import kotlin.collections.HashMap

data class ChatModel(var users: HashMap<String, Boolean> = hashMapOf(),
                     var comments: HashMap<String, Comment> = hashMapOf()) {

    class Comment {
        var uid: String? = null
        var message: String? = null
        var timeStamp : Any? = null
        var readUsers : HashMap<String, Any> = HashMap()
    }

}

//data class ChatModel(var users: Map<String, Boolean> = HashMap(), var comments: Map<String, Comment> = HashMap()) {
//
//    class Comment {
//
//        var uid: String? = null
//        var message: String? = null
//        var timeStamp : String? = null
//    }
//
//}
