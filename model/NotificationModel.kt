package com.example.junhyeokkwon.babel.model

data class NotificationModel(var to: String? = null, var notification: Notification = Notification(), var data : Data = Data()) {

    class Notification(var title: String? = null, var text: String? = null)
    class Data(var title: String? = null, var text: String? = null)
}
