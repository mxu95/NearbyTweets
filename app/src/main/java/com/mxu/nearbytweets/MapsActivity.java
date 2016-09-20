package com.mxu.nearbytweets;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.api.SearchResource;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(MainActivity.TAG, "Starting Maps Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        ArrayList<LatLng> locations = new ArrayList<LatLng>();
        ArrayList<String> sourcesAndTexts = new ArrayList<String>();

        double[] latitudes = intent.getDoubleArrayExtra("latitudes");
        double[] longitudes = intent.getDoubleArrayExtra("longitudes");
        String[] sources = intent.getStringArrayExtra("sources");
        String[] texts = intent.getStringArrayExtra("texts");

        if(sources.length != texts.length || sources.length != latitudes.length) {
            Log.i(MainActivity.TAG, "Array lengths do not match up!!!!!");
        } else {
            for(int i = 0; i < sources.length; i++) {
                sourcesAndTexts.add(sources[i] + ": " + texts[i]);
                locations.add(new LatLng(latitudes[i], longitudes[i]));
            }
        }

        for(int i = 0; i < locations.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(locations.get(i)).title(sourcesAndTexts.get(i)));
        }

        LatLng currentLocation = new LatLng(intent.getDoubleExtra("current-latitude", 38), intent.getDoubleExtra("current-longitude", -76));
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(7.0f));
    }
}
