package kr.co.skoop.firebasechatapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

/**
 * Created by Administrator on 2017-10-14.
 */
class ChatActivity : AppCompatActivity() {
    val adapter: ChatAdapter by lazy { ChatAdapter() }
    val chatRoomId: String by lazy { intent.getStringExtra("roomKey") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView.adapter = adapter.apply {

        }

        recyclerView.layoutManager = LinearLayoutManager(applicationContext).apply {
            orientation = LinearLayoutManager.VERTICAL
            stackFromEnd = true
        }

        btnSend.setOnClickListener {
            sendMessage()
        }

        initMessages()
    }

    private fun initMessages() {
        val currentUser = ResourceManager.currentUser

        if (currentUser == null) {
            return
        }

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("rooms").child(chatRoomId).child("messages")

        database.getReference("rooms").child(chatRoomId).child("userMap").child(currentUser.userId).setValue(currentUser)

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(chatRef: DataSnapshot?, s: String?) {
                if (chatRef == null) {
                    return
                }
                val chat = chatRef.getValue(ChatModel::class.java)

                if (chat != null) {
                    adapter.add(chat)

                    adapter.notifyItemInserted(adapter.itemCount - 1)
                    recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
                }
            }

            override fun onChildChanged(chatRef: DataSnapshot?, s: String?) {

            }

            override fun onChildMoved(chatRef: DataSnapshot?, s: String?) {

            }

            override fun onChildRemoved(chatRef: DataSnapshot?) {

            }

            override fun onCancelled(error: DatabaseError?) {

            }
        })
    }

    private fun sendMessage() {
        val currentUser = ResourceManager.currentUser
        val text = etText.text.toString()

        if (text.isEmpty() || currentUser == null) {
            return
        }

        val chat = ChatModel(currentUser.userId, currentUser.nickname, text, System.currentTimeMillis())

        val database = FirebaseDatabase.getInstance()

        val map: MutableMap<String, Any> = mutableMapOf("lastMessage" to text, "lastTimeStamp" to chat.timeStamp)

        database.getReference("rooms").child(chatRoomId).child("messages").child("${chat.timeStamp}").setValue(chat)
        database.getReference("rooms").child(chatRoomId).updateChildren(map)

        etText.setText("")
    }

    inner class ChatAdapter : RecyclerView.Adapter<ChatViewHolder>() {
        val list: ArrayList<ChatModel> = ArrayList()

        override fun onBindViewHolder(holder: ChatViewHolder?, position: Int) {
            val chat = list[position]

            holder?.tvMessage?.text = "${chat.nickname} : ${chat.text}"
            holder?.tvDate?.text = Util.getDateForMinute(chat.timeStamp)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_chat, parent, false)
            return ChatViewHolder(view)
        }

        fun addAll(list: List<ChatModel>) {
            this.list.addAll(list)

            notifyDataSetChanged()
        }

        fun add(chat: ChatModel) {
            this.list.add(chat)
        }

        fun clear() {
            list.clear()
        }

    }

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }
}