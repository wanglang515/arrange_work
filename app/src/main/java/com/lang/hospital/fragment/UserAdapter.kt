package com.lang.hospital.fragment

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.js.common.data.SpfManager
import com.lang.hospital.R

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserHolder>() {

    private val names = ArrayList<String>()

    fun setNames(str: String) {
        if (!str.isBlank()) {
            names.clear()
            val ss = str.split("#")
            names.addAll(ss.toList())
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): UserHolder {
        return UserHolder(LayoutInflater.from(p0.context).inflate(R.layout.user_item, p0, false))
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(p0: UserHolder, p1: Int) {
        p0.name?.text = names[p1]
        p0.id?.text = (p1 + 1).toString()
        p0.name?.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                AlertDialog.Builder(v?.context)
                    .setMessage("是否删除成员${names[p1]}")
                    .setNegativeButton("取消") { _, _ -> }
                    .setPositiveButton("确定") { _, _ ->
                        names.removeAt(p1)
                        notifyItemRemoved(p1)
                        notifyDataSetChanged()
                        var ss = ""
                        for (name in names) {
                            ss += "$name#"
                        }
                        SpfManager.putData("users", if (ss.isBlank()) "" else ss.substring(0, ss.length - 1))

                    }
                    .create().show()
                return true
            }
        })
    }

    class UserHolder : RecyclerView.ViewHolder {
        var name: TextView? = null
        var id: TextView? = null

        constructor(view: View) : super(view) {
            name = view.findViewById(R.id.user_name)
            id = view.findViewById(R.id.user_id)
        }
    }
}