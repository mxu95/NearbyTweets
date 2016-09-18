package com.mxu.nearbytweets;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Michael on 9/18/2016.
 */
public class TwitterHelper extends AsyncTask<Double, Void, List<twitter4j.Status>> {
    private Double latitude;
    private Double longitude;
    private Context context;
    private String consumerKey = "Ugz9oypymd4uoNI5HzNbxBoXC";
    private String consumerSecret = "HepLYiPkuB0WDrkM8v7ppv5doOP4FwSRQhnC6JwsD6Xu9mUZAv";
    private String accessToken = "777546905832349696-yDDYKumDvu7OMXTtYbb2HZJ56iVoorX";
    private String accessSecret = "TTf9tnanK8jk4ScXsHBQA9AGB2WqpCWtTcvvZhPtS7wWg";

    public TwitterHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected List<twitter4j.Status> doInBackground(Double... params) {
        Log.i(MainActivity.TAG, "Starting Twitter Helper");
        latitude = params[0];
        longitude = params[1];
        GeoLocation location = new GeoLocation(latitude, longitude);

        Log.i(MainActivity.TAG, "Creating query");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Query query = new Query();
        query.setGeoCode(location, 100, Query.Unit.mi);
        query.setSince("2016-09-17");
        query.setCount(100);
        QueryResult result = null;
        List<twitter4j.Status> tweets = null;
        try {
            do {
                Log.i(MainActivity.TAG, "Sending twitter query");
                result = twitter.search(query);
                tweets = result.getTweets();
            } while ((query = result.nextQuery()) != null);
        } catch (TwitterException e) {
            e.printStackTrace();
            Log.i(MainActivity.TAG, "Failed to search tweets: " + e.getErrorMessage());
        }
        Log.i(MainActivity.TAG, "Received query results");
        return tweets;
    }

    @Override
    protected void onPostExecute(List<twitter4j.Status> result) {
        Log.i(MainActivity.TAG, "Preparing results for Google Maps API");
        ArrayList<Double> lats = new ArrayList<Double>();
        ArrayList<Double> longs = new ArrayList<Double>();
        ArrayList<String> texts = new ArrayList<String>();
        ArrayList<String> sources = new ArrayList<String>();
        for(twitter4j.Status status : result) {
            if(status.getGeoLocation() != null) {
                lats.add(status.getGeoLocation().getLatitude());
                longs.add(status.getGeoLocation().getLongitude());
                texts.add(status.getText());
                sources.add(status.getUser().getName());
            } else if(status.getPlace() != null) {
                GeoLocation[][] boundingBox = status.getPlace().getBoundingBoxCoordinates();
                double statusLatitude = (boundingBox[0][0].getLatitude() + boundingBox[0][3].getLatitude())/2;
                double statusLongitude = (boundingBox[0][0].getLongitude() + boundingBox[0][1].getLongitude())/2;
                lats.add(statusLatitude);
                longs.add(statusLongitude);
                texts.add(status.getText());
                sources.add(status.getUser().getName());
            }
        }


        Intent intent = new Intent(context, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("texts", convertToStringArray(texts));
        intent.putExtra("sources", convertToStringArray(sources));
        intent.putExtra("latitudes", convertToDoubleArray(lats));
        intent.putExtra("longitudes", convertToDoubleArray(longs));
        intent.putExtra("current-latitude", latitude);
        intent.putExtra("current-longitude", longitude);
        context.startActivity(intent);
    }

    private double[] convertToDoubleArray(ArrayList<Double> in) {
        double[] out = new double[in.size()];
        for(int i = 0; i < in.size(); i++) {
            out[i] = in.get(i);
        }
        return out;
    }

    private String[] convertToStringArray(ArrayList<String> in) {
        String[] out = new String[in.size()];
        for(int i = 0; i < in.size(); i++) {
            out[i] = in.get(i);
        }
        return out;
    }
}
