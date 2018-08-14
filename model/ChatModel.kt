package com.example.junhyeokkwon.babel.model

data class ChatModel(var users: HashMap<String, Boolean> = hashMapOf(),
                     var comments: HashMap<String, Comment> = hashMapOf()) {

    class Comment {
        var uid: String? = null
        var message: String? = null
    }

}

//data class ChatModel(var uid: String? = null, var destinationUid: String? = null )