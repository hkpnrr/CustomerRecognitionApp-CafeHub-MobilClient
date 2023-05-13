package com.project.cafehub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.R
import com.project.cafehub.model.ChatMessage
import com.project.cafehub.model.CurrentUser

class ChatAdapter :RecyclerView.Adapter<ChatAdapter.ChatHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT =1
    private val VIEW_TYPE_MESSAGE_RECEIVED =2

    class ChatHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    private val diffUtil = object :DiffUtil.ItemCallback<ChatMessage>(){
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem==newItem
        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this,diffUtil)

    var chatMessages :List<ChatMessage>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {

        val chat =chatMessages.get(position)

        if(chat.fromId==CurrentUser.user.id.toString()){
            return VIEW_TYPE_MESSAGE_SENT
        }
        else{
            return VIEW_TYPE_MESSAGE_RECEIVED
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {

        if(viewType==VIEW_TYPE_MESSAGE_RECEIVED){

            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_chat_to_message,parent,false)
            return ChatHolder(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_chat_from_message,parent,false)
            return ChatHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.chatTextView)
        textView.text = "${chatMessages.get(position).message}"
    }
}