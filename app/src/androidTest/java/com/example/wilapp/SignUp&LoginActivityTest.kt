package com.example.wilapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpLoginActivityTest {

    @Test
    fun emptyEmailValidationShouldFail() {
        val isValidEmail = validateEmail("")
        assertFalse(isValidEmail)
    }

    @Test
    fun invalidEmailFormatValidationShouldFail() {
        val isValidEmail = validateEmail("invalidemail")
        assertFalse(isValidEmail)
    }

    @Test
    fun validEmailFormatValidationShouldPass() {
        val isValidEmail = validateEmail("test@example.com")
        assertTrue(isValidEmail)
    }

    @Test
    fun emptyPasswordValidationShouldFail() {
        val isValidPassword = validatePassword("", "")
        assertFalse(isValidPassword)
    }

    @Test
    fun passwordsNotMatchingValidationShouldFail() {
        val isValidPassword = validatePassword("password", "differentpassword")
        assertFalse(isValidPassword)
    }

    @Test
    fun matchingPasswordsValidationShouldPass() {
        val isValidPassword = validatePassword("ValidPassword123", "ValidPassword123")
        assertTrue(isValidPassword)
    }

    private fun validateEmail(email: String): Boolean {
        val emailPattern = ("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$").toRegex()
        val flag: Boolean = when {
            email.isEmpty() -> false
            !email.matches(emailPattern) -> false
            else -> true
        }
        return flag
    }

    private fun validatePassword(password: String, confirmPassword: String): Boolean {
        val passwordsMatch = password == confirmPassword
        val passwordLengthValid = password.length >= 6 // add your password validation logic

        return passwordsMatch && passwordLengthValid
    }
}
