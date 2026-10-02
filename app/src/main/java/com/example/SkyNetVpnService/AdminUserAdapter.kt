package com.example.SkyNetVpnService

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.SkyNetVpnService.db.UserAccount

class AdminUserAdapter(
    private var users: List<UserAccount>,
    private val onToggleApprove: (UserAccount) -> Unit,
    private val onCancelSubscription: (UserAccount) -> Unit,
    private val onDelete: (UserAccount) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUsername: TextView = itemView.findViewById(R.id.txtUsername)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)
        val txtCreatedAt: TextView = itemView.findViewById(R.id.txtCreatedAt)
        val txtRoleBadge: TextView = itemView.findViewById(R.id.txtRoleBadge)
        val txtApprovalStatus: TextView = itemView.findViewById(R.id.txtApprovalStatus)
        val txtSubscriptionStatus: TextView = itemView.findViewById(R.id.txtSubscriptionStatus)
        val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        val btnCancelSubscription: Button = itemView.findViewById(R.id.btnCancelSubscription)
        val btnDeleteUser: Button = itemView.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.txtUsername.text = user.username
        holder.txtEmail.text = user.email
        holder.txtCreatedAt.text = "Joined: ${user.createdAt}"
        holder.txtRoleBadge.text = user.role.uppercase()

        if (user.role.equals("Admin", ignoreCase = true)) {
            holder.txtRoleBadge.setBackgroundColor(Color.parseColor("#FF4444"))
        } else {
            holder.txtRoleBadge.setBackgroundColor(Color.parseColor("#4A4A6A"))
        }

        // Approval status styling
        if (user.isApproved) {
            holder.txtApprovalStatus.text = "Status: Approved"
            holder.txtApprovalStatus.setTextColor(Color.parseColor("#00FF88"))
            holder.btnApprove.text = "Block / Revoke"
            holder.btnApprove.isEnabled = true
            holder.btnApprove.alpha = 1.0f
        } else {
            holder.txtApprovalStatus.text = "Status: Blocked"
            holder.txtApprovalStatus.setTextColor(Color.parseColor("#FFBB00"))
            holder.btnApprove.text = "Approve"
            holder.btnApprove.isEnabled = true
            holder.btnApprove.alpha = 1.0f
        }

        // Subscription status styling
        if (user.isSubscribed) {
            holder.txtSubscriptionStatus.text = "Subscription: Active"
            holder.txtSubscriptionStatus.setTextColor(Color.parseColor("#00FF88"))
            holder.btnCancelSubscription.isEnabled = true
            holder.btnCancelSubscription.alpha = 1.0f
        } else {
            holder.txtSubscriptionStatus.text = "Subscription: Inactive"
            holder.txtSubscriptionStatus.setTextColor(Color.parseColor("#FF8888"))
            holder.btnCancelSubscription.isEnabled = false
            holder.btnCancelSubscription.alpha = 0.5f
        }

        // Hide admin self-destructive buttons
        if (user.role.equals("Admin", ignoreCase = true)) {
            holder.btnApprove.visibility = View.GONE
            holder.btnCancelSubscription.visibility = View.GONE
            holder.btnDeleteUser.visibility = View.GONE
        } else {
            holder.btnApprove.visibility = View.VISIBLE
            holder.btnCancelSubscription.visibility = View.VISIBLE
            holder.btnDeleteUser.visibility = View.VISIBLE
        }

        holder.btnApprove.setOnClickListener {
            onToggleApprove(user)
        }

        holder.btnCancelSubscription.setOnClickListener {
            onCancelSubscription(user)
        }

        holder.btnDeleteUser.setOnClickListener {
            onDelete(user)
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<UserAccount>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}
