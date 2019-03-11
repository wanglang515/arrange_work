package com.lang.hospital.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gftv.comlibrary.callback.FragmentSwitchInterface
import com.lang.hospital.R
import kotlinx.android.synthetic.main.select_fra.*

class SelectFra : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.select_fra, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_arrange_work.setOnClickListener {
            (activity as FragmentSwitchInterface).switchFragmentForward(
                ArrangeWorkFra::class.java.name,
                Bundle(),
                true
            )
        }

        button_user_info.setOnClickListener {
            (activity as FragmentSwitchInterface).switchFragmentForward(ResetUserFra::class.java.name, Bundle(), true)
        }
    }
}