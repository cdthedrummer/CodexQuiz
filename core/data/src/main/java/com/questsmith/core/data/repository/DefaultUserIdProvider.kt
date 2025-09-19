package com.questsmith.core.data.repository

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserIdProvider @Inject constructor() : UserIdProvider {
    @Volatile
    private var cached: String? = null

    override fun getUserId(): String = cached ?: generate()

    override fun setUserId(value: String) {
        cached = value
    }

    private fun generate(): String {
        val fresh = UUID.randomUUID().toString()
        cached = fresh
        return fresh
    }
}
