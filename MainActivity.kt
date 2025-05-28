package com.example.streetview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaFragment
import android.graphics.Point;

import android.os.PersistableBundle;


import android.util.Log;
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation

class MainActivity : AppCompatActivity(), OnStreetViewPanoramaReadyCallback {

    private var streetViewPanorama: StreetViewPanorama? = null
    private var streetViewPanoramaFragment: StreetViewPanoramaFragment? = null
    private var streetViewPanoramaChangeListener: StreetViewPanorama.OnStreetViewPanoramaChangeListener? =
        StreetViewPanorama.OnStreetViewPanoramaChangeListener {
            Log.e(
                MainActivity.Companion.TAG,
                "Street View Panorama Change Listener"
            )
        }
    private var streetViewPanoramaClickListener: StreetViewPanorama.OnStreetViewPanoramaClickListener? =
        StreetViewPanorama.OnStreetViewPanoramaClickListener { orientation: StreetViewPanoramaOrientation? ->
            val point: Point? = streetViewPanorama!!.orientationToPoint(orientation)
            if (point != null) {
                streetViewPanorama!!.animateTo(
                    StreetViewPanoramaCamera.Builder()
                        .orientation(orientation)
                        .zoom(streetViewPanorama!!.getPanoramaCamera().zoom)
                        .build(), MainActivity.Companion.PANORAMA_CAMERA_DURATION.toLong()
                )
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        streetViewPanoramaFragment = fragmentManager
            .findFragmentById(R.id.streetViewMap) as StreetViewPanoramaFragment
        streetViewPanoramaFragment!!.getStreetViewPanoramaAsync(this)
        var streetViewBundle: Bundle? = null
        if (savedInstanceState != null) streetViewBundle =
            savedInstanceState.getBundle(STREET_VIEW_BUNDLE)
        streetViewPanoramaFragment!!.onCreate(streetViewBundle)
    }
    override fun onResume() {
        super.onResume()
        streetViewPanoramaFragment!!.onResume()
    }
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        var mStreetViewBundle = outState.getBundle(STREET_VIEW_BUNDLE)
        if (mStreetViewBundle == null) {
            mStreetViewBundle = Bundle()
            outState.putBundle(STREET_VIEW_BUNDLE, mStreetViewBundle)
        }
        streetViewPanoramaFragment!!.onSaveInstanceState(mStreetViewBundle)
    }
    override fun onStreetViewPanoramaReady(streetViewPanorama: StreetViewPanorama) {
        this.streetViewPanorama = streetViewPanorama
        this.streetViewPanorama!!.setPosition(LatLng(17.4364,78.3877))
        this.streetViewPanorama!!.setOnStreetViewPanoramaChangeListener(
            streetViewPanoramaChangeListener
        )
        this.streetViewPanorama!!.setOnStreetViewPanoramaClickListener(
            streetViewPanoramaClickListener
        )
    }
    override fun onStop() {
        super.onStop()
        streetViewPanoramaFragment!!.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (streetViewPanoramaFragment != null) streetViewPanoramaFragment!!.onDestroy()
        streetViewPanoramaChangeListener = null
        streetViewPanoramaClickListener = null
        streetViewPanorama = null
    }
    companion object {
        private val PANORAMA_CAMERA_DURATION = 1000
        val TAG = MainActivity::class.java.simpleName
        private val STREET_VIEW_BUNDLE = "StreetViewBundle"
    }

}