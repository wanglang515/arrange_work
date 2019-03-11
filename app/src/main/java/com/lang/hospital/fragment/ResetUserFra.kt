package com.lang.hospital.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.js.common.data.SpfManager
import com.lang.hospital.R
import kotlinx.android.synthetic.main.reset_user_info.*


class ResetUserFra : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reset_user_info, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = UserAdapter()
        recycler_user.layoutManager = GridLayoutManager(context, 3)
        recycler_user.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        adapter.setNames(SpfManager.getStringData("users"))
        recycler_user.adapter = adapter

        add_button_user.setOnClickListener {
            val editLayout = LayoutInflater.from(context).inflate(R.layout.user_add, null)
            AlertDialog.Builder(context)
                .setView(editLayout)
                .setNegativeButton("取消") { _, _ -> }
                .setPositiveButton("确定") { _, _ ->
                    val name = editLayout.findViewById<EditText>(R.id.user_add_edit).text
                    if (!name?.isBlank()!!) {
                        var ss = SpfManager.getStringData("users")
                        if (ss.isEmpty())
                            SpfManager.putData("users", ss.plus(name))
                        else
                            SpfManager.putData("users", ss.plus("#$name"))
                        adapter.setNames(SpfManager.getStringData("users"))
                    }
                }.create().show()
        }
    }
}