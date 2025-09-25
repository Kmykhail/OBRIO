package com.kote.obrio.data.cache

import android.graphics.Bitmap
import android.util.LruCache
import timber.log.Timber

object ImageMemoryCache {
    private val maxMemoryKb: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSizeKb = maxMemoryKb / 8

    private val lru = object : LruCache<String, Bitmap>(cacheSizeKb) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount / 1024
        }
    }

    fun get(url: String): Bitmap? {
        return lru.get(url)
    }

    fun put(url: String, bitmap: Bitmap) {
        if (get(url) == null) {
            Timber.i("Added new bmp by url:$url")
            lru.put(url, bitmap)
        }
    }

    fun remove(url: String) {
        lru.remove(url)
    }

    fun clear() = lru.evictAll()
}