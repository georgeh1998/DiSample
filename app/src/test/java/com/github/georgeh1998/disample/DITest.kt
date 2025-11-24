package com.github.georgeh1998.disample

import com.github.georgeh1998.disample.data.QuoteRepository
import com.github.georgeh1998.disample.data.QuoteRepositoryImpl
import com.github.georgeh1998.disample.di.AppModule
import com.github.georgeh1998.disample.ui.QuoteViewModel
import com.github.georgeh1998.myhilt.annotations.HiltAndroidApp
import com.github.georgeh1998.myhilt.internal.MyHilt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method

// Mock Application class for testing
@HiltAndroidApp(modules = [AppModule::class])
class TestApplication : android.app.Application()

class DITest {

    @Before
    fun setup() {
        // Since MyHilt is a singleton object, we might need to reset it,
        // but for this simple test, we just assume clean state or idempotent behavior.
        // However, `registerModule` is private, but `init` calls it.
        // We can't easily mock Application context for `init` without Robolectric.
        // So we will test `resolve` directly after manually triggering module registration via reflection or
        // by making `registerModule` accessible for test, or just testing the `resolve` logic if we manually add bindings.

        // Actually, let's use reflection to invoke `registerModule` on MyHilt to simulate initialization without Android context.
        val method: Method = MyHilt::class.java.getDeclaredMethod("registerModule", Class::class.java)
        method.isAccessible = true
        method.invoke(MyHilt, AppModule::class.java)
    }

    @Test
    fun testRepositoryInjection() {
        // Test if QuoteRepository is correctly resolved to QuoteRepositoryImpl
        val repository = MyHilt.resolve(QuoteRepository::class.java)
        assertNotNull(repository)
        assertTrue(repository is QuoteRepositoryImpl)
        assertEquals("Life is simpler than you think.", repository.getQuote())
    }

    @Test
    fun testViewModelInjection() {
        // Test if ViewModel is created with repository injected
        val viewModel = MyHilt.resolve(QuoteViewModel::class.java)
        assertNotNull(viewModel)
        assertEquals("Life is simpler than you think.", viewModel.getQuote())
    }
}
