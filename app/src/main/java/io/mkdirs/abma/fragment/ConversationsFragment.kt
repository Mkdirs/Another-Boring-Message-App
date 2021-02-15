package io.mkdirs.abma.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.mkdirs.abma.R
import io.mkdirs.abma.controller.ChatAdapter
import kotlinx.android.synthetic.main.fragment_conversations.*


class ConversationsFragment(private val adapter:ChatAdapter) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        conversations_list_view.adapter = adapter
    }





}