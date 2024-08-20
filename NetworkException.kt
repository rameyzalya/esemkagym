package com.example.myesemka.tools

class NetworkException {
    class IgnorableException(msg: String) : Exception(msg)
    class AuthException : Exception ("You are not authorized")
}