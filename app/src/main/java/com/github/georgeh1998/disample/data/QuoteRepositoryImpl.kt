package com.github.georgeh1998.disample.data

import com.github.georgeh1998.myhilt.annotations.Inject

/**
 * QuoteRepositoryの実装クラス。
 *
 * @Inject constructor():
 *     このクラスのコンストラクタに @Inject をつけることで、
 *     Hilt（このサンプルではMyHilt）がこのクラスのインスタンス生成方法を知ることができます。
 *
 *     これにより、他の場所で QuoteRepositoryImpl が必要になったときに、
 *     自動的に `new QuoteRepositoryImpl()` 相当の処理が行われます。
 */
class QuoteRepositoryImpl @Inject constructor() : QuoteRepository {
    override fun getQuote(): String {
        return "Life is simpler than you think."
    }
}
