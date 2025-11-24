package com.github.georgeh1998.disample.di

import com.github.georgeh1998.disample.data.QuoteRepository
import com.github.georgeh1998.disample.data.QuoteRepositoryImpl
import com.github.georgeh1998.myhilt.annotations.InstallIn
import com.github.georgeh1998.myhilt.annotations.Module
import com.github.georgeh1998.myhilt.annotations.Provides
import com.github.georgeh1998.myhilt.annotations.Singleton
import com.github.georgeh1998.myhilt.annotations.SingletonComponent

/**
 * Hiltモジュール: 依存関係の提供方法を定義する場所
 *
 * インターフェースの実装クラスを提供する場合や、外部ライブラリのクラス（Retrofitなど）を
 * 生成する場合に使用します。
 *
 * @Module: これがDIのモジュールであることを示します。
 * @InstallIn(SingletonComponent::class):
 *    このモジュールで提供されるインスタンスの寿命（スコープ）を定義します。
 *    SingletonComponentは「アプリ全体で有効」という意味です。
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * QuoteRepositoryインターフェースの実装を提供するメソッド。
     *
     * アプリ内で `QuoteRepository` が必要になったとき（例えばViewModelのコンストラクタで要求されたとき）、
     * このメソッドが呼ばれてインスタンスが提供されます。
     *
     * @Provides: インスタンスを提供するメソッドにつけます。
     * @Singleton: アプリ全体で一つのインスタンスを使い回すことを示します。
     *             これがないと、要求されるたびに新しいインスタンスが生成されます。
     */
    @Provides
    @Singleton
    fun provideQuoteRepository(): QuoteRepository {
        // QuoteRepositoryの実体として QuoteRepositoryImpl を返します。
        // QuoteRepositoryImpl自体も @Inject コンストラクタを持っているので、
        // MyHiltが自動生成することも可能ですが、インターフェースへのバインドを明示するためにここで記述しています。
        return QuoteRepositoryImpl()
    }
}
