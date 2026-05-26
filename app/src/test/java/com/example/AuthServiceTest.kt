package com.example

import com.example.data.api.AuthResult
import com.example.data.api.AuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthServiceTest {

    private val authService = AuthService(api = null)

    @Test
    fun testAuthenticationSuccessForTestUser() = runBlocking {
        val result = authService.authenticateUser("samir@example.com", "password123")
        assertTrue(result is AuthResult.Success)
        val success = result as AuthResult.Success
        assertEquals("samir@example.com", success.email)
        assertEquals("Самир Мусабалиев", success.fullName)
    }

    @Test
    fun testAuthenticationFailureForInvalidPassword() = runBlocking {
        val result = authService.authenticateUser("samir@example.com", "wrongpassword")
        assertTrue(result is AuthResult.Failure)
        val failure = result as AuthResult.Failure
        assertEquals("Неверный пароль. Введите правильные учетные данные.", failure.message)
    }

    @Test
    fun testAuthenticationFailureForInvalidEmail() = runBlocking {
        val result = authService.authenticateUser("", "password123")
        assertTrue(result is AuthResult.Failure)
        val failure = result as AuthResult.Failure
        assertEquals("Email не может быть пустым, а пароль должен быть не менее 6 символов.", failure.message)
    }

    @Test
    fun testAuthenticationFailureForShortPassword() = runBlocking {
        val result = authService.authenticateUser("samir@example.com", "123")
        assertTrue(result is AuthResult.Failure)
        val failure = result as AuthResult.Failure
        assertEquals("Email не может быть пустым, а пароль должен быть не менее 6 символов.", failure.message)
    }

    @Test
    fun testRegisterUserWithBlankName() = runBlocking {
        val result = authService.registerUser("new@example.com", "securepass", "")
        assertTrue(result is AuthResult.Failure)
        val failure = result as AuthResult.Failure
        assertEquals("Укажите ваше имя для доставки контактов.", failure.message)
    }

    @Test
    fun testRegisterUserSuccess() = runBlocking {
        val result = authService.registerUser("new@example.com", "securepass", "Иван Иванов")
        assertTrue(result is AuthResult.Success)
        val success = result as AuthResult.Success
        assertEquals("new@example.com", success.email)
        assertEquals("Иван Иванов", success.fullName)
    }
}
