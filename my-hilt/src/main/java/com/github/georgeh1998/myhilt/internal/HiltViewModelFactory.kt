package com.github.georgeh1998.myhilt.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

/**
 * ViewModelの生成をMyHiltに委譲するためのFactoryクラス。
 *
 * AndroidのViewModelは、通常システム（ViewModelProvider）によってインスタンス化されますが、
 * デフォルトでは引数なしコンストラクタしか呼べません。
 *
 * DIを使う場合、ViewModelのコンストラクタにRepositoryなどを渡す必要があるため、
 * このFactoryを使って「MyHilt.resolve」経由でインスタンス化するようにフックします。
 */
class HiltViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return try {
            // 要求されたViewModelクラスのインスタンスを、MyHiltを使って生成（依存関係解決）して返す
            MyHilt.resolve(modelClass)
        } catch (e: Exception) {
            throw RuntimeException("Failed to create ViewModel ${modelClass.name}", e)
        }
    }
}
