package kr.co.skoop.firebasechatapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Administrator on 2017-10-14.
 */
class MainActivity : AppCompatActivity() {
    val database : FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUserSignIn.setOnClickListener {
            checkUsers()
        }
    }

    private fun checkUsers() {
        val userId = etUserId.text.toString()
        val nickName = etNickName.text.toString()

        if (userId.isEmpty()) {
            Toast.makeText(applicationContext, "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (nickName.isEmpty()) {
            Toast.makeText(applicationContext, "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        ResourceManager.currentUser = UserModel(userId, nickName)

        database.getReference("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userData: DataSnapshot?) {
                if (userData == null) {
                    createUser(userId)
                    return
                }

                val user = userData.getValue(UserModel::class.java)

                if (user == null) {
                    createUser(userId)
                    return
                }

                if (user.userId == ResourceManager.currentUser!!.userId) {
                    startActivity(Intent(this@MainActivity, ChatListActivity::class.java))
                }
            }

            override fun onCancelled(error: DatabaseError?) {

            }
        })
    }

    private fun createUser(userId : String) {
        database.getReference("users").child(userId).setValue(ResourceManager.currentUser, { error, dataRef ->
            if (dataRef.key == userId) {
                startActivity(Intent(this, ChatListActivity::class.java))
            }
        })
    }
}