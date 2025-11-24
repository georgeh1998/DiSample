package com.github.georgeh1998.disample.ui

import androidx.lifecycle.ViewModel
import com.github.georgeh1998.disample.data.QuoteRepository
import com.github.georgeh1998.myhilt.annotations.Inject

/**
 * 画面のデータを管理するViewModel。
 *
 * コンストラクタインジェクションを使用しています。
 *
 * @Inject constructor(...):
 *     MyHiltに対し、「このViewModelを作るときは、引数のrepositoryも一緒に注入してね」と伝えます。
 *
 *     repository引数の型は `QuoteRepository` です。
 *     MyHiltは `AppModule` の `provideQuoteRepository` メソッドを見て、
 *     `QuoteRepositoryImpl` のインスタンスを持ってきます。
 */
class QuoteViewModel @Inject constructor(
    private val repository: QuoteRepository
) : ViewModel() {

    fun getQuote(): String = repository.getQuote()
}
