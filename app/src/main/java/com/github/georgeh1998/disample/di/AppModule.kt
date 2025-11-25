package com.github.georgeh1998.disample.di

import com.github.georgeh1998.disample.data.QuoteRepository
import com.github.georgeh1998.disample.data.QuoteRepositoryImpl
import com.github.georgeh1998.myhilt.annotations.Binds
import com.github.georgeh1998.myhilt.annotations.InstallIn
import com.github.georgeh1998.myhilt.annotations.Module
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
interface AppModule {

    /**
     * QuoteRepositoryインターフェースの実装をバインドするメソッド。
     *
     * @Binds を使うことで、@Provides で手動でインスタンスを生成するコードを書かずに、
     * 「QuoteRepositoryImpl を QuoteRepository として使う」という宣言だけで済みます。
     *
     * メソッドの引数 (QuoteRepositoryImpl) は、Hiltが自動的に解決（@Inject constructorから生成）します。
     * 戻り値の型 (QuoteRepository) が、このメソッドが提供する型になります。
     *
     * - インターフェースである必要があります。
     * - 抽象メソッドである必要があります。
     * - 引数は一つだけ（実装クラス）です。
     */
    @Binds
    @Singleton
    fun bindQuoteRepository(impl: QuoteRepositoryImpl): QuoteRepository
}
