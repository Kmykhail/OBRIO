package com.kote.obrio.data.cache

import android.graphics.Bitmap
import android.util.LruCache
import timber.log.Timber

const val DEFAULT_MAX_KB = 1440
const val IMAGE_SIZE_KB = 36
const val IMAGE_NUMBERS_IN_CACHE = 20
const val TARGET_MAX_KB = IMAGE_SIZE_KB * IMAGE_NUMBERS_IN_CACHE

object ImageMemoryCache {
    private val lru = object : LruCache<String, Bitmap>(DEFAULT_MAX_KB) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount / 1024
        }
    }

    fun get(url: String): Bitmap? {
        return lru.get(url)
    }

    fun put(url: String, bitmap: Bitmap) {
        if (get(url) == null) {
            checkAndTrimIfNeeded()
            Timber.i("Added new bmp by url:$url")
            lru.put(url, bitmap)
        }
    }

    fun remove(url: String) {
        lru.remove(url)
    }

    private fun checkAndTrimIfNeeded() {
        if (currentSizeKb() > TARGET_MAX_KB) {
            Timber.i("ImageMemoryCache: trimToSize from ${currentSizeKb()} KB -> $TARGET_MAX_KB")
            lru.trimToSize(TARGET_MAX_KB)
            Timber.i("ImageMemoryCache: after trim currentKb=${currentSizeKb()}")
        }
    }

    fun clear() = lru.evictAll()
    fun currentSizeKb(): Int = lru.size()
    fun maxSizeKb(): Int = lru.maxSize()
}