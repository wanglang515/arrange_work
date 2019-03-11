package com.lang.hospital

import android.os.Bundle
import android.view.KeyEvent
import com.js.common.data.SpfManager
import com.lang.hospital.fragment.ArrangeWorkFra
import com.lang.hospital.fragment.ResetUserFra
import com.lang.hospital.fragment.SelectFra
import com.sh.tv.base.BaseActivity


class MainActivity : BaseActivity() {


    override fun getViewRootId(): Int {
        return R.id.main_content
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SpfManager.initData(applicationContext)
        switchFragmentForward(SelectFra::class.java.name, Bundle(), true)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (curFragment?.tag == ResetUserFra::class.java.name
                    || curFragment?.tag == ArrangeWorkFra::class.java.name
                ) {
                    curFragment?.fragmentManager?.popBackStack()
                    return true
                }
                if (curFragment?.tag == SelectFra::class.java.name) {
                    finish()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
