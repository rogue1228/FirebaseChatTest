package kr.co.skoop.firebasechatapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat_list.*

/**
 * Created by Administrator on 2017-10-14.
 */
class ChatListActivity : AppCompatActivity() {
    val adapter: ChatListAdapter by lazy { ChatListAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recyclerView.adapter = adapter.apply {

        }

        recyclerView.layoutManager = LinearLayoutManager(applicationContext).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        fabCreate.setOnClickListener {
            createRoom()
        }

        initRoomList()
    }

    private fun createRoom() {
        if (ResourceManager.currentUser == null) {
            Toast.makeText(applicationContext, "다시 로그인 해주세요.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val user = ResourceManager.currentUser!!

        val database = FirebaseDatabase.getInstance()
        val userMap = HashMap<String, UserModel>()
        userMap.put(user.userId, user)

        val room = ChatRoomModel(userMap, System.currentTimeMillis(), "")

        database.getReference("rooms").push().setValue(room, { error, roomRef ->
            if (roomRef == null) {
                return@setValue
            }

            enterChatRoom(roomRef.key)
        })
    }

    private fun initRoomList() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("rooms")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot == null) {
                    return
                }

                val roomIterable = dataSnapshot.children

                if (roomIterable != null) {
                    adapter.clear()
                    for (roomRef in roomIterable) {
                        val room = roomRef.getValue(ChatRoomModel::class.java)
                        if (room != null) {
                            room.key = roomRef.key
                            adapter.add(room)
                        }
                    }

                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError?) {

            }
        })

    }

    private fun enterChatRoom(roomKey: String) {
        startActivity(Intent(this@ChatListActivity, ChatActivity::class.java).apply {
            putExtra("roomKey", roomKey)
        })
    }


    inner class ChatListAdapter : RecyclerView.Adapter<RoomViewHolder>() {
        val list: ArrayList<ChatRoomModel> = ArrayList()

        override fun onBindViewHolder(holder: RoomViewHolder?, position: Int) {
            val room = list[position]

            holder?.tvMessage?.text = room.lastMessage
            holder?.tvDate?.text = Util.getDateForMinute(room.lastTimeStamp)
            holder?.tvUsers?.text = Util.userNames(room.userMap)

            holder?.itemView?.setOnClickListener {
                enterChatRoom(room.key)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RoomViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_chat_room, parent, false)
            return RoomViewHolder(view)
        }

        fun addAll(list: List<ChatRoomModel>) {
            this.list.addAll(list)

            notifyDataSetChanged()
        }

        fun add(room: ChatRoomModel) {
            this.list.add(room)
        }

        fun clear() {
            list.clear()
        }

    }

    inner class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsers: TextView = view.findViewById(R.id.tvUsers)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }
}