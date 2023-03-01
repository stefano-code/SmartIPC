package com.android.smartipc.imcp

import android.content.ContentProviderClient
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlin.collections.HashMap

// "content://com.technogym.android.visiowow.bt.AUTHORITY/item"


    open class IMCPProxy(val ctx : Context, val uri : Uri) :
                ContentObserver(Handler(Looper.getMainLooper()))
    {
        private val cp : ContentProviderClient?
        private var cache : HashMap<String, String>

        init {
            Log.e("TestProxy","uri " + uri)
            cp = ctx.contentResolver.acquireContentProviderClient(uri)
            ctx.contentResolver.registerContentObserver(uri, true, this)
            cache = HashMap<String, String>()
        }

        override fun onChange(selfChange: Boolean) {
            refresh()
        }

        private fun refresh() {
            val c = cp?.query(uri, null, null, null, null)
            if(c != null)
            {
                if(c.moveToFirst())
                {
                    val cn = c.columnCount
                    for(i in 0 until cn)
                    {
                        val k = c.getColumnName(i) ?: ""
                        val o = c. getString(i) ?: ""
                        if(!cache.containsKey(k) || cache[k] != o)
                        {
                            cache[k] = o
                        }
                    }
                }
                c.close()
            }
        }

        @Synchronized
        fun getString(fieldCode: String): String {
            return cache[fieldCode] ?: ""
        }

        @Synchronized
        fun getInt(fieldCode: String): Int {
            return cache[fieldCode]?.toInt() ?: -1
        }

        @Synchronized
        fun getDouble(fieldCode: String): Double {
            return cache[fieldCode]?.toDouble() ?: -1.0
        }

        @Synchronized
        fun set(fieldCode: String, value: String) {
            val cv = ContentValues()
            cv.put(fieldCode, value)
            cp?.update(uri, cv,null, null)
        }

        @Synchronized
        fun set(fieldCode: String, value: Int) {
            val cv = ContentValues()
            cv.put(fieldCode, value)
            cp?.update(uri, cv,null, null)
        }

        @Synchronized
        fun set(fieldCode: String, value: Double) {
            val cv = ContentValues()
            cv.put(fieldCode, value)
            cp?.update(uri, cv,null, null)
        }

        @Synchronized
        fun tearDown() {
            cache.clear()
            cp?.release()
            ctx.contentResolver.unregisterContentObserver(this)
        }
    }

