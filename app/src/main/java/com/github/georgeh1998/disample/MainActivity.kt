package com.github.georgeh1998.disample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.georgeh1998.disample.ui.theme.DiSampleTheme
import com.github.georgeh1998.myhilt.annotations.AndroidEntryPoint
import com.github.georgeh1998.disample.ui.QuoteViewModel
import com.github.georgeh1998.myhilt.internal.HiltViewModelFactory
import androidx.lifecycle.ViewModelProvider

/**
 * アプリのメイン画面（Activity）。
 *
 * @AndroidEntryPoint:
 *     このActivityがDIコンテナの注入対象であることを示します。
 *     MyHiltでは、ActivityのonCreate前に自動的にフィールドインジェクション（@Inject変数の解決）を行います。
 *
 *     今回はViewModelの取得に特化したDIを行っています。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- ViewModelの取得 ---
        // 通常のHiltでは `private val viewModel: QuoteViewModel by viewModels()` と書くだけで済みますが、
        // 今回は自作DIなので、明示的に `HiltViewModelFactory` を指定しています。
        //
        // HiltViewModelFactoryは、MyHiltを使って依存関係が解決されたViewModelを作成する工場クラスです。
        val viewModel = ViewModelProvider(this, HiltViewModelFactory())[QuoteViewModel::class.java]

        setContent {
            DiSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // ViewModelからデータを取得して表示
                    Greeting(
                        name = viewModel.getQuote(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Quote: $name",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DiSampleTheme {
        Greeting("Android")
    }
}
