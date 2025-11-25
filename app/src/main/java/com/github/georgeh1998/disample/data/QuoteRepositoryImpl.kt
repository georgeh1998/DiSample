package com.github.georgeh1998.disample.data

import com.github.georgeh1998.myhilt.annotations.Inject

/**
 * QuoteRepositoryの実装クラス。
 *
 * コンストラクタで Logger を受け取ります。
 * MyHiltは LoggerModule を通じて DebugLogger を注入してくれます。
 */
class QuoteRepositoryImpl @Inject constructor(
    private val logger: Logger
) : QuoteRepository {
    override fun getQuote(): String {
        logger.log("Getting quote...")
        return "Life is simpler than you think."
    }
}
