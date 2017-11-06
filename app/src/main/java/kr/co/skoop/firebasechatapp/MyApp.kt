package kr.co.skoop.firebasechatapp

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

/**
 * Created by Administrator on 2017-10-14.
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}