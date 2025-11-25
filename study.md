# My Hilt Study Guide

このプロジェクトは、Dagger Hiltを使わずに、HiltのようなDI（依存性注入）の仕組みを自作したサンプルです。
Hiltが裏側でどのような役割を果たしているのか（特に依存解決の流れ）を理解するために、**リフレクション（Reflection）**を用いてDIコンテナを簡易実装しています。

## 概要

`my-hilt` モジュールが簡易DIライブラリを提供し、`app` モジュールがそれを利用しています。

### コンパイル時 DI (Real Hilt) vs 実行時 DI (This Sample)

*   **Real Hilt (Dagger)**: アノテーションプロセッサ (KAPT/KSP) を使い、コンパイル時に依存関係解決のコード（FactoryクラスやInjectorクラス）を**自動生成**します。これにより、実行時のオーバーヘッドがなく、コンパイル時にエラー（依存関係の不足など）を検知できます。
*   **This Sample (Reflection)**: アノテーションがついたクラスやメソッドを**実行時に**探し出し、依存関係を解決してインスタンスを生成・注入します。実装はシンプルですが、実行速度は遅く、エラーは実行時にしか分かりません。

## ファイル構成と役割

### my-hilt モジュール

Hiltの機能を提供するライブラリ部分です。

*   **`annotations/`**: Hiltでおなじみのアノテーション定義です。
    *   `@Inject`: 依存性を注入してほしいコンストラクタやフィールドにつけます。
    *   `@Singleton`: アプリ内で唯一のインスタンス（シングルトン）であることを示します。
    *   `@AndroidEntryPoint`: ActivityなどのAndroidコンポーネントに依存性を注入することを示します。
    *   `@HiltAndroidApp`: アプリケーションクラスにつけ、DIコンテナの初期化トリガーとします。
    *   `@Module`, `@Provides`, `@InstallIn`: インターフェースの実装などを提供するモジュール定義に使います。

*   **`internal/MyHilt.kt`**: DIエンジンの本体です。
    *   `bindings`: `@Provides` で定義された「型と生成方法」のマップ。
    *   `singletonScope`: `@Singleton` インスタンスのキャッシュ。
    *   `resolve(clazz)`: 要求された型のインスタンスを生成・取得します。
        1. シングルトンキャッシュにあればそれを返す。
        2. `@Module` のバインディングにあればそれを使って生成。
        3. `@Inject` コンストラクタがあれば、引数を再帰的に解決してインスタンス化。
    *   `init(application)`: アプリ起動時に呼び出され、`@Module` を読み込み、Activityのライフサイクルを監視して自動注入の準備をします。

*   **`internal/Injector.kt`**: フィールドインジェクションを担当します。
    *   `inject(target)`: 対象オブジェクトのフィールドをスキャンし、`@Inject` がついていれば `MyHilt.resolve` で取得したインスタンスを代入します。

*   **`internal/HiltViewModelFactory.kt`**: ViewModelへのDIをサポートします。
    *   通常のViewModel生成フローに割り込み、`MyHilt` を使って依存関係が解決されたViewModelを返します。

### app モジュール

自作DIを利用するアプリケーションです。

*   **`MyApplication.kt`**:
    *   `@HiltAndroidApp` をつけ、`modules` 引数で使用するモジュールを指定しています。
    *   `MyHilt.init(this)` を呼び出しています（本物のHiltではバイトコード変換で自動で行われます）。

*   **`di/AppModule.kt`** (@Providesの例):
    *   `@Provides` を使用して `QuoteRepository` のインスタンス生成方法を定義しています。
    *   メソッドの引数として `Logger` を受け取っており、MyHiltが自動的に `Logger` のインスタンスを解決して渡してくれます。
    *   受け取ったLoggerを使って、手動で `QuoteRepositoryImpl(logger)` を生成しています。

*   **`di/LoggerModule.kt`** (@Bindsの例):
    *   `@Module` ですが、こちらは `interface` として定義されています。
    *   `@Binds` を使い、`Logger` インターフェースの実装が `DebugLogger` であることを宣言しています。
    *   `@Binds` はメソッドの中身を書く必要がなく、より効率的にインターフェースと実装を紐付けられます。

*   **`data/QuoteRepositoryImpl.kt`**:
    *   `@Inject constructor(private val logger: Logger)` となり、Loggerへの依存関係が追加されました。
    *   MyHiltは `LoggerModule` の定義に従って `DebugLogger` を生成し、ここに注入します。

*   **`ui/QuoteViewModel.kt`**:
    *   `@Inject constructor(private val repository: QuoteRepository)` でリポジトリを受け取ります。
    *   DIコンテナが自動的に `QuoteRepository` の実装を探して注入してくれます。

*   **`MainActivity.kt`**:
    *   `@AndroidEntryPoint` がついているため、`onCreate` 前にDI処理が走る仕組み（MyHilt内で実装）になっています。
    *   `ViewModelProvider` に `HiltViewModelFactory` を渡すことで、DI済みのViewModelを取得しています。

## まとめ

このサンプルを通じて、Hiltのアノテーションが「どこで」「何を」注入するかを指定するマーカーであり、裏側で「誰か（DIコンテナ）」がそれを読み取ってオブジェクトグラフを構築していることが理解できます。
本物のHiltはこれをコード生成で行うため、より高速で安全ですが、基本的な考え方はこのサンプルと同じです。
