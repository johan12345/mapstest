package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.LruCache
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.math.round

class IconGenerator(val context: Context) {
    val icon = R.drawable.ic_map_marker
    val cacheSize = 32 * 1024 * 1024; // 32MiB
    val cache = object : LruCache<Float, Bitmap>(cacheSize) {
        override fun sizeOf(key: Float, value: Bitmap): Int {
            return value.byteCount
        }
    }

    fun getBitmapDescriptor(scale: Float = 1f): BitmapDescriptor? {
        val bitmap = generateBitmap(scale)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun generateBitmap(scale: Float): Bitmap {
        val scaleRounded = round(scale * 20) / 20  // restrict to 20 steps for better caching

        if (cache[scaleRounded] != null) {
            return cache[scaleRounded]
        } else {
            val vd: Drawable = context.getDrawable(icon)!!
            vd.setBounds(
                0, 0,
                vd.intrinsicWidth,
                vd.intrinsicHeight
            )

            val bm = Bitmap.createBitmap(
                (vd.intrinsicWidth).toInt(), (vd.intrinsicHeight).toInt(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bm)
            canvas.scale(
                scaleRounded,
                scaleRounded,
                vd.intrinsicWidth / 2f,
                vd.intrinsicHeight.toFloat()
            )
            vd.draw(canvas)
            cache.put(scaleRounded, bm)
            return bm
        }
    }
}