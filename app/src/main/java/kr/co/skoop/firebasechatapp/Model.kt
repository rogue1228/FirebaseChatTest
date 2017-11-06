package kr.co.skoop.firebasechatapp

/**
 * Created by Administrator on 2017-10-14.
 */
class UserModel {
    var userId = ""
    var nickname = ""
    constructor()
    constructor(userId: String, nickname: String) {
        this.userId = userId
        this.nickname =nickname
    }
}
class ChatRoomModel  {
    var userMap = HashMap<String, UserModel>()
    var lastTimeStamp : Long = 0
    var lastMessage = ""
    var key = ""
    constructor()
    constructor(userMap : HashMap<String, UserModel>, lastTimeStamp : Long, lastMessage : String) {
        this.userMap = userMap
        this.lastMessage = lastMessage
        this.lastTimeStamp = lastTimeStamp
    }
}
class ChatModel {
    var senderId = ""
    var nickname = ""
    var text = ""
    var timeStamp : Long = 0
    constructor()
    constructor(senderId: String, nickname: String, text : String, timeStamp : Long) {
        this.senderId = senderId
        this.nickname = nickname
        this.text = text
        this.timeStamp = timeStamp
    }
}