package com.github.georgeh1998.disample.data

import android.util.Log
import com.github.georgeh1998.myhilt.annotations.Inject
import com.github.georgeh1998.myhilt.annotations.Singleton

interface Logger {
    fun log(message: String)
}

/**
 * Loggerの実装クラス。
 *
 * @Inject constructor():
 *     これを書くことでMyHiltがこのクラスのインスタンスを生成できるようになります。
 */
class DebugLogger @Inject constructor() : Logger {
    override fun log(message: String) {
        // 実際にはLog.dなどを使いますが、ユニットテストで動くように標準出力にしています
        println("DebugLogger: $message")
    }
}
