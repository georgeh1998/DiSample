package com.github.georgeh1998.disample.di

import com.github.georgeh1998.disample.data.DebugLogger
import com.github.georgeh1998.disample.data.Logger
import com.github.georgeh1998.myhilt.annotations.Binds
import com.github.georgeh1998.myhilt.annotations.InstallIn
import com.github.georgeh1998.myhilt.annotations.Module
import com.github.georgeh1998.myhilt.annotations.Singleton
import com.github.georgeh1998.myhilt.annotations.SingletonComponent

/**
 * Loggerに関する依存関係を定義するモジュール。
 *
 * @Binds を使用する例です。
 * インターフェースと実装の結合のみを行う場合は、このようにインターフェースでモジュールを定義し、
 * 抽象メソッド（Abstract Method）に @Binds をつけるのが一般的かつ効率的です。
 */
@Module
@InstallIn(SingletonComponent::class)
interface LoggerModule {

    @Binds
    @Singleton
    fun bindLogger(impl: DebugLogger): Logger
}
