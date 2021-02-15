package io.mkdirs.abma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.mkdirs.abma.controller.ChatAdapter
import io.mkdirs.abma.fragment.ConversationsFragment
import io.mkdirs.abma.fragment.ProfileFragment
import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.User
import io.mkdirs.abma.utils.DB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), ChildEventListener {
    lateinit var adapter:ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ChatAdapter(this)
        adapter.setNotifyOnChange(false)

        DB.goOnline()
        DB.getReference("chats").addChildEventListener(this)

        val profileFragment = ProfileFragment()
        val conversationsFragment = ConversationsFragment(adapter)
        makeCurrentFragment(conversationsFragment)

        main_bottom_navigation_view.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_profile -> makeCurrentFragment(profileFragment)

                R.id.bottom_conversations -> makeCurrentFragment(conversationsFragment)
            }

            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_wrapper_frame_layout, fragment)
            commit()
        }
    }


    private suspend fun notifyChatAsync(chat: Chat){
        GlobalScope.launch {
            adapter.fetchLastMessage(chat)
            withContext(Dispatchers.Main){
                adapter.notifyDataSetChanged()
                adapter.setNotifyOnChange(false)
            }
        }.join()
    }

    override fun onCancelled(error: DatabaseError) {
        //Do not touch
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        //Do not touch
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        GlobalScope.launch {
            val chat = async{ Chat.fromDB(snapshot.key!!)}.await()
            val data = Tasks.await(DB.getReference("chats_participants/${chat.uid}").get())
            if(data.hasChild(User.currentUser!!.uid)){
                withContext(Dispatchers.Main) {
                    adapter.remove(chat)
                    adapter.add(chat)
                }
                notifyChatAsync(chat)
            }
        }
    }

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        GlobalScope.launch {
            val chat = async{ Chat.fromDB(snapshot.key!!)}.await()
            val data = Tasks.await(DB.getReference("chats_participants/${chat.uid}").get())
            if(data.hasChild(User.currentUser!!.uid)){
                withContext(Dispatchers.Main){
                    adapter.add(chat)
                }
                notifyChatAsync(chat)
            }
        }
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        for(i in 0 until adapter.count){
            val chat = adapter.getItem(i)
            if(chat?.uid == snapshot.key){
                adapter.remove(chat)
                adapter.notifyDataSetChanged()
                adapter.setNotifyOnChange(false)
                break
            }
        }
    }

}