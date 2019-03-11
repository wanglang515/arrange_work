package com.sh.tv.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.gftv.comlibrary.callback.FragmentSwitchInterface
import com.lang.hospital.R

/**
 * @Description:
 * @Created by wl on 2018-01-16.
 */
abstract class BaseActivity : FragmentActivity(), FragmentSwitchInterface {

    protected var curFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.addOnBackStackChangedListener {
            curFragment = supportFragmentManager.findFragmentById(getViewRootId())
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun switchFragmentForward(target: String, bundle: Bundle, anim: Boolean) {
        try {
            val fragment = Class.forName(target).newInstance() as Fragment

            fragment.arguments = bundle
            val transaction = supportFragmentManager.beginTransaction()
            if (anim)
//                transaction.setCustomAnimations(0x00, R.anim.lessen_out, R.anim.slide_in_left, R.anim.slide_out_right)
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            if (curFragment != null) {
                transaction.hide(curFragment!!)
                fragment.setTargetFragment(curFragment, 0)
            }
            transaction.add(getViewRootId(), fragment, target)
            transaction.addToBackStack(target)
            transaction.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    abstract fun getViewRootId(): Int
}