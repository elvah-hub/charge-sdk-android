package de.elvah.charge.platform.network.retrofit.adapter

import arrow.core.Either
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class EitherCallAdapterFactoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val factory = EitherCallAdapterFactory.create(testScope)
    private val retrofit = mockk<Retrofit>()

    @Test
    fun `get returns null when return type is not Call`() {
        val returnType = String::class.java
        val annotations = emptyArray<Annotation>()
        
        val result = factory.get(returnType, annotations, retrofit)
        
        assertNull(result)
    }

    @Test
    fun `get returns null when Call parameter type is not Either`() {
        val returnType = createParameterizedType(Call::class.java, String::class.java)
        val annotations = emptyArray<Annotation>()
        
        val result = factory.get(returnType, annotations, retrofit)
        
        assertNull(result)
    }

    @Test
    fun `get throws exception when Either left side is not Throwable`() {
        val eitherType = createParameterizedType(Either::class.java, String::class.java, String::class.java)
        val returnType = createParameterizedType(Call::class.java, eitherType)
        val annotations = emptyArray<Annotation>()
        
        try {
            factory.get(returnType, annotations, retrofit)
            assert(false) { "Expected IllegalArgumentException" }
        } catch (e: IllegalArgumentException) {
            assert(e.message?.contains("Either left side must be Throwable") == true)
        }
    }

    @Test
    fun `get returns adapter when return type is Call of Either with Throwable left side`() {
        val eitherType = createParameterizedType(Either::class.java, Throwable::class.java, String::class.java)
        val returnType = createParameterizedType(Call::class.java, eitherType)
        val annotations = emptyArray<Annotation>()
        
        val result = factory.get(returnType, annotations, retrofit)
        
        assertNotNull(result)
        assertEquals(String::class.java, result!!.responseType())
    }

    @Test
    fun `create factory with default scope`() {
        val factory = EitherCallAdapterFactory.create()
        
        assertNotNull(factory)
    }

    @Test
    fun `create factory with custom scope`() {
        val customScope = CoroutineScope(Dispatchers.Main)
        val factory = EitherCallAdapterFactory.create(customScope)
        
        assertNotNull(factory)
    }

    private fun createParameterizedType(rawType: Class<*>, vararg typeArguments: Type): ParameterizedType {
        return object : ParameterizedType {
            override fun getActualTypeArguments() = typeArguments
            override fun getRawType() = rawType
            override fun getOwnerType(): Nothing? = null
        }
    }
}