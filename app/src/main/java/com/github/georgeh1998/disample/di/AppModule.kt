package com.github.georgeh1998.disample.di

import com.github.georgeh1998.disample.data.Logger
import com.github.georgeh1998.disample.data.QuoteRepository
import com.github.georgeh1998.disample.data.QuoteRepositoryImpl
import com.github.georgeh1998.myhilt.annotations.InstallIn
import com.github.georgeh1998.myhilt.annotations.Module
import com.github.georgeh1998.myhilt.annotations.Provides
import com.github.georgeh1998.myhilt.annotations.Singleton
import com.github.georgeh1998.myhilt.annotations.SingletonComponent

/**
 * アプリケーション全体の依存関係を定義するモジュール（@Provides版）。
 *
 * @Provides を使用してインスタンスを手動で生成・返却する例です。
 * 実装クラスが外部ライブラリのもので @Inject をつけられない場合や、
 * 生成時に複雑な初期化ロジックが必要な場合に使われます。
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * QuoteRepositoryの実装を提供するメソッド。
     *
     * 引数に `Logger` を指定しています。
     * MyHiltは `LoggerModule` で定義されたバインディングを使って `Logger` (DebugLogger) を解決し、
     * ここに渡してくれます。
     *
     * 受け取ったLoggerを使って、手動で `QuoteRepositoryImpl` を生成して返しています。
     */
    @Provides
    @Singleton
    fun provideQuoteRepository(logger: Logger): QuoteRepository {
        return QuoteRepositoryImpl(logger)
    }
}
