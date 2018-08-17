package com.example.junhyeokkwon.babel.model

data class NotificationModel(var to: String? = null, var notification: Notification = Notification()) {

    class Notification {
        var title: String? = null
        var text: String? = null
    }
}
