package com.pratama.dicodingstory.ui.login

import com.pratama.dicodingstory.data.repository.AuthRepo
import com.pratama.dicodingstory.data.remote.response.LoginResponse
import com.pratama.dicodingstory.utils.CoroutinesTestRule
import com.pratama.dicodingstory.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var authRepo: AuthRepo
    private lateinit var loginViewModel: LoginViewModel

    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyToken = "authentication_token"
    private val dummyEmail = "email@mail.com"
    private val dummyPassword = "password"

    @Before
//    fun setup() {
//        loginViewModel = LoginViewModel(authRepo)
//    }

    @Test
    fun `Login successfully - result success`(): Unit = runTest {
        val expectedResponse = flow {
            emit(Result.success(dummyLoginResponse))
        }

        `when`(loginViewModel.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        loginViewModel.userLogin(dummyEmail, dummyPassword).collect { result ->

            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertSame(dummyLoginResponse, actualResponse)
            }
        }

        Mockito.verify(authRepo).userLogin(dummyEmail, dummyPassword)
    }

    @Test
    fun `Login failed - result failure with exception`(): Unit = runTest {
        val expectedResponse: Flow<Result<LoginResponse>> =
            flowOf(Result.failure(Exception("login failed")))

        `when`(loginViewModel.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        loginViewModel.userLogin(dummyEmail, dummyPassword).collect { result ->

            Assert.assertFalse(result.isSuccess)
            Assert.assertTrue(result.isFailure)

            result.onFailure {
                Assert.assertNotNull(it)
            }
        }

        Mockito.verify(authRepo).userLogin(dummyEmail, dummyPassword)
    }

    @Test
    fun `Save authentication token successfully`(): Unit = runTest {
        loginViewModel.saveAuthToken(dummyToken)
        Mockito.verify(authRepo).saveAuthToken(dummyToken)
    }
}