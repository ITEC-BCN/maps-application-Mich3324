package com.example.mapsapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.MyAppSingleton
import com.example.mapsapp.utils.AuthState
import com.example.mapsapp.utils.SharedPreferencesHelper
import kotlinx.coroutines.launch

class AuthViewModel(private val sharedPreferencesHelper: SharedPreferencesHelper):ViewModel() {
    private val authManager = MyAppSingleton.auth
    private val _email = MutableLiveData<String>()
    val email = _email
    private val _password = MutableLiveData<String>()
    val password = _password
    private val _authState = MutableLiveData<AuthState>()
    val authState = _authState
    private val _showError = MutableLiveData<Boolean>(false)
    val showError = _showError
    private val _user = MutableLiveData<String?>()
    val user = _user

    private fun checkExistingSession() {
        viewModelScope.launch {
            val accessToken = sharedPreferencesHelper.getAccessToken()
            val refreshToken = sharedPreferencesHelper.getRefreshToken()
            when {
                !accessToken.isNullOrEmpty() -> refreshToken
                !refreshToken.isNullOrEmpty() -> refreshToken
                else -> _authState.value = AuthState.Unauthenticated
            }
        }
    }

    init {
        checkExistingSession()
    }

    fun editEmail(value: String) {
        _email.value = value
    }

    fun editPassword(value: String) {
        _password.value = value
    }

    fun errorMessageShowed(){
        _showError.value = false
    }

    fun signUp() {
        viewModelScope.launch {
            _authState.value = authManager.signInWithEmail (_email.value!!, _password.value!!)
            if (_authState.value is AuthState.Error) {
                _showError.value = true
            } else {
                val session = authManager.retrieveCurrentSession()
                sharedPreferencesHelper.saveAuthData(
                    session!!.accessToken,
                    session.refreshToken
                )
            }
        }
    }

    fun signIn() {
        viewModelScope.launch {
            _authState.value = authManager.signInWithEmail(_email.value!!, _password.value!!)
            if (_authState.value is AuthState.Error) {
                _showError.value = true
            } else {
                val session = authManager.retrieveCurrentSession()
                sharedPreferencesHelper.saveAuthData(
                    session!!.accessToken,
                    session.refreshToken
                )
            }
        }
    }

    private fun refreshToken() {
        viewModelScope.launch {
            try {
                authManager.refreshSession()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                sharedPreferencesHelper.clear()
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            sharedPreferencesHelper.clear()
            _authState.value = AuthState.Unauthenticated
        }
    }










}