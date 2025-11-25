package com.github.georgeh1998.disample

import android.app.Application
import com.github.georgeh1998.disample.di.AppModule
import com.github.georgeh1998.disample.di.LoggerModule
import com.github.georgeh1998.myhilt.annotations.HiltAndroidApp
import com.github.georgeh1998.myhilt.internal.MyHilt

/**
 * アプリケーションクラス。
 *
 * @HiltAndroidApp:
 *   このアノテーションをつけると、2つのことが起こります。
 *
 *   1. コンパイル時 (KSP):
 *      `Hilt_MyApplication` というクラスが自動生成されます。
 *      このクラスは `onCreate` で `MyHilt.init(this)` を呼び出します。
 *
 *   2. バイトコード変換時 (Gradle Plugin + ASM):
 *      この `MyApplication` クラスの親クラスが、`Application` から `Hilt_MyApplication` に
 *      書き換えられます。
 *
 *   結果として、開発者が手動で `MyHilt.init(this)` を書く必要がなくなります。
 */
@HiltAndroidApp(modules = [AppModule::class, LoggerModule::class])
class MyApplication : Application() {
    // Gradle Pluginにより、コンパイル後のバイトコードでは superclass が Hilt_MyApplication になります。
    // Hilt_MyApplication の onCreate で DI の初期化が行われるため、ここでは何も書く必要がありません。
}
