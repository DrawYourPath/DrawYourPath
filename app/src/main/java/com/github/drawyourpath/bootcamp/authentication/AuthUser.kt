package com.github.drawyourpath.bootcamp.authentication

interface AuthUser {
    fun getEmail(): String
    fun isAnonymous(): Boolean
    fun getUid(): String
    fun getPhoneNumber(): String
    fun getDisplayName(): String
}