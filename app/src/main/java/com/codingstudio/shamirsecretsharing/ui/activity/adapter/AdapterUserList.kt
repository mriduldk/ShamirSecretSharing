package com.codingstudio.shamirsecretsharing.ui.activity.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.User
import kotlinx.android.synthetic.main.activity_user.*

class AdapterUserList(private val context : Context) : RecyclerView.Adapter<AdapterUserList.ViewHolderProduct>() {

    private val TAG = "AdapterUserList"

    inner class ViewHolderProduct(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewUserName = itemView.findViewById<TextView>(R.id.textViewUserName)
        private val linearLayoutUserStatus = itemView.findViewById<LinearLayout>(R.id.linearLayoutUserStatus)
        private val linearLayoutUserRequest = itemView.findViewById<LinearLayout>(R.id.linearLayoutUserRequest)
        private val imageViewUserResponse = itemView.findViewById<ImageView>(R.id.imageViewUserResponse)
        private val textViewUserResponse = itemView.findViewById<TextView>(R.id.textViewUserResponse)
        private val textViewRequestUser = itemView.findViewById<TextView>(R.id.textViewRequestUser)

        fun bind(user: User) {

            if (user.status == 0) { // initial

                textViewRequestUser.background = ContextCompat.getDrawable(context, R.drawable.textview_bg_green)

            } else if (user.status == 1) { // request sent

                linearLayoutUserRequest.visibility = View.GONE
                linearLayoutUserStatus.visibility = View.VISIBLE

            } else if (user.status == 2) { // confirmed

                textViewUserResponse.text = "Confirmed"
                textViewUserResponse.setTextColor(ContextCompat.getColor(context, R.color.green))

                textViewRequestUser.visibility = View.GONE
                linearLayoutUserRequest.visibility = View.GONE
                linearLayoutUserStatus.visibility = View.VISIBLE

                DrawableCompat.setTint(
                    DrawableCompat.wrap(imageViewUserResponse.drawable),
                    ContextCompat.getColor(context, R.color.green)
                )

            } else if (user.status == 3) { // Rejected

                textViewRequestUser.visibility = View.VISIBLE
                linearLayoutUserRequest.visibility = View.VISIBLE
                linearLayoutUserStatus.visibility = View.GONE
                textViewRequestUser.text = "Rejected | "
                textViewRequestUser.background = ContextCompat.getDrawable(context, R.drawable.textview_bg_red)
                textViewRequestUser.setCompoundDrawablesWithIntrinsicBounds( 0, 0,  R.drawable.ic_baseline_refresh_24, 0);

            }

            textViewUserName.text = user.user_name


            textViewRequestUser.setOnClickListener {

                linearLayoutUserRequest.visibility = View.GONE
                linearLayoutUserStatus.visibility = View.VISIBLE

                onUserClickedListener?.let {
                    it(user, adapterPosition)
                }
            }


        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.user_id == newItem.user_id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderProduct {
        return ViewHolderProduct(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_user_list, parent, false)
        )
    }



    override fun onBindViewHolder(holder: ViewHolderProduct, position: Int) {
        val orderDetails = differ.currentList[position]
        (holder as ViewHolderProduct).bind(orderDetails)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onUserClickedListener : ((User, Int) -> Unit) ?= null

    fun setOnUserClickedListener(listener : (User, Int) -> Unit) {
        onUserClickedListener = listener
    }



}