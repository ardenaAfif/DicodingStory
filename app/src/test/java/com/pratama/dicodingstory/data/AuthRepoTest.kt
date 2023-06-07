package com.pratama.dicodingstory.data

import com.pratama.dicodingstory.data.local.AuthPreferencesDataSource
import com.pratama.dicodingstory.data.remote.retrofit.ApiService
import com.pratama.dicodingstory.data.repository.AuthRepo
import com.pratama.dicodingstory.utils.CoroutinesTestRule
import com.pratama.dicodingstory.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AuthRepoTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var preferencesDataSource: AuthPreferencesDataSource

    @Mock
    private lateinit var apiService: ApiService
    private lateinit var authRepo: AuthRepo

    private val dummyName = "Name"
    private val dummyEmail = "mail@mail.com"
    private val dummyPassword = "password"
    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        authRepo = AuthRepo(apiService, preferencesDataSource)
    }

    @Test
    fun `User login successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyLoginResponse()

        `when`(apiService.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        authRepo.userLogin(dummyEmail, dummyPassword).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(expectedResponse, actualResponse)
            }

            result.onFailure {
                Assert.assertNull(it)
            }
        }

    }

    @Test
    fun `User login failed - throw exception`(): Unit = runTest {
        `when`(apiService.userLogin(dummyEmail, dummyPassword)).then { throw Exception() }

        authRepo.userLogin(dummyEmail, dummyPassword).collect { result ->
            Assert.assertFalse(result.isSuccess)
            Assert.assertTrue(result.isFailure)

            result.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun `User register successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyRegisterResponse()

        `when`(apiService.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        authRepo.userRegister(dummyName, dummyEmail, dummyPassword).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(expectedResponse, actualResponse)
            }

            result.onFailure {
                Assert.assertNull(it)
            }
        }
    }

    @Test
    fun `User register failed - throw exception`(): Unit = runTest {
        `when`(
            apiService.userRegister(
                dummyName,
                dummyEmail,
                dummyPassword
            )
        ).then { throw Exception() }

        authRepo.userRegister(dummyName, dummyEmail, dummyPassword).collect { result ->
            Assert.assertFalse(result.isSuccess)
            Assert.assertTrue(result.isFailure)

            result.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun `Save auth token successfully`() = runTest {
        authRepo.saveAuthToken(dummyToken)
        Mockito.verify(preferencesDataSource).saveAuthToken(dummyToken)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        `when`(preferencesDataSource.getAuthToken()).thenReturn(expectedToken)

        authRepo.getAuthToken().collect { actualToken ->
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(preferencesDataSource).getAuthToken()
    }

}