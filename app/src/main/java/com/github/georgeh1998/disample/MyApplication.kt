package com.github.georgeh1998.disample

import android.app.Application
import com.github.georgeh1998.disample.di.AppModule
import com.github.georgeh1998.disample.di.LoggerModule
import com.github.georgeh1998.myhilt.annotations.HiltAndroidApp
import com.github.georgeh1998.myhilt.internal.MyHilt

// 使用するモジュールを配列で指定します。
// AppModule: @Providesの例
// LoggerModule: @Bindsの例
@HiltAndroidApp(modules = [AppModule::class, LoggerModule::class])
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 本来のHiltはGradle Pluginがバイトコード変換してHilt_MyApplicationを継承させることで初期化するが、
        // ここでは手動でinitを呼ぶことでエミュレートする。
        MyHilt.init(this)
    }
}
