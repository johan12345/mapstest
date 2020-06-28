package com.example.myapplication

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var gen: IconGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        gen = IconGenerator(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        for (j in 0..10) {
            for (i in 0..10) {
                val pos = LatLng(-34.0 + i * 5, 151.0 + j * 5)
                MarkerAnimator(gen, mMap, pos).start()
            }
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private class MarkerAnimator(val gen: IconGenerator, val map: GoogleMap, val position: LatLng) {
        private val handler = Handler()
        private var marker: Marker? = null

        private val removeMarker: Runnable = Runnable {
            val marker = this.marker ?: return@Runnable
            animateMarkerDisappear(marker)
            handler.postDelayed(this.addMarker, 1000)
        }
        private val addMarker: Runnable = Runnable {
            val opts = MarkerOptions().position(position).visible(false)
            val marker = map.addMarker(opts)
            animateMarkerAppear(marker)
            this.marker = marker
            handler.postDelayed(this.removeMarker, 1000)
        }

        fun start() {
            addMarker.run()
        }

        private fun animateMarkerAppear(marker: Marker) {
            val anim = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 250
                interpolator = LinearOutSlowInInterpolator()
                addUpdateListener { animationState ->
                    val scale = animationState.animatedValue as Float
                    marker.setIcon(gen.getBitmapDescriptor(scale))
                    marker.isVisible = true
                }
            }
            anim.start()
        }

        fun animateMarkerDisappear(marker: Marker) {
            val anim = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = 200
                interpolator = FastOutLinearInInterpolator()
                addUpdateListener { animationState ->
                    val scale = animationState.animatedValue as Float
                    marker.setIcon(gen.getBitmapDescriptor(scale))
                }
                addListener(onEnd = {
                    marker.remove()
                }, onCancel = {
                    marker.remove()
                })
            }
            anim.start()
        }
    }


}