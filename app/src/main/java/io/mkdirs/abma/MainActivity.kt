package io.mkdirs.abma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.mkdirs.abma.controller.ChatAdapter
import io.mkdirs.abma.fragment.ConversationsFragment
import io.mkdirs.abma.fragment.ProfileFragment
import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.Message
import io.mkdirs.abma.model.User
import io.mkdirs.abma.utils.DB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.Comparator

class MainActivity : AppCompatActivity(), ChildEventListener {
    lateinit var adapter:ChatAdapter
    lateinit var profileFragment:ProfileFragment
    lateinit var conversationsFragment:ConversationsFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ChatAdapter(this)

        DB.getReference("chats").addChildEventListener(this)

        profileFragment = ProfileFragment()
        conversationsFragment = ConversationsFragment(adapter)

        main_bottom_navigation_view.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_profile -> makeCurrentFragment(profileFragment)

                R.id.bottom_conversations -> makeCurrentFragment(conversationsFragment)
            }

            true
        }

        main_bottom_navigation_view.selectedItemId = R.id.bottom_conversations
    }

    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_wrapper_frame_layout, fragment)
            commit()
        }
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
                var ind = -1
                for(i in 0 until adapter.count){
                    if(adapter.getItem(i) == chat){
                        ind = i
                        break
                    }
                }

                if(ind != -1){
                    withContext(Dispatchers.Main) {
                        adapter.remove(adapter.getItem(ind))
                        adapter.add(chat)
                    }
                }


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
            }
        }
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        for(i in 0 until adapter.count){
            val chat = adapter.getItem(i)
            if(chat?.uid == snapshot.key){
                adapter.remove(chat)
                break
            }
        }
    }

}