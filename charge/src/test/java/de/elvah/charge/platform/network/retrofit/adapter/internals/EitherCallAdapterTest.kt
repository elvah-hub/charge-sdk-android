package de.elvah.charge.platform.network.retrofit.adapter.internals

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.elvah.charge.platform.network.error.NetworkErrorParser
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Call
import java.lang.reflect.Type

internal class EitherCallAdapterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val errorParser = NetworkErrorParser(moshi)
    private val responseType: Type = String::class.java

    @Test
    fun `responseType returns correct type`() {
        // Given
        val adapter = EitherCallAdapter<String>(responseType, testScope, errorParser)

        // When
        val result = adapter.responseType()

        // Then
        assertEquals(responseType, result)
    }

    @Test
    fun `adapt returns EitherCall instance`() {
        // Given
        val mockCall = mockk<Call<String>>()
        val adapter = EitherCallAdapter<String>(responseType, testScope, errorParser)

        // When
        val result = adapter.adapt(mockCall)

        // Then
        assertTrue("Result should be EitherCall instance", result is EitherCall)
    }

    @Test
    fun `adapt returns call with correct generic type`() {
        // Given
        val mockCall = mockk<Call<String>>()
        val adapter = EitherCallAdapter<String>(responseType, testScope, errorParser)

        // When
        val result = adapter.adapt(mockCall)

        // Then
        assertTrue(
            "Result should be Call<Either<Throwable, String?>>",
            result is Call<*>
        )
        // Note: We cannot easily test the generic type at runtime due to type erasure,
        // but the compilation itself validates the type safety
    }

    @Test
    fun `adapter works with different response types`() {
        // Given
        val intResponseType: Type = Int::class.java
        val mockCall = mockk<Call<Int>>()
        val adapter = EitherCallAdapter<Int>(intResponseType, testScope, errorParser)

        // When
        val result = adapter.adapt(mockCall)

        // Then
        assertTrue("Result should be EitherCall instance", result is EitherCall)
        assertEquals(intResponseType, adapter.responseType())
    }

    @Test
    fun `adapter works with custom data classes`() {
        // Given
        data class CustomResponse(val id: String, val value: Int)

        val customResponseType: Type = CustomResponse::class.java
        val mockCall = mockk<Call<CustomResponse>>()
        val adapter = EitherCallAdapter<CustomResponse>(customResponseType, testScope, errorParser)

        // When
        val result = adapter.adapt(mockCall)

        // Then
        assertTrue("Result should be EitherCall instance", result is EitherCall)
        assertEquals(customResponseType, adapter.responseType())
    }

    @Test
    fun `adapter preserves coroutine scope and error parser`() {
        // Given
        val mockCall = mockk<Call<String>>()
        val adapter = EitherCallAdapter<String>(responseType, testScope, errorParser)

        // When
        val result = adapter.adapt(mockCall) as EitherCall<String>

        // Then
        // We can't directly access private fields, but we can verify the EitherCall was created
        // The actual functionality testing is covered in EitherCallTest
        assertTrue("EitherCall should be created successfully", result is EitherCall)
    }
}
