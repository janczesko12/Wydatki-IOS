package com.example.wydatki.data

import platform.Foundation.NSUserDefaults

class IosStorage : Storage {
    private val delegate = NSUserDefaults.standardUserDefaults

    override fun getString(key: String): String? = delegate.stringForKey(key)

    override fun putString(key: String, value: String) {
        delegate.setObject(value, forKey = key)
    }

    override fun remove(key: String) {
        delegate.removeObjectForKey(key)
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return if (delegate.objectForKey(key) != null) {
            delegate.boolForKey(key)
        } else {
            default
        }
    }

    override fun putBoolean(key: String, value: Boolean) {
        delegate.setBool(value, forKey = key)
    }
}
