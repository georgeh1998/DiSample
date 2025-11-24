package com.github.georgeh1998.myhilt.internal

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.github.georgeh1998.myhilt.annotations.AndroidEntryPoint
import com.github.georgeh1998.myhilt.annotations.Binds
import com.github.georgeh1998.myhilt.annotations.HiltAndroidApp
import com.github.georgeh1998.myhilt.annotations.Inject
import com.github.georgeh1998.myhilt.annotations.Module
import com.github.georgeh1998.myhilt.annotations.Provides
import com.github.georgeh1998.myhilt.annotations.Singleton
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * MyHilt: 自作DI（依存性注入）コンテナのコアロジック
 *
 * このオブジェクトはアプリケーション全体で一つのインスタンス（シングルトン）として振る舞い、
 * 依存関係の登録、解決、インスタンスのキャッシュ管理を行います。
 *
 * 本物のDagger Hiltはコンパイル時にコード生成を行いますが、
 * このサンプルではリフレクション（実行時の型情報検査）を使用してDIを実現しています。
 */
object MyHilt {
    // @Singletonがついたインスタンスを保持するキャッシュ（シングルトンスコープ）
    // Key: クラスの型 (Class<*>), Value: インスタンス (Any)
    private val singletonScope = mutableMapOf<Class<*>, Any>()

    // 依存関係の生成ロジックを保持するマップ
    // Key: 要求されるクラスの型, Value: インスタンスを生成して返す関数
    private val bindings = mutableMapOf<Class<*>, () -> Any>()

    /**
     * アプリケーション起動時に呼び出される初期化メソッド
     *
     * 1. @HiltAndroidApp アノテーションから使用するモジュールを読み込みます。
     * 2. Activityのライフサイクルを監視し、Activity生成時に自動で依存性を注入する仕組みを登録します。
     */
    fun init(application: Application) {
        // 1. モジュールの読み込み
        // アプリケーションクラスについた @HiltAndroidApp アノテーションを探す
        val annotation = application::class.annotations.find { it is HiltAndroidApp } as? HiltAndroidApp
        // modules引数に指定されたクラス（例: AppModule）を一つずつ登録する
        annotation?.modules?.forEach { moduleClass ->
            registerModule(moduleClass.java)
        }

        // 2. Activityへの自動注入の登録
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // Activityが作成されたタイミングで、@AndroidEntryPointがついているかチェック
                if (activity::class.annotations.any { it is AndroidEntryPoint }) {
                    // ついていればフィールドインジェクションを実行
                    Injector.inject(activity)
                }
            }

            // その他のライフサイクルイベントは今回は使用しない
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    /**
     * モジュールクラス（@Module）を解析し、@Providesや@Bindsメソッドをbindingsマップに登録する
     */
    private fun registerModule(moduleClass: Class<*>) {
        // @Moduleがついていないクラスは無視
        if (!moduleClass.isAnnotationPresent(Module::class.java)) return

        // モジュールのインスタンスを生成（objectクラスの場合はobjectInstanceを取得）
        val moduleInstance = try {
             moduleClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            try {
                moduleClass.kotlin.objectInstance ?: moduleClass.getDeclaredConstructor().newInstance()
            } catch (e2: Exception) {
                null
            }
        }

        // モジュール内の全メソッドをスキャン
        moduleClass.methods.forEach { method ->
            if (method.isAnnotationPresent(Provides::class.java)) {
                // --- @Provides の処理 ---
                // メソッドの戻り値を「提供可能な型」とする
                val returnType = method.returnType

                // bindingsマップに「インスタンス生成関数」を登録
                bindings[returnType] = {
                    // メソッドの引数（依存関係）を再帰的に解決
                    val args = method.parameterTypes.map { resolve(it) }.toTypedArray()
                    // メソッドを実行してインスタンスを生成
                    val instance = method.invoke(moduleInstance, *args)

                    // @Singletonがついている場合はキャッシュする
                    if (method.isAnnotationPresent(Singleton::class.java)) {
                        singletonScope.getOrPut(returnType) { instance }
                    } else {
                        instance
                    }
                }
            } else if (method.isAnnotationPresent(Binds::class.java)) {
                // --- @Binds の処理 ---
                // @Bindsはインターフェースと実装を結びつける。
                // メソッドの引数が「実装クラス」、戻り値が「インターフェース」。
                val returnType = method.returnType
                val implType = method.parameterTypes[0]

                bindings[returnType] = {
                    // 実装クラスを解決して返す
                    resolve(implType)
                }

                // @Singletonの簡易対応
                if (method.isAnnotationPresent(Singleton::class.java)) {
                     val instance = resolve(implType)
                     singletonScope[returnType] = instance
                     instance
                }
            }
        }
    }

    /**
     * 依存関係解決のメインメソッド
     * 指定されたクラス(clazz)のインスタンスを返します。
     */
    fun <T> resolve(clazz: Class<T>): T {
        // 1. Singletonキャッシュの確認
        // すでに生成済みのSingletonインスタンスがあればそれを返す
        if (singletonScope.containsKey(clazz)) {
            @Suppress("UNCHECKED_CAST")
            return singletonScope[clazz] as T
        }

        // 2. モジュールバインディング(@Provides / @Binds)の確認
        // bindingsマップに登録されていれば、その生成関数を呼び出す
        if (bindings.containsKey(clazz)) {
            @Suppress("UNCHECKED_CAST")
            return bindings[clazz]!!.invoke() as T
        }

        // 3. コンストラクタインジェクション(@Inject)の確認
        // 上記で見つからない場合、そのクラス自体のコンストラクタに@Injectがついているか探す
        val constructors = clazz.constructors
        val injectConstructor = constructors.find { it.isAnnotationPresent(Inject::class.java) }
            // @Injectがなくても引数なしコンストラクタがあればそれを使う（フォールバック）
            ?: constructors.firstOrNull { it.parameterCount == 0 }

        if (injectConstructor != null) {
            // コンストラクタの引数を再帰的に解決
            val args = injectConstructor.parameterTypes.map { resolve(it) }.toTypedArray()
            // インスタンス生成
            val instance = injectConstructor.newInstance(*args) as T

            // クラス自体に@Singletonがついている場合はキャッシュ
            if (clazz.isAnnotationPresent(Singleton::class.java)) {
                singletonScope[clazz] = instance as Any
            }
            return instance
        }

        // 解決不能な場合
        throw IllegalStateException("Cannot resolve dependency for ${clazz.name}. No @Inject constructor or @Provides found.")
    }
}

/**
 * フィールドインジェクションを担当するヘルパーオブジェクト
 */
object Injector {
    fun inject(target: Any) {
        var clazz: Class<*>? = target.javaClass
        // 親クラスまで遡ってフィールドを探す
        while (clazz != null) {
            for (field in clazz.declaredFields) {
                // @Injectがついているフィールドを見つける
                if (field.isAnnotationPresent(Inject::class.java)) {
                    field.isAccessible = true // privateフィールドにもアクセス可能にする
                    // MyHiltを使って依存オブジェクトを取得
                    val dependency = MyHilt.resolve(field.type)
                    // フィールドに値をセット
                    field.set(target, dependency)
                }
            }
            clazz = clazz.superclass
        }
    }
}
